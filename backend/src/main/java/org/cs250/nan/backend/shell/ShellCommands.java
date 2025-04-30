package org.cs250.nan.backend.shell;

import org.cs250.nan.backend.config.AppProperties;
import org.cs250.nan.backend.database.MongoRetrievalResults;
import org.cs250.nan.backend.manager.ApplicationManager;
import org.json.JSONObject;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellOption;

import java.util.*;
import java.util.stream.Collectors;

@ShellComponent
public class ShellCommands {
    private final ApplicationManager appMgr;
    private final AppProperties props;
    private MongoRetrievalResults mongoRetrievalResults;

    public ShellCommands(ApplicationManager appMgr, AppProperties props) {
        this.appMgr = appMgr;
        this.props = props;
    }

    // ─── Shutdown aliases ────────────────────────────────────────────────────

    @ShellMethod(value = "Shutdown the application", key = {"shutdown", "exit", "quit", "stop"})
    public String shutdown() {
        return appMgr.shutdownApplication();
    }

    // ─── Monitoring Control ─────────────────────────────────────────────────

    @ShellMethod(value = "Start continuous monitoring", key = "monitor start")
    public String startMonitoring() {
        return appMgr.startMonitoring();
    }

    @ShellMethod(value = "Stop continuous monitoring", key = "monitor stop")
    public String stopMonitoring() {
        return appMgr.stopMonitoring();
    }

    // ─── One-Off Scans ───────────────────────────────────────────────────────

    @ShellMethod(value = "Run a one-off scan with defaults", key = "scan run-default")
    public String scanDefault() {
        return appMgr.runSingleScan(null).join();
    }

    @ShellMethod(value = "Run a one-off scan overriding monitor settings", key = "scan run")
    public String scanRun(
            @ShellOption(defaultValue = ShellOption.NULL) Integer scanInterval,
            @ShellOption(defaultValue = ShellOption.NULL) Boolean gpsOn,
            @ShellOption(defaultValue = ShellOption.NULL) Boolean kmlOutput,
            @ShellOption(defaultValue = ShellOption.NULL) Boolean csvOutput,
            @ShellOption(defaultValue = ShellOption.NULL) String jsonFileName,
            @ShellOption(defaultValue = ShellOption.NULL) String kmlFileName,
            @ShellOption(defaultValue = ShellOption.NULL) String csvFileName
    ) {
        // build override config
        var base = props.getMonitor();
        var cfg = new AppProperties.Monitor();
        cfg.setScanInterval(base.getScanInterval());
        cfg.setGpsOn(base.isGpsOn());
        cfg.setKmlOutput(base.isKmlOutput());
        cfg.setCsvOutput(base.isCsvOutput());
        cfg.setJsonFileName(base.getJsonFileName());
        cfg.setKmlFileName(base.getKmlFileName());
        cfg.setCsvFileName(base.getCsvFileName());

        if (scanInterval != null) cfg.setScanInterval(scanInterval);
        if (gpsOn != null) cfg.setGpsOn(gpsOn);
        if (kmlOutput != null) cfg.setKmlOutput(kmlOutput);
        if (csvOutput != null) cfg.setCsvOutput(csvOutput);
        if (jsonFileName != null) cfg.setJsonFileName(jsonFileName);
        if (kmlFileName != null) cfg.setKmlFileName(kmlFileName);
        if (csvFileName != null) cfg.setCsvFileName(csvFileName);

        return appMgr.runSingleScan(cfg).join();
    }

    // ─── Monitoring Data & State ─────────────────────────────────────────────

    @ShellMethod(value = "Export monitoring data to JSON file", key = "monitor export")
    public String exportData(@ShellOption(defaultValue = "monitorData") String fileName) {
        return appMgr.exportMonitoringData(fileName);
    }

    @ShellMethod(value = "Show collected monitoring data", key = "monitor data")
    public List<Object> showData() {
        return appMgr.getMonitoringData().stream()
                .map(o -> ((Map<?, ?>) ((org.json.JSONObject) o).toMap()))
                .collect(Collectors.toList());
    }

    @ShellMethod(value = "Show application state", key = "app state")
    public String showState() {
        var s = appMgr.getApplicationState();
        return String.format(
                "Monitoring: %s%nLast Scan: %s%nResults  : %d",
                s.monitoring(), s.lastScanTime(), s.lastScanResultCount()
        );
    }

    // ─── Settings Show/Update ─────────────────────────────────────────────────

    @ShellMethod(value = "Show current settings", key = "settings show")
    public String showSettings() {
        // 1) prepare a list of "key : value" strings
        var p = props;
        List<String> rows = List.of(
                "base-dir           : " + p.getBaseDir(),
                "data-storage       : " + p.getDataStorage(),
                "keep-history       : " + p.isKeepHistory(),
                "activate-gui       : " + p.isActivateGui(),
                "log-file           : " + p.getLogFile(),
                "",
                "db.remote-enabled  : " + p.getDb().isRemoteEnabled(),
                "db.remote-url      : " + p.getDb().getRemoteUrl(),
                "",
                "monitor.interval   : " + p.getMonitor().getScanInterval(),
                "monitor.gps-on     : " + p.getMonitor().isGpsOn(),
                "monitor.kml-out    : " + p.getMonitor().isKmlOutput(),
                "monitor.csv-out    : " + p.getMonitor().isCsvOutput(),
                "monitor.json-file  : " + p.getMonitor().getJsonFileName(),
                "monitor.kml-file   : " + p.getMonitor().getKmlFileName(),
                "monitor.csv-file   : " + p.getMonitor().getCsvFileName()
        );

        // 2) compute the maximum row length
        int max = rows.stream().mapToInt(String::length).max().orElse(0);

        // 3) draw top border, centering the title
        String title = " Current Settings ";
        int pad = (max - title.length()) / 2;
        String top = "╔"
                + "═".repeat(pad)
                + title
                + "═".repeat(max - title.length() - pad)
                + "╗";

        // 4) draw each row, padding to 'max'
        List<String> boxed = new ArrayList<>();
        boxed.add(top);
        for (String row : rows) {
            String content = row;
            // if empty, show blank line
            if (row.isEmpty()) {
                content = "";
            }
            boxed.add("║ "
                    + content
                    + " ".repeat(max - content.length())
                    + " ║");
        }
        // 5) bottom border
        boxed.add("╚" + "═".repeat(max + 2) + "╝");

        return String.join("\n", boxed);
    }

