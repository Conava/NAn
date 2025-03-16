package org.cs250.nan.backend.shell;

import org.json.JSONObject;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.atomic.AtomicBoolean;

public class MainScan {
    private static final AtomicBoolean running = new AtomicBoolean(false);

    public static List<JSONObject> scan(boolean continuousScan, boolean gpsOn, boolean kmlOutput, boolean csvOutput, int scanInterval, String kmlFileName, String csvFileName) throws IOException {
        List<JSONObject> collectedScans = new ArrayList<>();
        int counter = 0;
//        long startTime = System.currentTimeMillis();
//        long endTime = startTime + (totalTimeSeconds * 1000L);

        running.set(true);

        // This Thread allows the user to press the enter key to end a continuous scan
        Thread inputThread = new Thread(() -> {
            Scanner scanner = new Scanner(System.in);
            while (running.get()) {
                if (scanner.nextLine().trim().isEmpty()) {
                    running.set(false);
                }
            }
            scanner.close();
        });
        inputThread.start();

        while (running.get()) {
            try {
                // Step 1: Perform WiFi scan
                String wifiScanResult = SingleWiFiScanWindows.scan();
                if (wifiScanResult == null) {
                    System.out.println("Wi-Fi scan failed, exiting program.");
                    System.exit(1);
                }

                // Step 2: Parse Wi-Fi scan result
                List<JSONObject> wifiParsedResults = ParseWiFiDataWindows.parseStringToListOfJSON(wifiScanResult);

                if (gpsOn) {
                    // Step 3: Perform GPS scan
                    SingleGPSScanWindows gpsScanner = new SingleGPSScanWindows();
                    String gpsScanResult = gpsScanner.getGPSDataIgnoreGPGSV();

                    // Step 4: Parse GPS scan result
                    ParseGPSData gpsData = new ParseGPSData();
                    JSONObject gpsParsedResults = gpsData.parseStringToListOfJSON(gpsScanResult);

                    // Step 5: Merge the parsed results from the Wi-Fi and GPS scans
                    List<JSONObject> mergedScanResult = MergeJSONData.mergeJSONObjects(wifiParsedResults, gpsParsedResults);

                    // Step 6: Add to collected results
                    collectedScans.addAll(mergedScanResult);
                }
                else {
                    kmlOutput = false;
                    collectedScans.addAll(wifiParsedResults);
                }
//                System.out.println(collectedScans);
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

        return collectedScans;
    }

    public static void main(String[] args) throws IOException {
        List<JSONObject> results = scan(false, true, true, true, 1, "continuousWiFiScan", "continuousWiFiScan");

//        for (JSONObject data : results) { // Iterate through the JSON objects, printing each
//            System.out.println(data.toString(4));
//        }

    }
}