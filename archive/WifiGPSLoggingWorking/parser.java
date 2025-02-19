import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import org.json.JSONObject;

public class parser {



    public static void main(String[] args) {
        String input = """
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
        Map<String, Object> currentMap = null;// = new HashMap<String, Object>();
        Map<String, Object> outerMap = new HashMap<String, Object>();
        Map<String, Object> ssidDataMap = null;// = new HashMap<String, Object>();

        String[] lines = input.split("\n");

        for (int i =0; i <lines.length; ++i) {
            lines[i] = lines[i].trim();
            //System.out.println(lines[i]);
            if (lines[i].startsWith("SSID")) {
                ssidDataMap = new HashMap<String, Object>();
                currentMap = ssidDataMap;
                String[] partsSSID = lines[i].split(":");
//                System.out.print(partsSSID[0]+": ");
//                System.out.println(partsSSID[1]);
                outerMap.put(partsSSID[1].trim(), ssidDataMap);
            }
            else if (lines[i].startsWith("BSSID")) {
                currentMap = ssidDataMap;
                Map<String, Object> bssidMap = new HashMap<String, Object>();
                String[] partsBSSID = lines[i].split(":", 2);
//                System.out.print(partsBSSID[0]+": ");
//                System.out.println(partsBSSID[1]);
                currentMap.put(partsBSSID[1].trim(), bssidMap);
                currentMap = bssidMap;
            }
            else {
                String[] parts = lines[i].split(":");
//                System.out.print(parts[0]+": ");
//                System.out.println(parts[1]);
                currentMap.put(parts[0].trim(), parts[1].trim());
            }
        }
        System.out.println(outerMap);
//        for (int i =0; i <lines.length; ++i) {
//            System.out.println(lines[i]);
//        }
        Map<String, Object> sortedMap = new TreeMap<>(outerMap);
        JSONObject data = new JSONObject(sortedMap);
        System.out.println(data.toString(4));
    }
}