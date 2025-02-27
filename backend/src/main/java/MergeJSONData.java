import org.json.JSONObject; // necessary dependency to work with JSONObjects, I've added them to pom.xml/maven but not sure if it will work. May need to add the .jar file manually, let me know if there are issues (mason)
import java.util.List;

//This class has one method that takes the output from ParseWiFiDataWindows and ParseGPSData and merges the json info into a list of JSON objects, such that each WiFi json object gains all the gps data
public class MergeJSONData {

    public static List<JSONObject> mergeJSONObjects(List<JSONObject> jsonList, JSONObject json2) {
        // Iterate over each WiFi JSON object in the list
        for (JSONObject jsonObj : jsonList) {
            // Iterate over each key in json2
            for (String key : json2.keySet()) {
                // Merge key-value pairs from json2 into each jsonObj
                jsonObj.put(key, json2.get(key));
            }
        }
        return jsonList; //the argument provided for jsonList is modified, i.e. each item in that list gains the gps data, but an explicit return will likely be easier to work with
    }

    //For testing purposes only, if you run main it will use the included GPS NMEA sentences below as the input to json2 and the included WiFi data from class ParseWiFiDataWindows as the input for jsonList
    public static void main(String[] args) {
        String gpsData = "$GPGSA,A,3,29,05,18,23,25,15,13,26,20,12,,,1.13,0.79,0.81*04\n" +
                "$GPRMC,015455.000,A,3244.8110,N,11707.5409,W,0.11,218.56,260225,,,D*71\n" +
                "$GPGGA,015456.000,3244.8109,N,11707.5409,W,2,10,0.79,128.5,M,-35.8,M,,0000*55"; //hard coded sample data, for testing only

        ParseWiFiDataWindows wifiParser = new ParseWiFiDataWindows(); //create instance of ParseWiFiDataWindows to provide input into jsonList parameter of mergeJSONObjects method
        ParseGPSData gpsParser = new ParseGPSData(); //create instance of ParseGPSData to parse the included string above (String gpsData) into the correct json format (String gpsData)
        List<JSONObject> mergedJSONObjectList = wifiParser.parseStringToListOfJSON(); //using the parseStringToListOfJSON() method from class ParseWiFiDataWindows, generate the list of WiFi json objects from the sample data included in ParseWiFiDataWindows for testing purposes only
        mergeJSONObjects(mergedJSONObjectList, gpsParser.parseStringToListOfJSON(gpsData)); //add the gps data to each WiFi json object in mergedJSONObjectList, the return is not used in this case, but rather the variable mergedJSONObjectList, provided as the first argument since it is modified by the method

        for (JSONObject jsonObj : mergedJSONObjectList) { //display the final JSON object
            // Pretty print with an indentation factor of 4 spaces
            System.out.println(jsonObj.toString(4));
        }
    }
}
