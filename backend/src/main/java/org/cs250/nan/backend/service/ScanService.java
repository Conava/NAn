package org.cs250.nan.backend.service;

import org.cs250.nan.backend.config.AppProperties;
import org.cs250.nan.backend.scanner.Scanner;
import org.json.JSONObject;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * Performs a single scan asynchronously based on a Monitor config.
 */
@Service
public class ScanService {
    private final Scanner scanner;
    private final AppProperties props;

    public ScanService(Scanner scanner, AppProperties props) {
        this.scanner = scanner;
        this.props = props;
    }

    @Async
    public CompletableFuture<List<JSONObject>> scan(AppProperties.Monitor override) {
        var cfg = override != null ? override : props.getMonitor();
        try {
            List<JSONObject> results = scanner.scan(
                    cfg.isGpsOn(),
                    cfg.isKmlOutput(),
                    cfg.isCsvOutput(),
                    cfg.getJsonFileName(),
                    cfg.getKmlFileName(),
                    cfg.getCsvFileName()
            );
            return CompletableFuture.completedFuture(results);
        } catch (IOException e) {
            // log & recover
            return CompletableFuture.completedFuture(List.of());
        }
    }
}
