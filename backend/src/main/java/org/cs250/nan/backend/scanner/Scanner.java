package org.cs250.nan.backend.scanner;

import org.cs250.nan.backend.parser.GPSDataParser;
import org.cs250.nan.backend.parser.WiFiDataParser;
import org.cs250.nan.backend.service.MergeJSONData;
import org.cs250.nan.backend.service.WriteJSONfile;
import org.cs250.nan.backend.service.WriteJSONToCSV;
import org.cs250.nan.backend.service.WriteJSONToKML;
import org.cs250.nan.backend.database.SaveToMongoDB;
import org.cs250.nan.backend.database.SpringContext;
import org.cs250.nan.backend.database.MongoConnectionChecker;
import org.json.JSONObject;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * The Scanner class performs a single scan for WiFi and (optionally) GPS data using
 * unified scanner and parser implementations.
 *
 * <p>
 * This class delegates the scan to {@code WiFiScanner} and {@code GPSScanner} classes,
 * then parses the raw data using the unified parser classes {@code WiFiDataParser} and
 * {@code GPSDataParser}. If both WiFi and GPS data are available, the results are merged.
 * Optionally, the aggregated results can be output to KML or CSV files.
 * </p>
 *
 * <p>
 * This implementation adheres to best practices for Java and Spring Boot, ensuring the code
 * is clean, organized, and easy to maintain.
 * </p>
 *
 * @see WiFiScanner
 * @see GPSScanner
 * @see WiFiDataParser
 * @see GPSDataParser
 */
@Component
public class Scanner {

    /**
     * Performs a single scan for WiFi data and, optionally, GPS data.
     *
     * <p>
     * The method invokes {@code WiFiScanner.scan()} to obtain raw WiFi data and parses it using
     * {@code WiFiDataParser.parseStringToListOfJSON}. If GPS scanning is enabled, it similarly calls
     * {@code GPSScanner.scan()} and parses the result with {@code GPSDataParser.parseStringToJSON},
     * then merges the two results.
     * </p>
     *
     * <p>
     * If enabled, the scan results are written to KML and/or CSV files.
     * </p>
     *
     * @param gpsOn       whether to perform a GPS scan
     * @param kmlOutput   whether to output a KML file
     * @param csvOutput   whether to output a CSV file
     * @param kmlFileName the file name for the KML output
     * @param csvFileName the file name for the CSV output
     * @return a list of JSON objects representing the scan results
     * @throws IOException if an error occurs during scanning
     */
    public static List<JSONObject> scan(boolean gpsOn, boolean kmlOutput, boolean csvOutput, String jsonFileName, String kmlFileName, String csvFileName) throws IOException {
        // Aggregate scan results
        List<JSONObject> collectedScans = new ArrayList<>();

        // Obtain the raw WiFi scan data using the unified WiFiScanner
        WiFiScanner wifiScanner = new WiFiScanner();
        String wifiScanResult = wifiScanner.scan();

        // Ensure the WiFi scan returned valid data
        if (wifiScanResult == null || wifiScanResult.trim().isEmpty()) {
            System.out.println("WiFi scan returned an empty result.");
            return collectedScans;
        }

        // Parse the raw WiFi scan data using the unified WiFi data parser
        List<JSONObject> wifiParsedResults = WiFiDataParser.parseStringToListOfJSON(wifiScanResult);

        if (gpsOn) {
            // Obtain the raw GPS scan result using the unified GPSScanner
            GPSScanner gpsScanner = new GPSScanner();
            String gpsScanResult = gpsScanner.scan();

            // If valid GPS data is obtained, parse and merge with WiFi scan results
            if (gpsScanResult != null && !gpsScanResult.trim().isEmpty()) {
                JSONObject gpsParsedResult = GPSDataParser.parseStringToJSON(gpsScanResult);
                collectedScans.addAll(MergeJSONData.mergeJSONObjects(wifiParsedResults, gpsParsedResult));
            } else {
                // If no GPS data, use only the WiFi scan results
                collectedScans.addAll(wifiParsedResults);
            }
        } else {
            // GPS scanning not required, so use WiFi scan results directly
            collectedScans.addAll(wifiParsedResults);
        }

        // If connected to MongoDB, write Each JSON object from the single scan to MongoDB
        MongoConnectionChecker checker = SpringContext.getBean(MongoConnectionChecker.class);
        boolean mongoOk = checker.isConnected();

        if (mongoOk) {
            SaveToMongoDB mongoSaver = SpringContext.getBean(SaveToMongoDB.class);
            for (JSONObject scan : collectedScans) {
                mongoSaver.insertJSONObject(scan);
            }
        }

        // Write the results to a JSON file
        WriteJSONfile.writeJSONfile(collectedScans, jsonFileName);

        // Optionally write the results to a KML file
        if (kmlOutput) {
            WriteJSONToKML.writeJSONToKML(collectedScans, kmlFileName);
        }
        // Optionally write the results to a CSV file
        if (csvOutput) {
            WriteJSONToCSV.writeJsonListToCsv(collectedScans, csvFileName);
        }

        return collectedScans;
    }

    public static void main(String[] args) {



    }
}