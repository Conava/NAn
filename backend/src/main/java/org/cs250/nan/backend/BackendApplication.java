package org.cs250.nan.backend;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;

import java.util.Scanner;

@SpringBootApplication
public class BackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(BackendApplication.class, args);
    }

    @Bean
    public CommandLineRunner commandLineRunner(ApplicationContext ctx) {
        return args -> {
            if (args.length > 0) {
                // Check the first argument
                String arg = args[0];
                switch (arg) {
                    case "-s":
                    case "--scan":
                        runScanMode();
                        break;
                    case "-h":
                    case "--help":
                        showHelp();
                        break;
                    default:
                        System.out.println("Unknown parameter: " + arg);
                        showHelp();
                        break;
                }
            } else {
                // No parameters => interactive mode
                runInteractiveMode();
            }
        };
    }

    private void runScanMode() {
        // Implement logic for your scanning operation here
        // e.g., scanning a directory, reading config, etc.
        System.out.println("Running in scan mode...");
        // ...
    }

    private void showHelp() {
        System.out.println("Usage:");
        System.out.println("  -s, --scan   Start application in scan mode.");
        System.out.println("  -h, --help   Show help message.");
        System.out.println("  (no args)    Start application in interactive mode.");
        // ...
    }

    private void runInteractiveMode() {
        System.out.println("Interactive mode started. Type commands (type 'exit' to quit).");

        Scanner scanner = new Scanner(System.in);
        boolean running = true;
        while (running) {
            System.out.print(">>> ");
            String command = scanner.nextLine().trim();

            switch (command) {
                case "exit":
                    running = false;
                    break;
                case "hello":
                    System.out.println("Hello there!");
                    break;
                // Add more commands here
                default:
                    System.out.println("Unknown command: " + command);
            }
        }
        scanner.close();
        System.out.println("Interactive mode ended.");
    }


}
