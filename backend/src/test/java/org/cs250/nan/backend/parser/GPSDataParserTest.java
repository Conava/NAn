package org.cs250.nan.backend.parser;

import org.json.JSONException;
import org.json.JSONObject;

import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

public class GPSDataParserTest {

    private String input;
    private JSONObject result;

    @BeforeAll
    static void initial() {

    }

    @BeforeEach
    void setUp() {
        input = null;
        result = null;
        System.out.println("Initializing tests...");
    }

    @AfterAll
    static void cleanup() {
        System.out.println("Tests completed...");
    }

    @Test
    public void testParseValidJson() {
        try {
            input = "{\"lat\": 42.0, \"lon\": -71.0}";
            System.out.println("Input: "+ input);
            result = GPSDataParser.parseStringToJSON(input);

            assertEquals(42.0, result.getDouble("lat"));
            assertEquals(-71.0, result.getDouble("lon"));

            System.out.println(result);

        } catch (JSONException e) {
            fail("Exception thrown in testParseValidJson: " + e.getMessage());
        }
    }

    @Test
    public void testParseInvalidJsonFallsBackToRawGps() {
        try {
            input = "{lat: 42.0, lon: -71.0"; // malformed
            System.out.println("Input: "+ input);
            result = GPSDataParser.parseStringToJSON(input);

            System.out.println(result);

            assertTrue(result.has("rawGps"));
            assertEquals(input, result.getString("rawGps"));

        } catch (JSONException e) {
            fail("Exception thrown in testParseInvalidJsonFallsBackToRawGps: " + e.getMessage());
        }
    }

    @Test
    public void testParseNonJsonString() {
        try {
            input = "$GPGGA,123456.00,4200.00,N,07100.00,W,1,12,1.0,0.0,M,0.0,M,,*5A";
            System.out.println("Input: "+ input);
            result = GPSDataParser.parseStringToJSON(input);

            System.out.println(result);

            assertTrue(result.has("rawGps"));
            assertEquals(input, result.getString("rawGps"));
        } catch (JSONException e) {
            fail("Exception thrown in testParseNonJsonString: " + e.getMessage());
        }
    }

    @Test
    public void testParseNullReturnsEmptyJson() {
        result = GPSDataParser.parseStringToJSON(null);
        System.out.println(result);
        assertEquals(0, result.length());
    }

    @Test
    public void testParseBlankReturnsEmptyJson() {
        result = GPSDataParser.parseStringToJSON("   ");
        System.out.println(result);
        assertEquals(0, result.length());
    }
}