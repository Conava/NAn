package org.cs250.nan.backend.shell;

import org.cs250.nan.backend.database.SaveToMongoDB;
import org.json.JSONObject;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Scanner;
import java.util.concurrent.atomic.AtomicBoolean;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class MainScan {
    private static final AtomicBoolean running = new AtomicBoolean(false);

    public static List<JSONObject> scan(boolean continuousScan, boolean gpsOn, boolean kmlOutput, boolean csvOutput, int scanInterval, String kmlFileName, String csvFileName, boolean db) throws IOException {
        SaveToMongoDB mongoSaver = new SaveToMongoDB("allData");

        if (db) {
            MongoConnectionManager.initialize(
                    "mongodb+srv://mleavitt1457:paPq1zK5gmrdk7rT@cs250-nan.2finb.mongodb.net/?retryWrites=true&w=majority&appName=CS250-NAn",
                    "wifiData"
            );
        }

        List<JSONObject> collectedScans = new ArrayList<>();
        int counter = 0;

        running.set(true);

        // This Thread allows the user to press the enter key to end a continuous scan
        Thread inputThread = null;
        if (continuousScan) {
            inputThread = new Thread(() -> {
                Scanner scanner = new Scanner(System.in);
                while (running.get()) {
                    if (scanner.nextLine().trim().isEmpty()) {
                        running.set(false);
                        System.out.println("Concluding final scan and exiting...");
                    }
                }
                scanner.close();
            });
            inputThread.start();
        }

        //generate session id from the current second (YYMMDDHHMMSS)
        LocalDateTime now = LocalDateTime.now();
        // Define the format you want: YYMMDDHHMMSS
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyMMddHHmmss");
        // Format the current date and time
        String scanSessionID = now.format(formatter);

        System.out.println("Beginning scan. End the scan by pressing the enter key.\n\nThe session ID for this scan is: " + scanSessionID);
        System.out.println("\nAll data associated with this scan session can be found using this session ID in MongoDB.\nThe session ID coressponds to the time the scan was initiated in the format \"YYMMDDHHMMSS\".");

        while (running.get()) {
            try {
                // Step 1: Perform WiFi scan
                String wifiScanResult = SingleWiFiScanWindows.scan();
                if (wifiScanResult == null) {
                    System.out.println("Wi-Fi scan returned an empty string; retrying (or press enter to quit).");
                    continue;
                }

                // Step 2: Parse Wi-Fi scan result
                List<JSONObject> wifiParsedResults = ParseWiFiDataWindows.parseStringToListOfJSON(wifiScanResult);

                // Step 3: Perform GPS scan
                if (gpsOn) {
                    SingleGPSScanWindows gpsScanner = new SingleGPSScanWindows();
                    String gpsScanResult = gpsScanner.getGPSDataIgnoreGPGSV();

                    if (!Objects.equals(gpsScanResult, "")) {
                        // Step 4: Parse GPS scan result
                        ParseGPSData gpsData = new ParseGPSData();
                        JSONObject gpsParsedResults = gpsData.parseStringToListOfJSON(gpsScanResult);

                        // Step 5: Merge the parsed results from the Wi-Fi and GPS scans
                        List<JSONObject> mergedScanResult = MergeJSONData.mergeJSONObjects(wifiParsedResults, gpsParsedResults);

                        // Step 6: Add to collected results
                        collectedScans.addAll(mergedScanResult);
                    }
                    else {
                        System.out.println("GPS scan returned an empty string; retrying (or press enter to quit).");
                        collectedScans.addAll(wifiParsedResults);
                    }
                }
                else {
                    kmlOutput = false;
                    collectedScans.addAll(wifiParsedResults);
                }

                for (JSONObject obj : collectedScans) {
                    obj.put("sessionID", scanSessionID);
                }

                System.out.println(collectedScans.size());
                if (db) {
                    for (JSONObject obj : collectedScans) {
                        mongoSaver.saveJsonObject(obj);
                    }
                }

                counter++;
                System.out.println("Scan(s) completed: " + counter + ".");

                if (!continuousScan) {
                    running.set(false);
                    break;
                }
                // Step 7: Wait for the scan interval before the next iteration
                Thread.sleep(scanInterval * 1000L);

            } catch (Exception e) {
                System.err.println("An error occurred: " + e.getMessage());
                e.printStackTrace(); // Restore interrupt status
            }
        }

        if (kmlOutput) {
            WriteJSONToKML.writeJSONToKML(collectedScans, kmlFileName);
        }

        if (csvOutput) {
            WriteJSONToCSV.writeJsonListToCsv(collectedScans, csvFileName);
        }

        running.set(false);
        MongoConnectionManager.close();
        return collectedScans;
    }

    public static void main(String[] args) throws IOException {
        List<JSONObject> results = scan(false, false, false, false, 10, "continuousWiFiScan", "continuousWiFiScan", true);


//        for (JSONObject data : results) { // Iterate through the JSON objects, printing each
//            System.out.println(data.toString(4));
//        }
    }
}