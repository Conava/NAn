package org.cs250.nan.backend;

import lombok.Getter;
import org.cs250.nan.backend.config.Settings;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

import java.util.List;

/**
 * Main application entry point for the backend.
 * <p>
 * This class starts the Spring Boot application.
 * </p>
 */
@SpringBootApplication
public class BackendApplication {
    private static final Logger LOGGER = LoggerFactory.getLogger(BackendApplication.class);
    private static final List<String> VALID_OPTIONS = List.of(""); // Empty for now as program arguments are not supported.

    /**
     * -- GETTER --
     *  Returns the Spring application context.
     *
     * @return The Spring application context.
     */
    @Getter
    private static ApplicationContext applicationContext;

    @Getter
    private static Settings settings;

    /**
     * Application entry point.
     *
     * @param args Command line arguments.
     */
    public static void main(String[] args) {
        for (String arg : args) {
            if (!VALID_OPTIONS.contains(arg)) {
                System.err.println("Unknown command line parameter: " + arg);
                System.exit(1);
            }
        }
        settings = new Settings();
        System.setProperty("logging.file.name", settings.getLogFilePath());
        LOGGER.info("Configured log file path: {}", settings.getLogFilePath());
        LOGGER.info("Starting NAn NetworkAnalyzer...");
        LOGGER.info(getEnvironmentDetails());
        applicationContext = SpringApplication.run(BackendApplication.class, args);
        LOGGER.info("NAn started successfully.");
    }

    /**
     * Prints the environment details for diagnostic purposes.
     * <p>
     * Displays the operating system, architecture, Java version, user home directory,
     * and current working directory.
     * </p>
     */
    private static String getEnvironmentDetails() {
        return "Environment Details:\n" +
                "Operating System: " + System.getProperty("os.name").toLowerCase() + "\n" +
                "Architecture    : " + System.getProperty("os.arch") + "\n" +
                "Java Version    : " + System.getProperty("java.version") + "\n" +
                "User Home Dir   : " + System.getProperty("user.home") + "\n" +
                "Current Dir     : " + System.getProperty("user.dir") + "\n";
    }
}