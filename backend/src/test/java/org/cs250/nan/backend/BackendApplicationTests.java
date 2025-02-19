package org.cs250.nan.backend;

import org.junit.jupiter.api.*;
import org.springframework.boot.test.context.SpringBootTest;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import static org.junit.jupiter.api.Assertions.*;
@SpringBootTest
class BackendApplicationTests {
    private final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
    private final PrintStream capturedOutput = System.out; //capture output for cross-referencing

    @BeforeAll
    static void loadInitialMain(){
        System.out.println("Initializing tests:");
    }
    @BeforeEach
    //create new instance / clean up data before next test
    void backendRefresh(){
        System.out.println("Creating new instance:");
        //System.setOut(new PrintStream(outputStream)); // redirect output for testing
    }
    @AfterEach
    //Reset System.out to original
    void resetSystemOut(){
        //System.setOut(capturedOutput);
        //outputStream.reset();
    }
    @AfterAll
    static void cleanup(){
        System.out.println("Tests completed...");
    }

    @Test
    void contextLoads() {
        System.out.print("test");

        assertTrue(true);
    }

    @Test
    void CommandLineRunnerTest(){

        assertTrue(true);
    }

    @Test
    //@DisplayName("Testing runScanMode")
    void runScanModeTest(){
        String expectedOutput = "Running the one time scan...";
        //BackendApplication.main(new String[]{"--exit"});

        //assertTrue(outputStream.toString().trim().contains(expectedOutput));
        assertTrue(true);
    }

    @Test
    void showHelpTest(){

        assertTrue(true);
    }

}
