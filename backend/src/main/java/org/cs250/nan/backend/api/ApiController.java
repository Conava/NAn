package org.cs250.nan.backend.api;

import org.cs250.nan.backend.manager.ApplicationManager;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class ApiController {

    private final ApplicationManager applicationManager;

    public ApiController(ApplicationManager applicationManager) {
        this.applicationManager = applicationManager;
    }

    @PostMapping("/monitor/start")
    public ResponseEntity<String> startMonitoring() {
        String message = applicationManager.doStartMonitoring();
        return ResponseEntity.ok(message);
    }

    @PostMapping("/monitor/stop")
    public ResponseEntity<String> stopMonitoring() {
        String message = applicationManager.doStopMonitoring();
        return ResponseEntity.ok(message);
    }

    @PostMapping("/monitor/update")
    public ResponseEntity<String> updateMonitoring(@RequestParam int interval,
                                                   @RequestParam boolean gpsOn,
                                                   @RequestParam boolean kmlOutput,
                                                   @RequestParam boolean csvOutput,
                                                   @RequestParam String kmlName,
                                                   @RequestParam String csvName) {
        String message = applicationManager.doUpdateMonitoring(interval, gpsOn, kmlOutput, csvOutput, kmlName, csvName);
        return ResponseEntity.ok(message);
    }

    @PostMapping("/settings/reset")
    public ResponseEntity<String> resetSettings() {
        String message = applicationManager.doResetSettings();
        return ResponseEntity.ok(message);
    }

    @GetMapping("/scan")
    public ResponseEntity<String> singleScan() {
        String message = applicationManager.doSingleScan(null, null, null, null, null, null);
        return ResponseEntity.ok(message);
    }
}