    @ShellMethod(value = "Bulk‐set one or more settings: key=value …", key = "settings set")
    public String setSettings(
            @ShellOption(arity = Integer.MAX_VALUE, help = "pairs like base-dir=/path data-storage=/other …")
            String[] pairs
    ) {
        // start from current
        AppProperties p = new AppProperties();
        // copy existing
        p.setBaseDir(props.getBaseDir());
        p.setDataStorage(props.getDataStorage());
        p.setKeepHistory(props.isKeepHistory());
        p.setActivateGui(props.isActivateGui());
        p.setLogFile(props.getLogFile());
        p.getDb().setRemoteEnabled(props.getDb().isRemoteEnabled());
        p.getDb().setRemoteUrl(props.getDb().getRemoteUrl());
        var m = p.getMonitor();
        var b = props.getMonitor();
        m.setScanInterval(b.getScanInterval());
        m.setGpsOn(b.isGpsOn());
        m.setKmlOutput(b.isKmlOutput());
        m.setCsvOutput(b.isCsvOutput());
        m.setJsonFileName(b.getJsonFileName());
        m.setKmlFileName(b.getKmlFileName());
        m.setCsvFileName(b.getCsvFileName());

        var changed = new ArrayList<String>();
        for (var pair : pairs) {
            var parts = pair.split("=", 2);
            if (parts.length != 2) continue;
            var k = parts[0].trim();
            var v = parts[1].trim();
            try {
                switch (k) {
                    case "base-dir" -> {
                        p.setBaseDir(v);
                        changed.add(k);
                    }
                    case "data-storage" -> {
                        p.setDataStorage(v);
                        changed.add(k);
                    }
                    case "keep-history" -> {
                        p.setKeepHistory(Boolean.parseBoolean(v));
                        changed.add(k);
                    }
                    case "activate-gui" -> {
                        p.setActivateGui(Boolean.parseBoolean(v));
                        changed.add(k);
                    }
                    case "log-file" -> {
                        p.setLogFile(v);
                        changed.add(k);
                    }
                    case "db.remote-enabled" -> {
                        p.getDb().setRemoteEnabled(Boolean.parseBoolean(v));
                        changed.add(k);
                    }
                    case "db.remote-url" -> {
                        p.getDb().setRemoteUrl(v);
                        changed.add(k);
                    }
                    case "monitor.interval" -> {
                        p.getMonitor().setScanInterval(Integer.parseInt(v));
                        changed.add(k);
                    }
                    case "monitor.gps-on" -> {
                        p.getMonitor().setGpsOn(Boolean.parseBoolean(v));
                        changed.add(k);
                    }
                    case "monitor.kml-out" -> {
                        p.getMonitor().setKmlOutput(Boolean.parseBoolean(v));
                        changed.add(k);
                    }
                    case "monitor.csv-out" -> {
                        p.getMonitor().setCsvOutput(Boolean.parseBoolean(v));
                        changed.add(k);
                    }
                    case "monitor.json-file" -> {
                        p.getMonitor().setJsonFileName(v);
                        changed.add(k);
                    }
                    case "monitor.kml-file" -> {
                        p.getMonitor().setKmlFileName(v);
                        changed.add(k);
                    }
                    case "monitor.csv-file" -> {
                        p.getMonitor().setCsvFileName(v);
                        changed.add(k);
                    }
                    default -> {
                        return "Unknown setting key: " + k;
                    }
                }
            } catch (Exception ex) {
                return "Invalid value for " + k + ": " + v;
            }
        }

        String result = appMgr.updateSettings(p);
        return "Updated [" + String.join(", ", changed) + "] → " + result;
    }

    @ShellMethod(value = "Search MongoDB by key-value and return matching entries", key = "mongo retrieve")
    public String searchMongo() {
        // Get the search results
        List<JSONObject> results = appMgr.runMongoSearch();

        // Store the results in a new MongoSearchResults instance
        mongoRetrievalResults = new MongoRetrievalResults(results, "", "");

        return "Found " + results.size() + " matching entries.";
    }

    @ShellMethod(value = "Show previously fetched MongoDB results", key = "mongo show")
    public MongoRetrievalResults showMongoResults() {
        if (mongoRetrievalResults == null || mongoRetrievalResults.getResults().isEmpty()) {
            System.out.println("No MongoDB results found.");
            return new MongoRetrievalResults(List.of(), "", ""); // Return an empty result if no data is found
        }

        // Print each JSON object from the results
        for (JSONObject json : mongoRetrievalResults.getResults()) {
            System.out.println(json.toString(4)); // Pretty-print each JSON object
        }

        return mongoRetrievalResults; // Return the results object
    }

}
