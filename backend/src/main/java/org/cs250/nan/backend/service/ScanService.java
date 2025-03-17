package org.cs250.nan.backend.service;

import org.json.JSONObject;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Service
public class ScanService {

    /**
     * Runs the scan in the background.
     *
     * @param gpsOn enables GPS scanning if true
     * @param kmlOutput writes output in KML format if true
     * @param scanInterval time interval between scans in seconds
     * @param kmlFileName KML output file name
     * @param csvFileName CSV output file name
     * @return CompletableFuture holding the list of scan results
     */
    @Async
    public CompletableFuture<List<JSONObject>> runScanAsync(boolean gpsOn, boolean kmlOutput, boolean csvOutput, int scanInterval, String kmlFileName, String csvFileName) {
        try {
            List<JSONObject> results = MainScan.scan(false, gpsOn, kmlOutput, csvOutput, scanInterval, kmlFileName, csvFileName);
            return CompletableFuture.completedFuture(results);
        } catch (IOException e) {
            // Handle exception as needed and return a completed future with a null value
            return CompletableFuture.completedFuture(null);
        }
    }
}