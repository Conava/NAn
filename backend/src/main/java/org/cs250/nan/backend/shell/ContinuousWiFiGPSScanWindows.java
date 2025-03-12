package org.cs250.nan.backend.shell;

import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public class ContinuousWiFiGPSScanWindows {
    private final AtomicBoolean running = new AtomicBoolean(false);

    public List<JSONObject> scan(int intervalSeconds, int totalTimeSeconds) {
        List<JSONObject> collectedScans = new ArrayList<>();
        long startTime = System.currentTimeMillis();
        long endTime = startTime + (totalTimeSeconds * 1000L);

        running.set(true);

        while (running.get() && System.currentTimeMillis() < endTime) {
            try {
                // Step 1: Perform WiFi scan
                String wifiScanResult = SingleWiFiScanWindows.scan();  // Assume this method exists
//                System.out.println(wifiScanResult);

                // Step 2: Parse Wi-Fi scan result
                List<JSONObject> wifiParsedResults = ParseWiFiDataWindows.parseStringToListOfJSON(wifiScanResult);  // Assume this method exists
//                System.out.println(parsedResults);

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

                // Step 7: Wait for the interval or stop if interrupted
                Thread.sleep(intervalSeconds * 1000L);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt(); // Restore interrupt status
                break;
            }
        }

        return collectedScans;
    }

    public void stopScanning() {
        running.set(false);
    }

    public static void main(String[] args) {
        ContinuousWiFiGPSScanWindows contScan = new ContinuousWiFiGPSScanWindows();
        List <JSONObject> jsonWiFiObjects = contScan.scan(4, 8);
//        System.out.println("\n\n\n\n\n");
        for (JSONObject data : jsonWiFiObjects) { //iterate through the JSON objects, printing each
            System.out.println(data.toString(4));
        }
    }

}