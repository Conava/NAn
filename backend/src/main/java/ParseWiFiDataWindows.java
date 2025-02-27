import org.json.JSONObject; //required to work, I've included this in the pom.xml file/Maven, but I'm not sure how that works, you might need to manually include the .jar file, let me know if you have issues (mason)
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ParseWiFiDataWindows {

    public List<JSONObject> parseStringToListOfJSON(String wifiData) {
        List<Map<String, String>> uniqueBSSIDs = new ArrayList<>();
        Map<String, String> currentMap = null; // = new HashMap<String, Object>();
        String ssid="", auth="", encryp="";
        String[] lineKeyValPair;

        String[] lines = wifiData.split("\n");

        for (int i =0; i < lines.length; ++i) {
            lines[i] = lines[i].trim();
            if (lines[i].startsWith("SSID")) {
                lineKeyValPair = lines[i].split(":");
                ssid = lineKeyValPair[1].trim();
            }
            else if (lines[i].startsWith("Authentication")) {
                lineKeyValPair = lines[i].split(":");
                auth = lineKeyValPair[1].trim();
            }
            else if (lines[i].startsWith("Encryption")) {
                lineKeyValPair = lines[i].split(":");
                encryp = lineKeyValPair[1].trim();
            }
            else if (lines[i].startsWith("BSSID")) {
                if (currentMap != null) {
                    uniqueBSSIDs.add(currentMap);
                }
                currentMap = new HashMap<String, String>();
                lineKeyValPair = lines[i].split(":", 2);
                currentMap.put("MAC", lineKeyValPair[1].trim());
                currentMap.put("SSID", ssid);
                currentMap.put("Authentication", auth);
                currentMap.put("Encryption", encryp);
            }
            else {
                String[] parts = lines[i].split(":");
                currentMap.put(parts[0].trim(), parts[1].trim());
            }
        }
        uniqueBSSIDs.add(currentMap);

        List<JSONObject> jsonWiFiObjects = new ArrayList<>();

        for (Map<String, String> map : uniqueBSSIDs) {
            JSONObject data = new JSONObject(map);
            jsonWiFiObjects.add(data);
        }
        return jsonWiFiObjects;
    }

    //Overloaded function for testing, not to be used live
    public List<JSONObject> parseStringToListOfJSON() {
        String wifiData = """
                SSID 1 : ATTAuRigg31
                Authentication          : WPA2-Personal
                Encryption              : CCMP
                BSSID 1                 : 10:c4:ca:dd:64:04
                    Signal             : 50%
                    Radio type         : 802.11n
                    Band               : 2.4 GHz
                        Connected Stations:         1
                        Channel Utilization:        121 (47 %)
                BSSID 2                 : 10:c4:ca:dd:64:08
                    Signal             : 83%
                    Radio type         : 802.11ax
                    Band               : 5 GHz
                        Connected Stations:         3
                        Channel Utilization:        97 (38 %)
                BSSID 3                 : 10:c4:ca:dd:64:0c
                    Signal             : 85%
                    Radio type         : 802.11ax
                    Band               : 5 GHz
                        Connected Stations:         2
                        Channel Utilization:        85 (33 %)
            SSID 2 : LwCastle
                Authentication          : WPA2-Personal
                Encryption              : CCMP
                BSSID 1                 : d0:fc:d0:72:5b:2c
                    Signal             : 11%
                    Radio type         : 802.11ax
                    Band               : 5 GHz
                        Connected Stations:         0
                        Channel Utilization:        67 (26 %)
            """;
        return parseStringToListOfJSON(wifiData);
    }

    //Main for testing purposes
    public static void main(String[] args) {
        ParseWiFiDataWindows test = new ParseWiFiDataWindows();
        List<JSONObject> jsonWiFiObjects;
        jsonWiFiObjects = test.parseStringToListOfJSON();
        System.out.println("\n\n\n\n\n");
        for (JSONObject data : jsonWiFiObjects) {
            System.out.println(data.toString(4));
        }
    }

}
