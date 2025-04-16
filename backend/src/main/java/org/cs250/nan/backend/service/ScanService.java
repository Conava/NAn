package org.cs250.nan.backend.service;

        import org.cs250.nan.backend.scanner.Scanner;
        import org.json.JSONObject;
        import org.springframework.beans.factory.annotation.Autowired;
        import org.springframework.scheduling.annotation.Async;
        import org.springframework.stereotype.Service;

        import java.io.IOException;
        import java.util.List;
        import java.util.concurrent.CompletableFuture;

        @Service
        public class ScanService {

            private final Scanner scanner;

            @Autowired
            public ScanService(Scanner scanner) {
                this.scanner = scanner;
            }

            /**
             * Runs the scan in the background.
             *
             * @param gpsOn       enables GPS scanning if true
             * @param kmlOutput   writes output in KML format if true
             * @param csvOutput   writes output in CSV format if true
             * @param kmlFileName KML output file name
             * @param csvFileName CSV output file name
             * @return CompletableFuture holding the list of scan results
             */
            @Async
            public CompletableFuture<List<JSONObject>> runScanAsync(boolean gpsOn, boolean kmlOutput, boolean csvOutput, String jsonFileName, String kmlFileName, String csvFileName) {
                try {
                    List<JSONObject> results = scanner.scan(gpsOn, kmlOutput, csvOutput, jsonFileName, kmlFileName, csvFileName);
                    return CompletableFuture.completedFuture(results);
                } catch (IOException e) {
                    // Handle exception as needed and return a completed future with a null value
                    return CompletableFuture.completedFuture(null);
                }
            }
        }