package org.cs250.nan.backend.shell;

import org.cs250.nan.backend.manager.ApplicationManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellOption;

/**
 * Shell commands that delegate to the centralized application manager.
 */
@ShellComponent
public class ShellController {

    private final ApplicationManager applicationManager;

    @Autowired
    public ShellController(ApplicationManager applicationManager) {
        this.applicationManager = applicationManager;
    }

    @ShellMethod(value = "Start monitoring mode", key = "startMonitoring")
    public String startMonitoring() {
        return applicationManager.doStartMonitoring();
    }

    @ShellMethod(value = "Stop monitoring mode", key = "stopMonitoring")
    public String stopMonitoring() {
        return applicationManager.doStopMonitoring();
    }

    @ShellMethod(value = "Update monitoring settings", key = "updateMonitoring")
    public String updateMonitoring(@ShellOption(defaultValue = "5") int scanInterval,
                                   @ShellOption(defaultValue = "true") boolean gpsOn,
                                   @ShellOption(defaultValue = "true") boolean kmlOutput,
                                   @ShellOption(defaultValue = "false") boolean csvOutput,
                                   @ShellOption(defaultValue = "monitorKML") String kmlFileName,
                                   @ShellOption(defaultValue = "monitorCSV") String csvFileName) {
        return applicationManager.doUpdateMonitoring(scanInterval, gpsOn, kmlOutput, csvOutput, kmlFileName, csvFileName);
    }

    @ShellMethod(value = "Reset all settings to default", key = "resetSettings")
    public String resetSettings() {
        return applicationManager.doResetSettings();
    }

    @ShellMethod(value = "Run single scan", key = "singleScan")
    public String singleScan(@ShellOption(defaultValue = ShellOption.NULL) Boolean gpsOn,
                             @ShellOption(defaultValue = ShellOption.NULL) Boolean kmlOutput,
                             @ShellOption(defaultValue = ShellOption.NULL) Boolean csvOutput,
                             @ShellOption(defaultValue = ShellOption.NULL) Integer scanInterval,
                             @ShellOption(defaultValue = ShellOption.NULL) String kmlFileName,
                             @ShellOption(defaultValue = ShellOption.NULL) String csvFileName) {
        return applicationManager.doSingleScan(gpsOn, kmlOutput, csvOutput, scanInterval, kmlFileName, csvFileName);
    }
}