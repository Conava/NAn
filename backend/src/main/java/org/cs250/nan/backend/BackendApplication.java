package org.cs250.nan.backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;

/**
 * Entry point for the backend application.
 * <p>
 * This class processes command line parameters to determine the mode in which the application should run.
 * It prints system information such as the operating system, architecture, Java version, user home directory,
 * and current working directory. Based on the provided command line argument, the application can:
 * </p>
 * <ul>
 *   <li>
 *     Run in scan mode (-s or --scan) to perform a one-time scan for WiFi access points using a minimal,
 *     non-web Spring Boot context.
 *   </li>
 *   <li>
 *     Show help (-h or --help) displaying usage instructions.
 *   </li>
 *   <li>
 *     Start an interactive Spring Shell session if no parameters are provided.
 *   </li>
 * </ul>
 *
 * <p>
 * The minimal scan mode uses Spring Boot's dependency injection to retrieve scanning services and then exits.
 * </p>
 */
@SpringBootApplication
public class BackendApplication {

    /**
     * Main method for starting the application.
     * <p>
     * If a command line argument is supplied, the method checks for the following cases:
     * </p>
     * <ul>
     *   <li>
     *     "-s" or "--scan": Executes a one-time scan of WiFi access points and exits.
     *   </li>
     *   <li>
     *     "-h" or "--help": Displays help information and exits.
     *   </li>
     *   <li>
     *     Any other argument will display an unknown parameter message, show help, and exit with an error.
     *   </li>
     * </ul>
     * If no arguments are provided, the interactive Spring Shell is started.
     *
     * @param args command line arguments
     */
    public static void main(String[] args) {
        System.out.println("Starting the application...");

        // Determine operating system and other environment details
        String os = System.getProperty("os.name").toLowerCase();
        String arch = System.getProperty("os.arch");
        String javaVersion = System.getProperty("java.version");
        String userHome = System.getProperty("user.home");
        String userDir = System.getProperty("user.dir");

        System.out.println("Operating System: " + os);
        System.out.println("Architecture: " + arch);
        System.out.println("Java Version: " + javaVersion);
        System.out.println("User Home Directory: " + userHome);
        System.out.println("Current Directory: " + userDir);

        System.out.println("\n");

        if (args.length > 0) {
            switch (args[0]) {
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
                    System.out.println("Unknown parameter: " + args[0]);
                    showHelp();
                    System.exit(1);
            }
        }

        //Start the interactive Spring Shell
        args = new String[0];
        SpringApplication app = new SpringApplication(BackendApplication.class);
        app.run(args);
    }

    /**
     * Runs the application in scan mode.
     * <p>
     * This method creates a minimal, non-web Spring Boot context and is intended to execute a one-time
     * scan for WiFi access points. The scanning service (to be implemented) would be retrieved as a bean,
     * used to perform the scan, and the results would be printed to the console. The context is closed after execution.
     * </p>
     */
    private static void runScanMode() {
        // Use a minimal non\-web context for scanning
        ConfigurableApplicationContext context = new SpringApplicationBuilder(BackendApplication.class)
                .web(WebApplicationType.NONE)
                .run();

        /* TODO: Implement the LocalNetworkScannerService (Naming can be changed)
        LocalNetworkScannerService scannerService = context.getBean(LocalNetworkScannerService.class);
        scannerService.scanNetworks().forEach(System.out::println);
        */
        context.close();
    }

    /**
     * Displays help information.
     * <p>
     * This method outputs the usage instructions for the application, showing available command line parameters:
     * </p>
     * <ul>
     *   <li>
     *     -s, --scan   : Perform a one-time scan for WiFi access points.
     *   </li>
     *   <li>
     *     -h, --help   : Show the help message.
     *   </li>
     *   <li>
     *     (no args)    : Start the application in interactive mode, using the Spring Shell.
     *   </li>
     * </ul>
     */
    private static void showHelp() {
        System.out.println("Usage:");
        System.out.println("  -s, --scan   Perform a one time scan for WiFi access points.");
        System.out.println("  -h, --help   Show this help message.");
        System.out.println("  (no args)    Start application in interactive mode. You may use \"help\" " +
                "again to see available commands in the interactive shell.");
    }
}
