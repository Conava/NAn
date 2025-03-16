package org.cs250.nan.backend.shell;

import org.json.JSONObject;
import java.util.HashMap;
import java.util.Map;

public class ParseGPSData {

    private final Map<String, String[]> dataLabels; // these labels are the keys for the ksy-value pairs of the GPS data
    private static final String[] gpggaSentenceLabels = { //"fix data", arranged in order as reported by NMEA sentences
            "timeUTC", "latitude", "northSouth", "longitude", "eastWest", "positionFix",
            "satelliteCount", "hdop", "altitude", "altitudeUnits", "geoIdSep", "sepUnits",
            "dgpsAge", "dgpsStationId"
    };
    private static final String[] gpgsaSentenceLabels = { //"active satellites", arranged in order as reported by NMEA sentences
            "selectionMode", "fixType", "sat01", "sat02", "sat03", "sat04",
            "sat05", "sat06", "sat07", "sat08", "sat09", "sat10",
            "sat11", "sat12", "pdop", "hdop", "vdop"
    };
    private static final String[] gprmcSentenceLabels = { //"position and time data", arranged in order as reported by NMEA sentences
            "timeUTC", "status", "latitude", "northSouth", "longitude", "eastWest",
            "spdOverGrnd", "courseOverGrnd", "dateUTC", "magVariation", "magDirection", "modeIndicator"
    };
    public ParseGPSData() { //constructor to initialize data labels for use in creating the Map holding the GPS data
        dataLabels = new HashMap<>();
        dataLabels.put("GPGGA", gpggaSentenceLabels);
        dataLabels.put("GPGSA", gpgsaSentenceLabels);
        dataLabels.put("GPRMC", gprmcSentenceLabels);
    }


    public JSONObject parseStringToListOfJSON(String gpsData) { // takes in the NMEA sentences and parses them to a map
        Map<String, String> gpsDataMap = new HashMap<>(); // initialize tha mpa
        String[] indivNMEAsentences = gpsData.split("\n"); // split the sentences up
        for (String sentence : indivNMEAsentences) { // go through all sentences
            sentence = sentence.substring(1, sentence.indexOf('*')); // remove the checksum, unnecessary for our needs
            String[] sentenceComponents = sentence.split(",",-1); // separate each data element into an array, -1 prevents skipping empty fields
            String keyToUse = sentenceComponents[0].trim();// remove white-space

            String[] labels = this.dataLabels.get(keyToUse); // determine which set of labels to use based on sentence type
            for (int i = 0; i < labels.length; ++i) {
                gpsDataMap.put(labels[i], sentenceComponents[i+1].trim()); // add the key value pairs to the map
            }
        }
        return new JSONObject(gpsDataMap); // returns a JSON object that will be merged into each associated Wi-Fi JSON object
    }

    // main for testing purposes only and is not representative of all cases of this class' use
    public static void main(String[] args) {
        String gpsData = "$GPGSA,A,3,29,05,18,23,25,15,13,26,20,12,,,1.13,0.79,0.81*04\n" +
                "$GPRMC,015455.000,A,3244.8110,N,11707.5409,W,0.11,218.56,260225,,,D*71\n" +
                "$GPGGA,015456.000,3244.8109,N,11707.5409,W,2,10,0.79,128.5,M,-35.8,M,,0000*55";

        ParseGPSData parser = new ParseGPSData();
        JSONObject output = parser.parseStringToListOfJSON(gpsData);
        System.out.println(output.toString(4));
    }
}
