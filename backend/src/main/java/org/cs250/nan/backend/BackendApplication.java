package org.cs250.nan.backend;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class BackendApplication {

    public static void main(String[] args) {
        SpringApplication app = new SpringApplication(BackendApplication.class);
        app.run(args);
    }

    /**
     * Scans the comamand line arguments of the application and executes the desired command
     * or starts the interactive shell if non specified.
     * @param ctx ApplicationContext
     * @return ComandLineRunner
     */
    @Bean
    public CommandLineRunner commandLineRunner(ApplicationContext ctx) {
        return args -> {
            if (args.length > 0) {
                // Check the first argument only
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
            }
        };
    }

    private void runScanMode() {
        System.out.println("Running the one time scan...");
        // TODO: implement the scanner here
    }

    private void showHelp() {
        System.out.println("Usage:");
        System.out.println("  -s, --scan   Start application in scan mode.");
        System.out.println("  -h, --help   Show help message.");
        System.out.println("  (no args)    Start application in interactive mode.");
    }
}
