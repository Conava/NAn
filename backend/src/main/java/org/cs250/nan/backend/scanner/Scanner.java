package org.cs250.nan.backend.scanner;

import org.cs250.nan.backend.database.MongoConnectionChecker;
import org.cs250.nan.backend.database.SaveToMongoDB;
import org.cs250.nan.backend.service.*;
import org.json.JSONObject;
import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Unified Scanner component performing Wi-Fi + optional GPS,
 * merging results, writing to DB or files.
 */
@Component
public class Scanner {
    private final WiFiScanner wifi;
    private final GPSScanner gps;
    private final MergeJsonDataService mergeService;
    private final JsonWriterService jsonWriter;
    private final KmlGeneratorService kmlService;
    private final CsvWriterService csvService;
    private final MongoConnectionChecker mongoChecker;
    private final SaveToMongoDB mongoSaver;

    @Autowired
    public Scanner(SystemWiFiScanner wifi,
                   SystemGPSScanner gps,
                   MergeJsonDataService mergeService,
                   JsonWriterService jsonWriter,
                   KmlGeneratorService kmlService,
                   CsvWriterService csvService,
                   MongoConnectionChecker mongoChecker,
                   SaveToMongoDB mongoSaver) {
        this.wifi = wifi;
        this.gps = gps;
        this.mergeService = mergeService;
        this.jsonWriter = jsonWriter;
        this.kmlService = kmlService;
        this.csvService = csvService;
        this.mongoChecker = mongoChecker;
        this.mongoSaver = mongoSaver;
    }

    public List<JSONObject> scan(boolean gpsOn,
                                 boolean kmlOutput,
                                 boolean csvOutput,
                                 String jsonFileName,
                                 String kmlFileName,
                                 String csvFileName) throws IOException {
        // 1) Wi-Fi
        var wifiList = wifi.scan();

        // 2) GPS
        List<JSONObject> merged;
        if (gpsOn) {
            var gpsObj = gps.scan();
            merged = mergeService.mergeJSONObjects(wifiList, gpsObj);
        } else {
            merged = new ArrayList<>(wifiList);
        }

        // 3) Persist or buffer
        if (mongoChecker.isConnected()) {
            merged.forEach(mongoSaver::insertJSONObject);
        } else {
            jsonWriter.writeJsonFile(merged, "ScanPendingUpload");
        }

        // 4) File outputs
        jsonWriter.writeJsonFile(merged, jsonFileName);
        if (kmlOutput) kmlService.generateKml(merged, kmlFileName);
        if (csvOutput) csvService.writeJsonListToCsv(merged, csvFileName);

        return merged;
    }
}
