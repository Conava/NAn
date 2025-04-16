package org.cs250.nan.backend.scanner;

import org.cs250.nan.backend.parser.GPSDataParser;
import org.cs250.nan.backend.parser.WiFiDataParser;
import org.cs250.nan.backend.service.CsvWriterService;
import org.cs250.nan.backend.service.KmlGeneratorService;
import org.cs250.nan.backend.service.MergeJsonDataService;
import org.cs250.nan.backend.service.WriteJSONfile;
import org.cs250.nan.backend.database.SaveToMongoDB;
import org.cs250.nan.backend.database.SpringContext;
import org.cs250.nan.backend.database.MongoConnectionChecker;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * The Scanner class performs a single scan for WiFi and optional GPS data.
 *
 * <p>
 * This implementation delegates fetching and parsing of WiFi and GPS scan data,
 * then merges the results. If enabled, it outputs the aggregated results into a
 * KML file (using the KML generator service) and/or a CSV file.
 * </p>
 */
@Component
public class Scanner {

    private final KmlGeneratorService kmlGeneratorService;
    private final CsvWriterService csvWriterService;
    private final MergeJsonDataService mergeJsonDataService;

    @Autowired
    public Scanner(KmlGeneratorService kmlGeneratorService, CsvWriterService csvWriterService, MergeJsonDataService mergeJsonDataService) {
        this.kmlGeneratorService = kmlGeneratorService;
        this.csvWriterService = csvWriterService;
        this.mergeJsonDataService = mergeJsonDataService;
    }

    /**
     * Performs the data scan for WiFi and optional GPS data.
     *
     * @param gpsOn       whether to perform a GPS scan
     * @param kmlOutput   whether to output to a KML file
     * @param csvOutput   whether to output to a CSV file
     * @param kmlFileName the file name for the KML output (without extension)
     * @param csvFileName the file name for the CSV output (without extension)
     * @return a list of JSON objects representing the merged scan results
     * @throws IOException if an error occurs during scanning or file writing
     */
    public List<JSONObject> scan(boolean gpsOn, boolean kmlOutput, boolean csvOutput, String jsonFileName, String kmlFileName, String csvFileName)
            throws IOException {
        List<JSONObject> collectedScans = new ArrayList<>();

        // Get WiFi scan data
        WiFiScanner wifiScanner = new WiFiScanner();
        String wifiScanResult = wifiScanner.scan();

        if (wifiScanResult == null || wifiScanResult.trim().isEmpty()) {
            System.out.println("WiFi scan returned an empty result.");
            return collectedScans;
        }

        // Parse WiFi scan results
        List<JSONObject> wifiParsedResults = WiFiDataParser.parseStringToListOfJSON(wifiScanResult);

        // If GPS scanning is enabled, merge GPS data with WiFi results
        if (gpsOn) {
            GPSScanner gpsScanner = new GPSScanner();
            String gpsScanResult = gpsScanner.scan();

            if (gpsScanResult != null && !gpsScanResult.trim().isEmpty()) {
                JSONObject gpsParsedResult = GPSDataParser.parseStringToJSON(gpsScanResult);
                collectedScans.addAll(mergeJsonDataService.mergeJSONObjects(wifiParsedResults, gpsParsedResult));
            } else {
                collectedScans.addAll(wifiParsedResults);
            }
        } else {
            collectedScans.addAll(wifiParsedResults);
        }

        // Output KML file using the injected KML generator service
        // If connected to MongoDB, write Each JSON object from the single scan to MongoDB
        MongoConnectionChecker checker = SpringContext.getBean(MongoConnectionChecker.class);
        boolean mongoOk = checker.isConnected();

        if (mongoOk) {
            SaveToMongoDB mongoSaver = SpringContext.getBean(SaveToMongoDB.class);
            for (JSONObject scan : collectedScans) {
                mongoSaver.insertJSONObject(scan);
            }
        } else {
            System.out.println("MongoDB connection failed: " + mongoOk);
            System.out.println("Saving locally as a .json file for future upload...");
            WriteJSONfile.writeJSONfile(collectedScans, "ScanDataPendingUploadToDB");
        }

        // Write the results to a JSON file
        WriteJSONfile.writeJSONfile(collectedScans, jsonFileName);

        // Optionally write the results to a KML file
        if (kmlOutput) {
            kmlGeneratorService.generateKml(collectedScans, kmlFileName);
        }

        // Output CSV file
        if (csvOutput) {
            csvWriterService.writeJsonListToCsv(collectedScans, csvFileName);
        }

        return collectedScans;
    }
}