package org.cs250.nan.backend.shell;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ParseWiFiDataWindows {
//this class takes in the string output from SingleWiFiScanWindows and converts it into JSON format

    public static List<JSONObject> parseStringToListOfJSON(String wifiData) {
        List<Map<String, String>> uniqueBSSIDs = new ArrayList<>(); //initialize the list of maps; these maps will each
        //become an individual JSON object, uniquely identified by their individual "MAC" key-value pairs
        Map<String, String> currentMap = null; //initialize a map to hold individual sets of WiFi data, based on BSSID/MAC
        String ssid="", auth="", encryp=""; //varaibles to hold data that is constant for a series of BSSIDs (MACs). This
        //is necessary becasue the scan produced a set of data per SSID, but each SSID may have multiple WiFi interfaces
        //associated with them (for example 2 antennas, one 2.4 GHz, and one 5 GHz). The anetenna are different devices,
        //but they rely on common services/data, specifically SSID, authentication type, encryption type. These values
        //don't change for each unique BSSID (MAC) asslociated with the SSID.
        String[] lineKeyValPair; //initialize string array that will hold a key in index 0 and it's value in index 1

        String[] lines = wifiData.split("\n"); //split the provided argument into individual lines

        for (int i =0; i < lines.length; ++i) { //iterate throguh all the lines
            lines[i] = lines[i].trim(); //time leading and trailing whitespace from each line
            if (lines[i].startsWith("SSID")) { //check for lines that start with SSID
                lineKeyValPair = lines[i].split(":"); //split that line, capturing the SSID name in index 1
                ssid = lineKeyValPair[1].trim(); //set the SSID value; to be added to each susequent BSSID/MAC associated
                //with it until a new SSID is encountered
            }
            else if (lines[i].startsWith("Authentication")) { //check for lines that start with Authentication
                lineKeyValPair = lines[i].split(":");
                auth = lineKeyValPair[1].trim(); //set the auth value; to be added to each susequent BSSID/MAC associated
                //with it until a new SSID is encountered and the auth value becomes updated
            }
            else if (lines[i].startsWith("Encryption")) { //check for lines that start with Encryption
                lineKeyValPair = lines[i].split(":");
                encryp = lineKeyValPair[1].trim(); //set the encryp value; to be added to each susequent BSSID/MAC associated
                //with it until a new SSID is encountered and the encryp value becomes updated
            }
            else if (lines[i].startsWith("BSSID")) { //check for lines that start with BSSID
                if (currentMap != null) { //if the current map is not empty, that means this is not first BSSID encountered.
                    // We need to add the current, and now complete map to the map list
                    uniqueBSSIDs.add(currentMap);
                }

                currentMap = new HashMap<String, String>(); // create a new map, and assign it to the currentMap var
                lineKeyValPair = lines[i].split(":", 2); //split the BSSID line; the 2 keeps the line from spliting at
                //every ":", since the value of the BSSID line has multiple ":", rather it only splits at the first
                //occurrence of ":", into 2 elements total
                currentMap.put("MAC", lineKeyValPair[1].trim()); //add the value of the BSSID to the map with key "MAC"
                currentMap.put("SSID", ssid); //add the previously collected values for ssid, auth, and encrpytion
                currentMap.put("Authentication", auth);
                currentMap.put("Encryption", encryp);
            }
            else { //for all other lines, simple split them at the ":", remove whitespace and add to the map
                String[] parts = lines[i].split(":");
                currentMap.put(parts[0].trim(), parts[1].trim());
            }
        }
        uniqueBSSIDs.add(currentMap); //adter all lines ahve been processed, add the final constructed map to the map list

        List<JSONObject> jsonWiFiObjects = new ArrayList<>(); //initilaize a list of JSON objects

        for (Map<String, String> map : uniqueBSSIDs) { //go through each map
            JSONObject data = new JSONObject(map); //create a JSON object from the map
            jsonWiFiObjects.add(data); //add the JSON object to the JSON object list
        }
        return jsonWiFiObjects; //return the lsit of JSON objects
    }

    //Overloaded method for testing, not to be used live; below, a sample string is provided to be parsed
    //if no string is provided to the primary method, this overloaded metho will execute instead
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

    //main for testing purposes only
    public static void main(String[] args) {
        ParseWiFiDataWindows test = new ParseWiFiDataWindows(); //create new instace of the class
        List<JSONObject> jsonWiFiObjects; // create list of JSONObjects to hold the method output
        jsonWiFiObjects = test.parseStringToListOfJSON(); //run the overloaded function to parse the smaple string
        System.out.println("\n\n\n\n\n");
        for (JSONObject data : jsonWiFiObjects) { //iterate through the JSON objects, printing each
            System.out.println(data.toString(4));
        }
    }
}