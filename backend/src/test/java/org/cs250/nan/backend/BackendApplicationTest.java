/**
 * Test class for the backend application.
 * <p>
 * Ensures the Spring context loads and verifies behavior for invalid
 * command line parameters when running via Maven.
 */
package org.cs250.nan.backend;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.NONE,
        properties = {
                "spring.shell.interactive.enabled=false",
                "spring.shell.script.enabled=false"
        }
)
class BackendApplicationTest {

    /**
     * Verifies that the Spring application context loads without errors.
     */
    @Test
    void contextLoads() {
        // Simple assertion for context loading
        assertTrue(true, "\"Context should load without errors\"");
    }

    /**
     * Tests the application's reaction to an unsupported command line parameter.
     * @throws IOException if the process fails to start
     * @throws InterruptedException if the process is interrupted
     */
    @Test
    @DisplayName("Test with unsupported command line parameter 'invalidParam'")
    void unknownCommandLineParameterTest() throws IOException, InterruptedException {
        // Determine the OS and run Maven appropriately
        String osName = System.getProperty("os.name").toLowerCase();
        Process subProcess = getProcess(osName);

        // Capture output from the process
        BufferedReader reader = new BufferedReader(new InputStreamReader(subProcess.getInputStream()));
        StringBuilder output = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            output.append(line).append("\n");
        }

        // Verify that the process exits with code 1
        int exitCode = subProcess.waitFor();
        assertEquals(1, exitCode, "Process should exit with code 1");

        // Check for an expected error message
        assertTrue(output.toString().contains("Unknown command line parameter:"),
                "Expected 'Unknown command line parameter' not found!");
    }

    /**
     * Creates and returns a process to run the Spring Boot application based on OS.
     * @param osName the lowercased operating system name
     * @return the started process
     * @throws IOException if the process fails to start
     */
    private static Process getProcess(String osName) throws IOException {
        ProcessBuilder processBuilder;
        if (osName.contains("win")) {
            processBuilder = new ProcessBuilder(
                    "cmd.exe", "/c", "mvn", "spring-boot:run", "-Dspring-boot.run.arguments=invalidParam"
            );
        } else {
            processBuilder = new ProcessBuilder(
                    "mvn", "spring-boot:run", "-Dspring-boot.run.arguments=invalidParam"
            );
        }
        // Redirect the error stream to combine stdout and stderr
        processBuilder.redirectErrorStream(true);
        return processBuilder.start();
    }
}