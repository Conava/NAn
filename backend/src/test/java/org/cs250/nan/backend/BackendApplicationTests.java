package org.cs250.nan.backend;

import org.junit.jupiter.api.*;
import org.springframework.boot.test.context.SpringBootTest;
import java.io.*;
import static org.junit.jupiter.api.Assertions.*;

/*
Notes:
can't create subProcess in @BeforeEach due to declaring Process builder each time for each unique command line
TODO: test with no input
TODO: test SpringApplication with team
 */

//commented as can't test with it
//@SpringBootTest
class BackendApplicationTests {
    @BeforeAll
    static void loadInitialMain(){
        System.out.println("Initializing tests:");
    }
    @BeforeEach
    //create new instance / clean up data before next test
    void backendRefresh(){
        System.out.println("Creating new instance:");
    }
    @AfterAll
    static void cleanup(){
        System.out.println("Tests completed...");
    }

    @Test
    void springApplicationTest() {
        //System.out.println("test");

        assertTrue(true);
    }

    @Test
    @DisplayName("Test with anything that isn't a assigned command")
    void mainTest() throws IOException, InterruptedException {
        // start BackendApplication in separate process
        // to support class having a self-termination
        ProcessBuilder processBuilder = new ProcessBuilder(
                "java", "-cp", "target/classes", "org.cs250.nan.backend.BackendApplication", ""
        );
        processBuilder.redirectErrorStream(true); // allow grab of exceptions/errors
        Process subProcess = processBuilder.start(); // inject and perform commandline

        // Capture output from subProcess
        BufferedReader reader = new BufferedReader(new InputStreamReader(subProcess.getInputStream()));
        StringBuilder output = new StringBuilder();

        //Read each line from subProcess output, append to string for testing
        String line;
        while ((line = reader.readLine()) != null) {
            output.append(line).append("\n");
        }

        int exitCode = subProcess.waitFor(); // ensure subProcess was completed
        assertEquals(1, exitCode, "Process should exit with code 1");

        // expected outputs
        String expectedOutput = "Unknown parameter: ";

        // asserts to check outputs
        assertTrue(output.toString().contains(expectedOutput), "Expected output not found!");
    }

    @Test
    @DisplayName("Testing runScanMode")
    void runScanModeTest() throws IOException, InterruptedException {
        // start BackendApplication in separate process
        // to support class having a self-termination
        ProcessBuilder processBuilder = new ProcessBuilder(
                "java", "-cp", "target/classes", "org.cs250.nan.backend.BackendApplication", "--scan"
        );
        processBuilder.redirectErrorStream(true); // allow grab of exceptions/errors
        Process subProcess = processBuilder.start(); // inject and perform commandline

        // Capture output from subProcess
        BufferedReader reader = new BufferedReader(new InputStreamReader(subProcess.getInputStream()));
        StringBuilder output = new StringBuilder();

        //Read each line from subProcess output, append to string for testing
        String line;
        while ((line = reader.readLine()) != null) {
            output.append(line).append("\n");
        }

        int exitCode = subProcess.waitFor(); // ensure subProcess was completed
        assertEquals(0, exitCode, "Process should exit with code 0");

        //expected outputs
        String expectedOutput = "Running the one time scan...";

        //Asserts to check outputs below
        assertTrue(output.toString().contains(expectedOutput), "Expected output not found!");
    }

    @Test
    void showHelpTest() throws IOException, InterruptedException {
        // start BackendApplication in separate process
        // to support class having a self-termination
        ProcessBuilder processBuilder = new ProcessBuilder(
                "java", "-cp", "target/classes", "org.cs250.nan.backend.BackendApplication", "--help"
        );
        processBuilder.redirectErrorStream(true); // allow grab of exceptions/errors
        Process subProcess = processBuilder.start(); // inject and perform commandline

        // Capture output from subProcess
        BufferedReader reader = new BufferedReader(new InputStreamReader(subProcess.getInputStream()));
        StringBuilder output = new StringBuilder();

        //Read each line from subProcess output, append to string for testing
        String line;
        while ((line = reader.readLine()) != null) {
            output.append(line).append("\n");
        }

        int exitCode = subProcess.waitFor(); // ensure subProcess was completed
        assertEquals(0, exitCode, "Process should exit with code 0");

        // expected outputs
        String expectedOutput = """
                Usage:
                  -s, --scan   Start application in scan mode.
                  -h, --help   Show help message.
                  (no args)    Start application in interactive mode.
                  """;
        // asserts to check outputs
        assertTrue(output.toString().contains(expectedOutput), "Expected output not found!");
    }
}
