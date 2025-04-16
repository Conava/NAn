package org.cs250.nan.backend.service;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Implementation of the {@link KmlGeneratorService}.
 * Generates a KML file from provided JSON data.
 */
@Service
public class KmlGeneratorServiceImpl implements org.cs250.nan.backend.service.KmlGeneratorService {

    private static final Logger LOGGER = LoggerFactory.getLogger(KmlGeneratorServiceImpl.class);
    private static final int DMS_MIN_LENGTH = 7;

    /**
     * Generates a KML file from a list of JSONObjects.
     *
     * @param jsonObjects  the list of JSONObjects with geospatial data
     * @param baseFileName the file name base for the output file (a timestamp and extension will be added)
     */
    @Override
    public void generateKml(List<JSONObject> jsonObjects, String baseFileName) {
        String fileName = createTimestampedFileName(baseFileName);
        StringBuilder kmlBuilder = new StringBuilder();

        // Start KML structure
        kmlBuilder.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
        kmlBuilder.append("<kml xmlns=\"http://www.opengis.net/kml/2.2\">\n");
        kmlBuilder.append("<Document>\n");

        int counter = 0;
        for (JSONObject jsonObject : jsonObjects) {
            if (hasValidGeoData(jsonObject)) {
                appendPlacemark(kmlBuilder, jsonObject);
                counter++;
            }
        }

        // Close KML structure
        kmlBuilder.append("</Document>\n");
        kmlBuilder.append("</kml>\n");

        // Write to file if at least one valid placemark is found
        if (counter > 0) {
            try (FileWriter writer = new FileWriter(fileName)) {
                writer.write(kmlBuilder.toString());
                LOGGER.info("KML file successfully created at: {}", fileName);
            } catch (IOException e) {
                LOGGER.error("Error writing KML file: {}", e.getMessage());
            }
        } else {
            LOGGER.info("KML file was not created: no geospatial data found.");
        }
    }

    /**
     * Appends a KML Placemark element to the provided StringBuilder.
     *
     * @param kmlBuilder the StringBuilder containing KML content
     * @param jsonObject the JSON object with placemark data
     */
    private void appendPlacemark(StringBuilder kmlBuilder, JSONObject jsonObject) {
        double latitude = convertToDecimal(
                jsonObject.getString("latitude"),
                jsonObject.getString("northSouth")
        );
        double longitude = convertToDecimal(
                jsonObject.getString("longitude"),
                jsonObject.getString("eastWest")
        );
        double altitude = jsonObject.optDouble("altitude", 0.0);
        String ssid = jsonObject.optString("SSID", "Unknown Network");
        String signal = jsonObject.optString("Signal", "N/A");
        String radioType = jsonObject.optString("Radio type", "Unknown");
        String date = jsonObject.optString("date", "Unknown Date");
        String utcTime = jsonObject.optString("utcTime", "Unknown Time");
        String course = jsonObject.optString("courseOverGrnd", "N/A");
        String speed = jsonObject.optString("spdOverGrnd", "N/A");

        kmlBuilder.append("<Placemark>\n");
        kmlBuilder.append("  <name>").append(escapeSpecialXMLChars(ssid)).append("</name>\n");
        kmlBuilder.append("  <description>\n");
        kmlBuilder.append("    Signal Strength: ").append(signal).append("\n");
        kmlBuilder.append("    Radio Type: ").append(radioType).append("\n");
        kmlBuilder.append("    Date/Time: ").append(date).append(" UTC ").append(utcTime).append("\n");
        kmlBuilder.append("    Course Over Ground: ").append(course).append("\n");
        kmlBuilder.append("    Speed Over Ground: ").append(speed).append(" knots\n");
        kmlBuilder.append("  </description>\n");
        kmlBuilder.append("  <Point>\n");
        kmlBuilder.append("    <coordinates>")
                .append(longitude).append(",")
                .append(latitude).append(",")
                .append(altitude)
                .append("</coordinates>\n");
        kmlBuilder.append("  </Point>\n");
        kmlBuilder.append("</Placemark>\n");
    }

    /**
     * Validates that the JSON object contains the required geospatial properties.
     *
     * @param jsonObject the JSON object to check
     * @return {@code true} if the object has valid geospatial data, {@code false} otherwise
     */
    private boolean hasValidGeoData(JSONObject jsonObject) {
        return jsonObject.has("latitude") && jsonObject.has("longitude") &&
                !jsonObject.getString("latitude").isEmpty() &&
                !jsonObject.getString("longitude").isEmpty() &&
                !jsonObject.optString("SSID", "").contains("<");
    }

    /**
     * Converts a DMS (degrees and minutes) value into decimal degrees.
     *
     * @param dms       the DMS string (e.g., "3244.8109")
     * @param direction the direction indicator ("N", "S", "E", "W")
     * @return the decimal degree representation
     */
    private double convertToDecimal(String dms, String direction) {
        try {
            int dmsLength = dms.length();
            double degrees = Double.parseDouble(dms.substring(0, dmsLength - DMS_MIN_LENGTH));
            double minutes = Double.parseDouble(dms.substring(dmsLength - DMS_MIN_LENGTH));
            double decimal = degrees + (minutes / 60.0);
            if ("S".equalsIgnoreCase(direction) || "W".equalsIgnoreCase(direction)) {
                decimal = -decimal;
            }
            return decimal;
        } catch (Exception e) {
            LOGGER.error("Error converting coordinate {} to decimal format: {}", dms, e.getMessage());
            return 0.0;
        }
    }

    /**
     * Escapes special XML characters in the SSID by wrapping it in a CDATA section.
     *
     * @param ssid the SSID string to escape
     * @return the escaped SSID string
     */
    private String escapeSpecialXMLChars(String ssid) {
        if (ssid == null) {
            return null;
        }
        return "<![CDATA[" + ssid + "]]>";
    }

    /**
     * Creates a timestamped file name with the .kml extension.
     *
     * @param baseFileName the base file name
     * @return the final file name including the timestamp and extension
     */
    private String createTimestampedFileName(String baseFileName) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss");
        String currentDateTime = sdf.format(new Date());
        return currentDateTime + "_" + baseFileName + ".kml";
    }
}