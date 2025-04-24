package org.cs250.nan.backend.api;

import lombok.Data;
import org.cs250.nan.backend.config.AppProperties;
import org.cs250.nan.backend.manager.ApplicationManager;
import org.cs250.nan.backend.manager.ApplicationManager.ApplicationState;
import org.json.JSONObject;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
public class ApiController {

    private final ApplicationManager appMgr;
    private final AppProperties props;

    public ApiController(ApplicationManager appMgr,
                         AppProperties props) {
        this.appMgr = appMgr;
        this.props = props;
    }

    // ─── Application Lifecycle ───────────────────────────────────────────────

    /**
     * Gracefully shut down the application.
     */
    @PostMapping("/application/shutdown")
    public ResponseEntity<String> shutdown() {
        return ResponseEntity.ok(appMgr.shutdownApplication());
    }

    // ─── Monitoring Control ─────────────────────────────────────────────────

    @PostMapping("/monitor/start")
    public ResponseEntity<String> startMonitoring() {
        return ResponseEntity.ok(appMgr.startMonitoring());
    }

    @PostMapping("/monitor/stop")
    public ResponseEntity<String> stopMonitoring() {
        return ResponseEntity.ok(appMgr.stopMonitoring());
    }

    // ─── One-Off Scans ───────────────────────────────────────────────────────

    @GetMapping("/scan")
    public CompletableFuture<ResponseEntity<String>> singleScan() {
        return appMgr
                .runSingleScan(null)
                .thenApply(ResponseEntity::ok);
    }

    @PostMapping("/scan")
    public CompletableFuture<ResponseEntity<String>> singleScanWithConfig(
            @RequestBody MonitorConfigDTO dto) {
        AppProperties.Monitor cfg = toMonitor(dto);
        return appMgr
                .runSingleScan(cfg)
                .thenApply(ResponseEntity::ok);
    }

    // ─── Monitoring Data & Export ────────────────────────────────────────────

    @GetMapping("/monitor/data")
    public ResponseEntity<List<Object>> getMonitoringData() {
        List<Object> raw = appMgr.getMonitoringData()
                .stream()
                .map(JSONObject::toMap)
                .collect(Collectors.toList());
        return ResponseEntity.ok(raw);
    }

    @GetMapping("/monitor/export")
    public ResponseEntity<String> exportMonitoringData(
            @RequestParam(defaultValue = "monitorData") String fileName) {
        return ResponseEntity.ok(appMgr.exportMonitoringData(fileName));
    }

    // ─── Application State ──────────────────────────────────────────────────

    @GetMapping("/state")
    public ResponseEntity<ApplicationState> getState() {
        return ResponseEntity.ok(appMgr.getApplicationState());
    }

    // ─── Settings Get/Update ─────────────────────────────────────────────────

    @GetMapping("/settings")
    public ResponseEntity<SettingsDTO> getSettings() {
        return ResponseEntity.ok(fromProps(props));
    }

    @PostMapping("/settings")
    public ResponseEntity<String> updateSettings(@RequestBody SettingsDTO dto) {
        AppProperties newProps = toProps(dto);
        String result = appMgr.updateSettings(newProps);
        return ResponseEntity.ok(result);
    }

    // ─── DTOs & Mappers ─────────────────────────────────────────────────────

    @Data
    public static class MonitorConfigDTO {
        private Integer scanInterval;
        private Boolean gpsOn;
        private Boolean kmlOutput;
        private Boolean csvOutput;
        private String jsonFileName;
        private String kmlFileName;
        private String csvFileName;
    }

    @Data
    public static class SettingsDTO {
        private String baseDir;
        private String dataStorage;
        private Boolean defaultUseGps;
        private Boolean keepHistory;
        private Boolean activateGui;
        private String logFile;
        private DbDTO db;
        private MonitorConfigDTO monitor;

        @Data
        public static class DbDTO {
            private Boolean remoteEnabled;
            private String remoteUrl;
        }
    }

    private AppProperties.Monitor toMonitor(MonitorConfigDTO dto) {
        AppProperties.Monitor m = new AppProperties.Monitor();
        if (dto.getScanInterval() != null) m.setScanInterval(dto.getScanInterval());
        if (dto.getGpsOn() != null) m.setGpsOn(dto.getGpsOn());
        if (dto.getKmlOutput() != null) m.setKmlOutput(dto.getKmlOutput());
        if (dto.getCsvOutput() != null) m.setCsvOutput(dto.getCsvOutput());
        if (dto.getJsonFileName() != null) m.setJsonFileName(dto.getJsonFileName());
        if (dto.getKmlFileName() != null) m.setKmlFileName(dto.getKmlFileName());
        if (dto.getCsvFileName() != null) m.setCsvFileName(dto.getCsvFileName());
        return m;
    }

    private AppProperties toProps(SettingsDTO dto) {
        AppProperties p = new AppProperties();
        p.setBaseDir(dto.getBaseDir());
        p.setDataStorage(dto.getDataStorage());
        p.setDefaultUseGps(dto.getDefaultUseGps());
        p.setKeepHistory(dto.getKeepHistory());
        p.setActivateGui(dto.getActivateGui());
        p.setLogFile(dto.getLogFile());

        var db = p.getDb();
        var dbdto = dto.getDb();
        db.setRemoteEnabled(dbdto.getRemoteEnabled());
        db.setRemoteUrl(dbdto.getRemoteUrl());

        p.getMonitor().setScanInterval(dto.getMonitor().getScanInterval());
        p.getMonitor().setGpsOn(dto.getMonitor().getGpsOn());
        p.getMonitor().setKmlOutput(dto.getMonitor().getKmlOutput());
        p.getMonitor().setCsvOutput(dto.getMonitor().getCsvOutput());
        p.getMonitor().setJsonFileName(dto.getMonitor().getJsonFileName());
        p.getMonitor().setKmlFileName(dto.getMonitor().getKmlFileName());
        p.getMonitor().setCsvFileName(dto.getMonitor().getCsvFileName());

        return p;
    }

    private SettingsDTO fromProps(AppProperties p) {
        SettingsDTO dto = new SettingsDTO();
        dto.setBaseDir(p.getBaseDir());
        dto.setDataStorage(p.getDataStorage());
        dto.setDefaultUseGps(p.isDefaultUseGps());
        dto.setKeepHistory(p.isKeepHistory());
        dto.setActivateGui(p.isActivateGui());
        dto.setLogFile(p.getLogFile());

        SettingsDTO.DbDTO dbdto = new SettingsDTO.DbDTO();
        dbdto.setRemoteEnabled(p.getDb().isRemoteEnabled());
        dbdto.setRemoteUrl(p.getDb().getRemoteUrl());
        dto.setDb(dbdto);

        MonitorConfigDTO mDto = new MonitorConfigDTO();
        var m = p.getMonitor();
        mDto.setScanInterval(m.getScanInterval());
        mDto.setGpsOn(m.isGpsOn());
        mDto.setKmlOutput(m.isKmlOutput());
        mDto.setCsvOutput(m.isCsvOutput());
        mDto.setJsonFileName(m.getJsonFileName());
        mDto.setKmlFileName(m.getKmlFileName());
        mDto.setCsvFileName(m.getCsvFileName());
        dto.setMonitor(mDto);

        return dto;
    }
}
