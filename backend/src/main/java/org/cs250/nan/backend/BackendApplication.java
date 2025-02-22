package org.cs250.nan.backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Main application entry point for the backend.
 * <p>
 * This class checks command-line arguments for pre-startup actions
 * (scan mode or help) before starting the Spring Boot application.
 * </p>
 */
@SpringBootApplication
public class BackendApplication {

    /**
     * Application entry point.
     *
     * @param args Command line arguments.
     */
    public static void main(String[] args) {
        processPreStartupCommands(args);
        SpringApplication.run(BackendApplication.class, args);
    }

    /**
     * Processes pre-startup commands based on the provided command-line arguments.
     * <p>
     * If a recognized pre-startup command is detected, the corresponding action is executed
     * and the application exits before the Spring context is started.
     * </p>
     *
     * @param args Command line arguments passed to the application.
     */
    private static void processPreStartupCommands(String[] args) {
        printEnvironmentDetails();
        if (args != null && args.length > 0) {
            String arg = args[0];
            switch (arg) {
                case "-s":
                case "--scan":
                    runScanMode();
                    System.exit(0);
                    break;
                case "-h":
                case "--help":
                    showHelp();
                    System.exit(0);
                    break;
                default:
                    System.out.println("Unknown parameter: " + arg);
                    showHelp();
                    System.exit(1);
            }
        }
    }

    /**
     * Prints the environment details for diagnostic purposes.
     * <p>
     * Displays the operating system, architecture, Java version, user home directory,
     * and current working directory.
     * </p>
     */
    private static void printEnvironmentDetails() {
        System.out.println("Environment Details:");
        System.out.println("Operating System: " + System.getProperty("os.name").toLowerCase());
        System.out.println("Architecture    : " + System.getProperty("os.arch"));
        System.out.println("Java Version    : " + System.getProperty("java.version"));
        System.out.println("User Home Dir   : " + System.getProperty("user.home"));
        System.out.println("Current Dir     : " + System.getProperty("user.dir"));
        System.out.println();
    }

    /**
     * Executes scan mode.
     * <p>
     * The scanning logic should be implemented here once available.
     * </p>
     */
    private static void runScanMode() {
        System.out.println("Running the one-time scan...");
        // TODO: Implement the scanner logic and output the results.
    }

    /**
     * Displays help information with usage instructions.
     */
    private static void showHelp() {
        System.out.println("Usage:");
        System.out.println("  -s, --scan   Perform a one time scan on the WiFi Access Points.");
        System.out.println("  -h, --help   Show help information.");
        System.out.println("  (no args)    Start application in interactive mode. Use 'help'" + " in the interactive mode for more information.");
    }
}