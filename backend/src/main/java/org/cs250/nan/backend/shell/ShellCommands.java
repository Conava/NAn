package org.cs250.nan.backend.shell;

import org.cs250.nan.backend.BackendApplication;
import org.cs250.nan.backend.config.Settings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellOption;

@ShellComponent
public class ShellCommands {

    private static final Logger logger = LoggerFactory.getLogger(ShellCommands.class);
    private final Settings settings;
    private final ApplicationContext context;

    @Autowired
    public ShellCommands(ApplicationContext context, Settings settings) {
        this.context = context;
        this.settings = settings;
    }

    @ShellMethod(value = "Print a greeting message", key = "hello")
    public String hello(@ShellOption(defaultValue = "User") String name) {
        logger.debug("Entering hello command with parameter: {}", name);
        String greeting = String.format("Hello, %s!", name);
        logger.info("Greeting generated: {}", greeting);
        logger.debug("Exiting hello command");
        return greeting;
    }

    @ShellMethod(value = "Perform a one time scan", key = "scan")
    public String scan() {
        logger.debug("Entering scan command");
        String result = "Scan initiated";
        logger.info("Scan command executed");
        logger.debug("Exiting scan command");
        return result;
    }

    @ShellMethod(value = "Start continuous scan monitoring", key = "monitor")
    public String monitor() {
        logger.debug("Entering monitor command");
        String result = "Continuous scan monitoring started";
        logger.info("Monitor command executed");
        logger.debug("Exiting monitor command");
        return result;
    }

    @ShellMethod(value = "Open API", key = "openApi")
    public String openApi() {
        logger.debug("Entering openApi command");
        String result = "API opened";
        logger.info("Open API command executed");
        logger.debug("Exiting openApi command");
        return result;
    }

    @ShellMethod(value = "Close API", key = "closeApi")
    public String closeApi() {
        logger.debug("Entering closeApi command");
        String result = "API closed";
        logger.info("Close API command executed");
        logger.debug("Exiting closeApi command");
        return result;
    }

    @ShellMethod(value = "View the latest scan data", key = "latestScan")
    public String latestScan() {
        logger.debug("Entering latestScan command");
        String result = "Latest scan data displayed";
        logger.info("LatestScan command executed");
        logger.debug("Exiting latestScan command");
        return result;
    }

    @ShellMethod(value = "Open web interface", key = "openWeb")
    public String openWeb() {
        logger.debug("Entering openWeb command");
        String result = "Web interface opened";
        logger.info("openWeb command executed");
        logger.debug("Exiting openWeb command");
        return result;
    }

    @ShellMethod(value = "Export data", key = "export")
    public String export() {
        logger.debug("Entering export command");
        String result = "Data export initiated";
        logger.info("Export command executed");
        logger.debug("Exiting export command");
        return result;
    }

    @ShellMethod(value = "Restart the application", key = "restart")
    public String restart() {
        logger.debug("Entering restart command");
        String result = "Application restarting...";
        logger.info("Restart command executed");
        logger.debug("Exiting restart command");
        return result;
    }

    @ShellMethod(value = "Display or update settings", key = "settings")
    public String settings(@ShellOption(defaultValue = "") String key, @ShellOption(defaultValue = "") String value) {
        logger.debug("Entering settings command; key: {}, value: {}", key, value);
        String result;
        if (key.isEmpty()) {
            result = String.format("dataStorage: %s\ndefaultUseOfGps: %s\nkeepHistory: %s\nactivateGui: %s",
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
                    logger.warn("Invalid setting key attempted: {}", key);
                    return "Invalid setting key.";
            }
            settings.saveSettings();
            result = "Setting updated.";
        }
        logger.info("Exiting settings command with result: {}", result);
        return result;
    }

    @ShellMethod(value = "Help for settings command", key = "helpSettings")
    public String helpSettings() {
        logger.debug("Entering helpSettings command");
        String helpMessage = "Settings command usage:\n" +
                "settings - Display all settings\n" +
                "settings <key> <value> - Update a setting\n" +
                "Available keys:\n" +
                "dataStorage - Path to data storage directory\n" +
                "defaultUseOfGps - true/false to enable/disable GPS by default\n" +
                "keepHistory - true/false to keep or delete data from the last run\n" +
                "activateGui - true/false to activate or deactivate the GUI by default";
        logger.info("HelpSettings command executed");
        logger.debug("Exiting helpSettings command");
        return helpMessage;
    }

    @ShellMethod(value = "Exit the application", key = "exit")
    public void exit() {
        logger.debug("Entering exit command");
        SpringApplication.exit(context, () -> 0);
        logger.info("Exit command executed");
        logger.debug("Exiting exit command");
        System.exit(0);
    }

    @ShellMethod(value= "Exit the application", key = "quit")
    public void quit() {
        exit();
    }
}