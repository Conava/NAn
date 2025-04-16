package org.cs250.nan.backend.service;

import org.cs250.nan.backend.config.Settings;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.Future;
import java.util.Objects;

@Service
public class singleScanManager {

    private final ScanService singleScanManager;
    private final Settings settings;

    @Autowired
    public singleScanManager(ScanService singleScanManager, Settings settings) {
        this.singleScanManager = singleScanManager;
        this.settings = settings;
    }

    /**
     * Runs a single scan using the specified parameters.
     * If a parameter is null, the default value from the Settings is used.
     *
     * @param gpsOn        optional flag indicating if GPS should be enabled; if null, use default.
     * @param kmlOutput    optional flag indicating if KML output is enabled; if null, use default.
     * @param csvOutput    optional flag indicating if CSV output is enabled; if null, use default.
     * @param scanInterval optional scan interval in seconds; if null, use default.
     * @param kmlFileName  optional file name for KML output; if null, use default.
     * @param csvFileName  optional file name for CSV output; if null, use default.
     * @return Future with the list of scan results.
     */
    public Future<List<JSONObject>> runSingleScan(Boolean gpsOn,
                                                  Boolean kmlOutput,
                                                  Boolean csvOutput,
                                                  Integer scanInterval,
                                                  String JsonFileName,
                                                  String kmlFileName,
                                                  String csvFileName) {

        boolean effectiveGpsOn = Objects.nonNull(gpsOn) ? gpsOn : settings.isDefaultUseOfGps();
        boolean effectiveKmlOutput = Objects.nonNull(kmlOutput) ? kmlOutput : settings.isMonitorKmlOutput();
        boolean effectiveCsvOutput = Objects.nonNull(csvOutput) ? csvOutput : settings.isMonitorCsvOutput();
        int effectiveScanInterval = Objects.nonNull(scanInterval) ? scanInterval : settings.getMonitorScanInterval();
        String effectiveJsonFileName = Objects.nonNull(JsonFileName) ? JsonFileName : settings.getMonitorKmlFileName();
        String effectiveKmlFileName = Objects.nonNull(kmlFileName) ? kmlFileName : settings.getMonitorKmlFileName();
        String effectiveCsvFileName = Objects.nonNull(csvFileName) ? csvFileName : settings.getMonitorCsvFileName();

        return singleScanManager.runScanAsync(effectiveGpsOn, effectiveKmlOutput, effectiveCsvOutput,
                effectiveScanInterval, effectiveJsonFileName, effectiveKmlFileName, effectiveCsvFileName);
    }
}