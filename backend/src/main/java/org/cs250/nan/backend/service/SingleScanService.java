package org.cs250.nan.backend.service;

import org.cs250.nan.backend.config.AppProperties;
import org.json.JSONObject;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * Facade for one‑off scans. Returns a Future of the raw JSON results.
 */
@Service
public class SingleScanService {
    private final ScanService scanService;
    private final AppProperties props;

    public SingleScanService(ScanService scanService, AppProperties props) {
        this.scanService = scanService;
        this.props = props;
    }

    public CompletableFuture<List<JSONObject>> run(AppProperties.Monitor overrideConfig) {
        return scanService.scan(overrideConfig);
    }
}
