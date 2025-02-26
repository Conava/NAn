import org.json.JSONObject;
import java.util.List;


public class MergeJSONData {

    public static void mergeJSONObjects(List<JSONObject> jsonList, JSONObject json2) {
        // Iterate over each JSON object in the list
        for (JSONObject jsonObj : jsonList) {
            // Iterate over the keys in json2
            for (String key : json2.keySet()) {
                // Merge key-value pairs from json2 into each jsonObj
                jsonObj.put(key, json2.get(key));
            }
        }
    }

    public static void main(String[] args) {
        String gpsData = "$GPGSA,A,3,29,05,18,23,25,15,13,26,20,12,,,1.13,0.79,0.81*04\n" +
                "$GPRMC,015455.000,A,3244.8110,N,11707.5409,W,0.11,218.56,260225,,,D*71\n" +
                "$GPGGA,015456.000,3244.8109,N,11707.5409,W,2,10,0.79,128.5,M,-35.8,M,,0000*55";

        ParseWiFiDataWindows wifiParser = new ParseWiFiDataWindows();
        ParseGPSData gpsParser = new ParseGPSData();
        List<JSONObject> mergedJSONObjectList = wifiParser.parseStringToListOfJSON();
        mergeJSONObjects(mergedJSONObjectList, gpsParser.parseStringToListOfJSON(gpsData));

        for (JSONObject jsonObj : mergedJSONObjectList) {
            // Pretty print with an indentation factor of 4 spaces
            System.out.println(jsonObj.toString(4));
        }
    }
}
