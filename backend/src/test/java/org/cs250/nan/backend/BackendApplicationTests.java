package org.cs250.nan.backend;

import org.junit.jupiter.api.*;
import java.io.*;
import org.springframework.boot.test.context.SpringBootTest;
import static org.junit.jupiter.api.Assertions.*;

/*
Notes:
can't create subProcess in @BeforeEach due to declaring Process builder each time for each unique command line
TODO: create test for SpringApplication with team
TODO: create test for printEnvironmentalDetails for exceptions (very low priority)
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
    void preTest(){
        System.out.print("Performing new test: ");
    }
    @AfterEach
    void afterTest(){
        System.out.println("Done");
    }
    @AfterAll
    static void cleanup(){
        System.out.println("Tests completed...");
    }
    @Test
    @DisplayName("Test with anything that isn't an assigned command")
    void unknownParaTest() throws IOException, InterruptedException {
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

        // ensure subProcess was completed
        int exitCode = subProcess.waitFor();
        assertEquals(1, exitCode, "Process should exit with code 1");

        // expected outputs
        String expectedOutput = "Unknown parameter: ";

        // asserts to check outputs
        assertTrue(output.toString().contains(expectedOutput), "Expected output not found!");
    }

    @Test
    @DisplayName("Testing runScanMode method via commandline")
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
        String expectedOutput = "Running the one-time scan...";

        //Asserts to check outputs below
        assertTrue(output.toString().contains(expectedOutput), "Expected output not found!");
    }

    @Test
    @DisplayName("Test showHelp method via commandline")
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
                  -s, --scan   Perform a one time scan on the WiFi Access Points.
                  -h, --help   Show help information.
                  (no args)    Start application in interactive mode. Use 'help' in the interactive mode for more information.
                   """;
        // asserts to check outputs
        assertTrue(output.toString().contains(expectedOutput), "Expected output not found!");
    }
    @Test
    void springApplicationTest() {
        assertTrue(true);
    }
}
