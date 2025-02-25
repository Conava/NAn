package org.cs250.nan.backend.shell;

import org.cs250.nan.backend.BackendApplication;
import org.cs250.nan.backend.config.Settings;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellOption;

@ShellComponent
public class ShellCommands {

    private final Settings settings = BackendApplication.getSettings();

    // Basic command for testing
    @ShellMethod(value = "Print a greeting message", key = "hello")
    public String hello(@ShellOption(defaultValue = "User") String name) {
        return String.format("Hello, %s!", name);
    }

    @ShellMethod(value = "Perform a one time scan", key = "scan")
    public String scan() {
        return "Scan initiated";
    }

    @ShellMethod(value = "Start continuous scan monitoring", key = "monitor")
    public String monitor() {
        return "Continuous scan monitoring started";
    }

    @ShellMethod(value = "Open API", key = "openApi")
    public String openApi() {
        return "API opened";
    }

    @ShellMethod(value = "Close API", key = "closeApi")
    public String closeApi() {
        return "API closed";
    }

    @ShellMethod(value = "View the latest scan data", key = "latestScan")
    public String latestScan() {
        return "Latest scan data displayed";
    }

    @ShellMethod(value = "Open web interface", key = "openWeb")
    public String openWeb() {
        return "Web interface opened";
    }

    @ShellMethod(value = "Export data", key = "export")
    public String export() {
        return "Data export initiated";
    }

    @ShellMethod(value = "Restart the application", key = "restart")
    public String restart() {
        return "Application restarting...";
    }

    @ShellMethod(value = "Display or update settings", key = "settings")
    public String settings(@ShellOption(defaultValue = "") String key, @ShellOption(defaultValue = "") String value) {
        if (key.isEmpty()) {
            return String.format("dataStorage: %s\ndefaultUseOfGps: %s\nkeepHistory: %s\nactivateGui: %s",
                    settings.getDataStorage(), settings.isDefaultUseOfGps(),
                    settings.isKeepHistory(), settings.isActivateGui());
        } else {
            switch (key) {
                case "dataStorage":
                    settings.setDataStorage(value);
                    break;
                case "defaultUseOfGps":
                    settings.setDefaultUseOfGps(Boolean.parseBoolean(value));
                    break;
                case "keepHistory":
                    settings.setKeepHistory(Boolean.parseBoolean(value));
                    break;
                case "activateGui":
                    settings.setActivateGui(Boolean.parseBoolean(value));
                    break;
                default:
                    return "Invalid setting key.";
            }
            settings.saveSettings();
            return "Setting updated.";
        }
    }

    @ShellMethod(value = "Help for settings command", key = "helpSettings")
    public String helpSettings() {
        return "Settings command usage:\n" +
                "settings - Display all settings\n" +
                "settings <key> <value> - Update a setting\n" +
                "Available keys:\n" +
                "dataStorage - Path to data storage directory\n" +
                "defaultUseOfGps - true/false to enable/disable GPS by default\n" +
                "keepHistory - true/false to keep or delete data from the last run\n" +
                "activateGui - true/false to activate or deactivate the GUI by default";
    }
}

