package org.cs250.nan.backend.shell;

import org.json.JSONObject;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public class WriteJSONToKML {

    public static void writeJSONToKML(List<JSONObject> jsonObjects, String filePath) {
        StringBuilder kmlBuilder = new StringBuilder();

        // Start KML structure
        kmlBuilder.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
        kmlBuilder.append("<kml xmlns=\"http://www.opengis.net/kml/2.2\">\n");
        kmlBuilder.append("<Document>\n");

        for (JSONObject jsonObject : jsonObjects) {
            if (jsonObject.has("latitude") && jsonObject.has("longitude")) {
                double latitude = convertToDecimal(jsonObject.getString("latitude"), jsonObject.getString("northSouth"));
                double longitude = convertToDecimal(jsonObject.getString("longitude"), jsonObject.getString("eastWest"));
                double altitude = jsonObject.optDouble("altitude", 0.0);
                String ssid = jsonObject.optString("SSID", "Unknown Network");
                String signal = jsonObject.optString("Signal", "N/A");
                String radioType = jsonObject.optString("Radio type", "Unknown");
                String date = jsonObject.optString("date", "Unknown Date");
                String utcTime = jsonObject.optString("utcTime", "Unknown Time");
                String course = jsonObject.optString("courseOverGrnd", "N/A");
                String speed = jsonObject.optString("spdOverGrnd", "N/A");

                // Create Placemark
                kmlBuilder.append("<Placemark>\n");
                kmlBuilder.append("<name>").append(ssid).append("</name>\n");
                kmlBuilder.append("<description>\n");
                kmlBuilder.append("Signal Strength: ").append(signal).append("\n");
                kmlBuilder.append("Radio Type: ").append(radioType).append("\n");
                kmlBuilder.append("Date/Time: ").append(date).append(" UTC ").append(utcTime).append("\n");
                kmlBuilder.append("Course Over Ground: ").append(course).append("\n");
                kmlBuilder.append("Speed Over Ground: ").append(speed).append(" knots\n");
                kmlBuilder.append("</description>\n");
                kmlBuilder.append("<Point>\n");
                kmlBuilder.append("<coordinates>")
                        .append(longitude).append(",")
                        .append(latitude).append(",")
                        .append(altitude)
                        .append("</coordinates>\n");
                kmlBuilder.append("</Point>\n");
                kmlBuilder.append("</Placemark>\n");
            }
        }

        // Close KML structure
        kmlBuilder.append("</Document>\n");
        kmlBuilder.append("</kml>\n");

        // Write to file
        try (FileWriter writer = new FileWriter(filePath)) {
            writer.write(kmlBuilder.toString());
            System.out.println("KML file successfully created at: " + filePath);
        } catch (IOException e) {
            System.err.println("Error writing KML file: " + e.getMessage());
        }
    }

    // Converts "3244.8109" N/S format to decimal degrees needed for .kml file
    private static double convertToDecimal(String dms, String direction) {
        double degrees = Double.parseDouble(dms.substring(0, dms.length() - 7));  // Extract degrees
        double minutes = Double.parseDouble(dms.substring(dms.length() - 7));  // Extract minutes
        double decimal = degrees + (minutes / 60.0);

        // Apply negative for South or West
        if ("S".equalsIgnoreCase(direction) || "W".equalsIgnoreCase(direction)) {
            decimal = -decimal;
        }
        return decimal;
    }

    public static void main(String[] args) {
        String gpsData = "$GPGSA,A,3,29,05,18,23,25,15,13,26,20,12,,,1.13,0.79,0.81*04\n" +
                "$GPRMC,015455.000,A,3244.8110,N,11707.5409,W,0.11,218.56,260225,,,D*71\n" +
                "$GPGGA,015456.000,3244.8109,N,11707.5409,W,2,10,0.79,128.5,M,-35.8,M,,0000*55"; //hard coded sample data, for testing only

        ParseWiFiDataWindows wifiParser = new ParseWiFiDataWindows(); //create instance of ParseWiFiDataWindows to provide input into jsonList parameter of mergeJSONObjects method
        ParseGPSData gpsParser = new ParseGPSData(); //create instance of ParseGPSData to parse the included string above (String gpsData) into the correct json format (String gpsData)
        List<JSONObject> mergedJSONObjectList = wifiParser.parseStringToListOfJSON(); //using the parseStringToListOfJSON() method from class ParseWiFiDataWindows, generate the list of WiFi json objects from the sample data included in ParseWiFiDataWindows for testing purposes only
        MergeJSONData.mergeJSONObjects(mergedJSONObjectList, gpsParser.parseStringToListOfJSON(gpsData)); //add the gps data to each WiFi json object in mergedJSONObjectList, the return is not used in this case, but rather the variable mergedJSONObjectList, provided as the first argument since it is modified by the method

        writeJSONToKML(mergedJSONObjectList, "output2.kml");

    }

}