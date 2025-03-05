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

    @Test
    void contextLoads() {
        assertTrue(true, "\"Context should load without errors\"");
    }


    @Test
    @DisplayName("Test with unsupported command line parameter 'invalidParam'")
    void unknownCommandLineParameterTest() throws IOException, InterruptedException {
        ProcessBuilder processBuilder = new ProcessBuilder(
                "mvn", "spring-boot:run", "-Dspring-boot.run.arguments=invalidParam"
        );
        processBuilder.redirectErrorStream(true);
        Process subProcess = processBuilder.start();

        // Capture output
        BufferedReader reader = new BufferedReader(new InputStreamReader(subProcess.getInputStream()));
        StringBuilder output = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            output.append(line).append("\n");
        }

        // Verify process exit
        int exitCode = subProcess.waitFor();
        assertEquals(1, exitCode, "Process should exit with code 1");

        // Check for expected error message
        assertTrue(output.toString().contains("Unknown command line parameter:"),
                "Expected 'Unknown command line parameter' not found!");
    }

    @Test
    void springApplicationTest() {
        assertTrue(true);
    }
}
