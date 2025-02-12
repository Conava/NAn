package org.cs250.nan.backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.Arrays;

@SpringBootApplication
public class BackendApplication {

    public static void main(String[] args) {
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
