import org.json.JSONObject;
import java.util.HashMap;
import java.util.Map;


public class ParseGPSData {

    private final Map<String, String[]> dataLabels;
    private static final String[] gpggaSentenceLabels = { //fix data, arranged in order as reported by NMEA sentences
            "utcTime", "latitude", "northSouth", "longitude", "eastWest", "positionFix",
            "satelliteCount", "hdop", "altitude", "altitudeUnits", "geoIdSep", "sepUnits",
            "dgpsAge", "dgpsStationId"
    };
    private static final String[] gpgsaSentenceLabels = { //active satellites, arranged in order as reported by NMEA sentences
            "selectionMode", "fixType", "sat01", "sat02", "sat03", "sat04",
            "sat05", "sat06", "sat07", "sat08", "sat09", "sat10",
            "sat11", "sat12", "pdop", "hdop", "vdop"
    };
    private static final String[] gprmcSentenceLabels = { //position and time data, arranged in order as reported by NMEA sentences
            "utcTime", "status", "latitude", "northSouth", "longitude", "eastWest",
            "spdOverGrnd", "courseOverGrnd", "date", "magVariation", "magDirection", "modeIndicator"
    };
    public ParseGPSData() { //constructor to initialize data labels for use in creating the Map holding the GPS data
        dataLabels = new HashMap<>();
        dataLabels.put("GPGGA", gpggaSentenceLabels);
        dataLabels.put("GPGSA", gpgsaSentenceLabels);
        dataLabels.put("GPRMC", gprmcSentenceLabels);
    }


    public JSONObject parseStringToListOfJSON(String gpsData) {

        Map<String, String> gpsDataMap = new HashMap<>();
        String[] indivNMEAsentences = gpsData.split("\n");
        for (String sentence : indivNMEAsentences) {
            sentence = sentence.substring(1, sentence.indexOf('*'));
            String[] sentenceComponents = sentence.split(",");
            String keyToUse = sentenceComponents[0].trim();

            String[] labels = this.dataLabels.get(keyToUse);
            for (int i = 0; i < labels.length; ++i) {
                gpsDataMap.put(labels[i], sentenceComponents[i+1].trim());
            }
        }
        return new JSONObject(gpsDataMap);
    }

    public static void main(String[] args) {
        String gpsData = "$GPGSA,A,3,29,05,18,23,25,15,13,26,20,12,,,1.13,0.79,0.81*04\n" +
                "$GPRMC,015455.000,A,3244.8110,N,11707.5409,W,0.11,218.56,260225,,,D*71\n" +
                "$GPGGA,015456.000,3244.8109,N,11707.5409,W,2,10,0.79,128.5,M,-35.8,M,,0000*55";

        ParseGPSData parser = new ParseGPSData();
        JSONObject ouput = parser.parseStringToListOfJSON(gpsData);
        System.out.println(ouput.toString(4));
    }
}
