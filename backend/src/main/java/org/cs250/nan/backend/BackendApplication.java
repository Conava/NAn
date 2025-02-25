package org.cs250.nan.backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Main application entry point for the backend.
 * <p>
 * This class starts the Spring Boot application.
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
        printEnvironmentDetails();
        SpringApplication.run(BackendApplication.class, args);
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
}