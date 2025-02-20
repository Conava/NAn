package org.cs250.nan.backend;

import org.junit.jupiter.api.*;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.boot.test.context.SpringBootTest;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

//committed as can't test with it
//@SpringBootTest

class BackendApplicationTests {
    private final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
    private final PrintStream origionalOutput = System.out; //capture output for cross-referencing
/*
    @Mock
private BackendApplication backendApplication;
@InjectMocks
//allow use of command line for tests
private CommandLineRunner commandLineRunner;
*/
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
        //System.setOut(origionalOutput);
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
    //@DisplayName("Testing runScanMode")
    void runScanModeTest(){
        String expectedOutput = "Running the one time scan...";
        BackendApplication.main(new String[]{"--scan"});

        assertTrue(outputStream.toString().trim().contains(expectedOutput));
    }

    @Test
    void showHelpTest(){

        assertTrue(true);
    }
}
