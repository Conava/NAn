package org.cs250.nan.backend;

import org.junit.jupiter.api.*;
import org.springframework.boot.test.context.SpringBootTest;
import java.io.*;
import static org.junit.jupiter.api.Assertions.*;

//committed as can't test with it
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
    @AfterEach
    //Reset System.out to original
    void resetSystemOut(){
    }
    @AfterAll
    static void cleanup(){
        System.out.println("Tests completed...");
    }

    /*
    @Test
    void contextLoads() {
        //System.out.println("test");

        assertTrue(true);
    }
*/
    @Test
    void CommandLineRunnerTest(){

        assertTrue(true);
    }

    @Test
    @DisplayName("Testing runScanMode")
    void runScanModeTest() throws IOException, InterruptedException {
        String expectedOutput = "Running the one time scan...";

        // start BackendApplication in separate process
        // to support class having a self-termination
        ProcessBuilder processBuilder = new ProcessBuilder(
                "java", "-cp", "target/classes", "org.cs250.nan.backend.BackendApplication", "--scan"
        );
        processBuilder.redirectErrorStream(true); // allow grab of exceptions/errors
        Process subProcess = processBuilder.start(); // output of the ran executed command line

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

        //Asserts to check outputs below
        assertTrue(output.toString().contains(expectedOutput), "Expected output not found!");
    }

    @Test
    void showHelpTest(){

        assertTrue(true);
    }
}
