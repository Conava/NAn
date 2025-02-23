package org.cs250.nan.backend.shell;

import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellOption;

@ShellComponent
public class ShellCommands {

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

    @ShellMethod(value = "Display or update settings", key = "settings")
    public String settings() {
        return "Settings command executed";
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
}

