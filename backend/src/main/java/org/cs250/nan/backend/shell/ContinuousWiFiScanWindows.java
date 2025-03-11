package org.cs250.nan.backend.shell;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import org.json.JSONObject;

public class ContinuousWiFiScanWindows {
    private final AtomicBoolean running = new AtomicBoolean(false);

    public List<JSONObject> scan(int intervalSeconds, int totalTimeSeconds) {
        List<JSONObject> collectedScans = new ArrayList<>();
        long startTime = System.currentTimeMillis();
        long endTime = startTime + (totalTimeSeconds * 1000L);

        running.set(true);

        while (running.get() && System.currentTimeMillis() < endTime) {
            try {
                // Step 1: Perform WiFi scan
                String scanResult = SingleWiFiScanWindows.scan();  // Assume this method exists
//                System.out.println(scanResult);

                // Step 2: Parse scan result
                List<JSONObject> parsedResults = ParseWiFiDataWindows.parseStringToListOfJSON(scanResult);  // Assume this method exists
//                System.out.println(parsedResults);

                // Step 3: Add to collected results
                collectedScans.addAll(parsedResults);

                // Step 4: Wait for the interval or stop if interrupted
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
        ContinuousWiFiScanWindows contScan = new ContinuousWiFiScanWindows();
        List <JSONObject> jsonWiFiObjects = contScan.scan(1, 3);
//        System.out.println("\n\n\n\n\n");
        for (JSONObject data : jsonWiFiObjects) { //iterate through the JSON objects, printing each
            System.out.println(data.toString(4));
        }
    }

}