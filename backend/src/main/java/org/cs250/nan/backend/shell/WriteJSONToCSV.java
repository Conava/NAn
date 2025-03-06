package org.cs250.nan.backend.shell;

import org.json.JSONObject;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class WriteJSONToCSV {
    public static void writeJsonListToCsv(List<JSONObject> jsonList, String filePath) throws IOException {
        if (jsonList.isEmpty()) {
            System.out.println("Empty JSON list, nothing to write.");
            return;
        }

        // Extract headers (keys) from the first JSON object
        Set<String> headers = jsonList.get(0).keySet();

        // Open CSV file writer
        try (FileWriter writer = new FileWriter(filePath)) {
            // Write headers
            writer.append(String.join(",", headers));
            writer.append("\n");

            // Write data rows
            for (JSONObject json : jsonList) {
                List<String> row = new ArrayList<>();
                for (String header : headers) {
                    row.add(json.optString(header, "")); // Handle missing keys
                }
                writer.append(String.join(",", row));
                writer.append("\n");
            }

            System.out.println("CSV file created successfully: " + filePath);
        }
    }

//    public static void main(String[] args) {
////        List<JSONObject> jsonList = List.of(
////                new JSONObject().put("name", "Alice").put("age", 25).put("city", "New York"),
////                new JSONObject().put("name", "Bob").put("age", 30).put("city", "San Francisco"),
////                new JSONObject().put("name", "Charlie").put("age", 35).put("city", "Los Angeles")
////        );
//
//        String gpsData = "$GPGSA,A,3,29,05,18,23,25,15,13,26,20,12,,,1.13,0.79,0.81*04\n" +
//                "$GPRMC,015455.000,A,3244.8110,N,11707.5409,W,0.11,218.56,260225,,,D*71\n" +
//                "$GPGGA,015456.000,3244.8109,N,11707.5409,W,2,10,0.79,128.5,M,-35.8,M,,0000*55"; //hard coded sample data, for testing only
//
//        ParseWiFiDataWindows wifiParser = new ParseWiFiDataWindows(); //create instance of ParseWiFiDataWindows to provide input into jsonList parameter of mergeJSONObjects method
//        ParseGPSData gpsParser = new ParseGPSData(); //create instance of ParseGPSData to parse the included string above (String gpsData) into the correct json format (String gpsData)
//        List<JSONObject> mergedJSONObjectList = wifiParser.parseStringToListOfJSON(); //using the parseStringToListOfJSON() method from class ParseWiFiDataWindows, generate the list of WiFi json objects from the sample data included in ParseWiFiDataWindows for testing purposes only
//        MergeJSONData.mergeJSONObjects(mergedJSONObjectList, gpsParser.parseStringToListOfJSON(gpsData)); //add the gps data to each WiFi json object in mergedJSONObjectList, the return is not used in this case, but rather the variable mergedJSONObjectList, provided as the first argument since it is modified by the method
//
////        for (JSONObject jsonObj : mergedJSONObjectList) { //display the final JSON object
////            // Pretty print with an indentation factor of 4 spaces
////            System.out.println(jsonObj.toString(4));
////        }
//
//        try {
//            writeJsonListToCsv(mergedJSONObjectList, "output.csv");
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }
}
