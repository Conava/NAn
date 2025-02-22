package org.cs250.nan.backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class BackendApplication {

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
        SpringApplication app = new SpringApplication(BackendApplication.class);
        app.run(args);
    }

    private static void runScanMode() {
        System.out.println("Running the one time scan...");
        // TODO: implement the scanner and use it here once and print the result
    }

    private static void showHelp() {
        System.out.println("Usage:");
        System.out.println("  -s, --scan   Start application in scan mode.");
        System.out.println("  -h, --help   Show help message.");
        System.out.println("  (no args)    Start application in interactive mode.");
    }
}
