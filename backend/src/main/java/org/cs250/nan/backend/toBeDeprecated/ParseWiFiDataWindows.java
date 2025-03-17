package org.cs250.nan.backend.toBeDeprecated;

import org.json.JSONObject;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

//todo: Merge logic into one class in the parser package, then delete this class.
public class ParseWiFiDataWindows {
//this class takes in the string output from SingleWiFiScanWindows and converts it into JSON format

    public static List<JSONObject> parseStringToListOfJSON(String wifiData) {
        List<Map<String, String>> uniqueBSSIDs = new ArrayList<>(); //initialize the list of maps; these maps will each
        //become an individual JSON object, uniquely identified by their individual "MAC" key-value pairs
        Map<String, String> currentMap = null; //initialize a map to hold individual sets of Wi-Fi data, based on BSSID/MAC
        String ssid="", auth="", encryp=""; //variables to hold data that is constant for a series of BSSIDs (MACs). This
        //is necessary because the scan produced a set of data per SSID, but each SSID may have multiple Wi-Fi interfaces
        //associated with them (for example 2 antennas, one 2.4 GHz, and one 5 GHz). The antenna are different devices,
        //but they rely on common services/data, specifically SSID, authentication type, encryption type. These values
        //don't change for each unique BSSID (MAC) associated with the SSID.
        String[] lineKeyValPair; //initialize string array that will hold a key in index 0, and it's value in index 1

        String[] lines = wifiData.split("\n"); //split the provided argument into individual lines
        boolean firstSSIDFound = false;

        for (int i = 0; i < lines.length; ++i) { //iterate through all the lines
            lines[i] = lines[i].trim(); //time leading and trailing whitespace from each line
            if (!lines[i].startsWith("SSID") && !firstSSIDFound) { // Skips lines until the first SSID is encountered
                continue;
            }
            else {
                firstSSIDFound = true;
            }
            if (lines[i].startsWith("SSID")) { //check for lines that start with SSID
                lineKeyValPair = lines[i].split(":"); //split that line, capturing the SSID name in index 1
                if (lineKeyValPair.length == 2) {
                    ssid = lineKeyValPair[1].trim(); //set the SSID value; to be added to each subsequent BSSID/MAC associated
                    //with it until a new SSID is encountered
                }
                else {
                    ssid = ""; //if the entry is blank from the netsh scan, store an empty string
                }
            }
            else if (lines[i].startsWith("Authentication")) { //check for lines that start with Authentication
                lineKeyValPair = lines[i].split(":");
                if (lineKeyValPair.length == 2) {
                    auth = lineKeyValPair[1].trim(); //set the auth value; to be added to each subsequent BSSID/MAC associated
                    //with it until a new SSID is encountered and the auth value becomes updated
                }
                else {
                    auth = ""; //if the entry is blank from the netsh scan, store an empty string
                }
            }
            else if (lines[i].startsWith("Encryption")) { //check for lines that start with Encryption
                lineKeyValPair = lines[i].split(":");
                if (lineKeyValPair.length == 2) {
                    encryp = lineKeyValPair[1].trim(); //set the encrypt value; to be added to each subsequent BSSID/MAC associated
                    //with it until a new SSID is encountered and the encrypt value becomes updated
                }
                else {
                    encryp = ""; //if the entry is blank from the netsh scan, store an empty string
                }
            }
            else if (lines[i].startsWith("BSSID")) { //check for lines that start with BSSID
                if (currentMap != null) { //if the current map is not empty, that means this is not first BSSID encountered.
                    // We need to add the current, and now complete map to the map list
                    uniqueBSSIDs.add(currentMap);
                }

                currentMap = new HashMap<String, String>(); // create a new map, and assign it to the currentMap var
                lineKeyValPair = lines[i].split(":", 2); //split the BSSID line; the 2 keeps the line from splitting at
                //every ":", since the value of the BSSID line has multiple ":", rather it only splits at the first
                //occurrence of ":", into 2 elements total
                if (lineKeyValPair.length == 2) {
                    currentMap.put("MAC", lineKeyValPair[1].trim()); //add the value of the BSSID to the map with key "MAC"
                }
                else {
                    currentMap.put("MAC", ""); //if the entry is blank from the netsh scan, store an empty string
                }
                currentMap.put("SSID", ssid); //add the previously collected values for ssid, auth, and encryption
                currentMap.put("Authentication", auth);
                currentMap.put("Encryption", encryp);

                // Get the local time in "HHmmss.SSS" format
                String timeFormatted = LocalTime.now().format(DateTimeFormatter.ofPattern("HHmmss.SSS"));
                currentMap.put("timeLocal", timeFormatted);

                // Get the local date in "ddMMyy" format
                String dateFormatted = LocalDate.now().format(DateTimeFormatter.ofPattern("ddMMyy"));
                currentMap.put("dateLocal", dateFormatted);

            }
            else { //for all other lines, simple split them at the ":", remove whitespace and add to the map
                String[] parts = lines[i].split(":");
                if (parts.length == 2) {
                    currentMap.put(parts[0].trim(), parts[1].trim());
                }
                else {
                    currentMap.put(parts[0].trim(), ""); //if the entry is blank from the netsh scan, store an empty string
                }
            }
        }
        uniqueBSSIDs.add(currentMap); //after all lines have been processed, add the final constructed map to the map list

        List<JSONObject> jsonWiFiObjects = new ArrayList<>(); //initialize a list of JSON objects

        for (Map<String, String> map : uniqueBSSIDs) { //go through each map
            JSONObject data = new JSONObject(map); //create a JSON object from the map
            jsonWiFiObjects.add(data); //add the JSON object to the JSON object list
        }
        return jsonWiFiObjects; //return the list of JSON objects
    }

    //Overloaded method for testing, not to be used live; below, a sample string is provided to be parsed
    //if no string is provided to the primary method, this overloaded method will execute instead
    public static List<JSONObject> parseStringToListOfJSON() {
        String wifiData = """
                Usage: show networks [[interface=]<string>] [[mode=]ssid/bssid]
                        mode          - Get detailed bssid information.
                        Parameter interface and bssid are both optional.
                        If mode=bssid is given then the visible bssids for each ssid
                        will also be listed. Otherwise only ssids will be listed.
                        show networks mode=Bssid
                
                
                SSID 1 : SDSU_Guest
                        Authentication          : Open
                        Encryption              : None\s
                        BSSID 1                 : 84:d4:7e:69:95:b1
                             Signal             : 35% \s
                             Radio type         : 802.11ac
                             Band               : 5 GHz
                        BSSID 2                 : 84:d4:7e:69:8a:01
                             Signal             : 65% \s
                             Radio type         : 802.11n
                             Band               : 2.4 GHz
                        BSSID 3                 : 84:d4:7e:69:7d:01
                             Signal             : 29% \s
                             Radio type         : 802.11n
                             Band               : 2.4 GHz
                        BSSID 4                 : 84:d4:7e:69:87:81
                             Signal             : 35% \s
                             Radio type         : 802.11n
                             Band               : 2.4 GHz
                        BSSID 5                 : 84:d4:7e:69:89:91
                             Signal             : 26% \s
                             Radio type         : 802.11ac
                             Band               : 5 GHz
                        BSSID 6                 : 84:d4:7e:69:95:a1
                             Signal             : 40% \s
                             Radio type         : 802.11n
                             Band               : 2.4 GHz
                        BSSID 7                 : 84:d4:7e:69:89:81
                             Signal             : 50% \s
                             Radio type         : 802.11n
                             Band               : 2.4 GHz
                        BSSID 8                 : 84:d4:7e:69:8a:11
                             Signal             : 26% \s
                             Radio type         : 802.11ac
                             Band               : 5 GHz
                        BSSID 9                 : 84:d4:7e:69:8e:01
                             Signal             : 72% \s
                             Radio type         : 802.11n
                             Band               : 2.4 GHz
                        BSSID 10                 : 84:d4:7e:69:83:21
                             Signal             : 72% \s
                             Radio type         : 802.11n
                             Band               : 2.4 GHz
                        BSSID 11                 : 7c:57:3c:be:5e:41
                             Signal             : 40% \s
                             Radio type         : 802.11ac
                             Band               : 5 GHz
                        BSSID 12                 : 84:d4:7e:69:7d:71
                             Signal             : 35% \s
                             Radio type         : 802.11ac
                             Band               : 5 GHz
                        BSSID 13                 : 84:d4:7e:69:82:91
                             Signal             : 82% \s
                             Radio type         : 802.11ac
                             Band               : 5 GHz
                        BSSID 14                 : 84:d4:7e:69:83:31
                             Signal             : 83% \s
                             Radio type         : 802.11ac
                             Band               : 5 GHz
                        BSSID 15                 : 84:d4:7e:69:8e:11
                             Signal             : 57% \s
                             Radio type         : 802.11ac
                             Band               : 5 GHz
                        BSSID 16                 : 84:d4:7e:69:7d:11
                             Signal             : 20% \s
                             Radio type         : 802.11ac
                             Band               : 5 GHz
                        BSSID 17                 : 84:d4:7e:69:80:71
                             Signal             : 80% \s
                             Radio type         : 802.11ac
                             Band               : 5 GHz
                        BSSID 18                 : 7c:57:3c:be:5e:51
                             Signal             : 35% \s
                             Radio type         : 802.11ac
                             Band               : 5 GHz
                        BSSID 19                 : 84:d4:7e:69:80:61
                             Signal             : 86% \s
                             Radio type         : 802.11n
                             Band               : 2.4 GHz
                        BSSID 20                 : 84:d4:7e:69:82:81
                             Signal             : 87% \s
                             Radio type         : 802.11n
                             Band               : 2.4 GHz
                    SSID 2 : eduroam
                        Authentication          : WPA2-Enterprise
                        Encryption              : CCMP\s
                        BSSID 1                 : 84:d4:7e:69:95:b0
                             Signal             : 33% \s
                             Radio type         : 802.11ac
                             Band               : 5 GHz
                        BSSID 2                 : 84:d4:7e:69:8a:00
                             Signal             : 65% \s
                             Radio type         : 802.11n
                             Band               : 2.4 GHz
                        BSSID 3                 : 84:d4:7e:69:89:80
                             Signal             : 50% \s
                             Radio type         : 802.11n
                             Band               : 2.4 GHz
                        BSSID 4                 : 84:d4:7e:69:95:a0
                             Signal             : 40% \s
                             Radio type         : 802.11n
                             Band               : 2.4 GHz
                        BSSID 5                 : 84:d4:7e:69:89:90
                             Signal             : 24% \s
                             Radio type         : 802.11ac
                             Band               : 5 GHz
                        BSSID 6                 : 84:d4:7e:69:7d:70
                             Signal             : 40% \s
                             Radio type         : 802.11ac
                             Band               : 5 GHz
                        BSSID 7                 : 84:d4:7e:69:8e:00
                             Signal             : 75% \s
                             Radio type         : 802.11n
                             Band               : 2.4 GHz
                        BSSID 8                 : fc:7f:f1:f9:01:30
                             Signal             : 24% \s
                             Radio type         : 802.11ax
                             Band               : 5 GHz
                        BSSID 9                 : 84:d4:7e:69:83:20
                             Signal             : 72% \s
                             Radio type         : 802.11n
                             Band               : 2.4 GHz
                        BSSID 10                 : 84:d4:7e:69:82:90
                             Signal             : 82% \s
                             Radio type         : 802.11ac
                             Band               : 5 GHz
                        BSSID 11                 : 84:d4:7e:69:83:30
                             Signal             : 80% \s
                             Radio type         : 802.11ac
                             Band               : 5 GHz
                        BSSID 12                 : 84:d4:7e:69:8e:10
                             Signal             : 57% \s
                             Radio type         : 802.11ac
                             Band               : 5 GHz
                        BSSID 13                 : 84:d4:7e:69:7d:10
                             Signal             : 22% \s
                             Radio type         : 802.11ac
                             Band               : 5 GHz
                        BSSID 14                 : 84:d4:7e:69:80:70
                             Signal             : 80% \s
                             Radio type         : 802.11ac
                             Band               : 5 GHz
                        BSSID 15                 : 7c:57:3c:be:5e:50
                             Signal             : 33% \s
                             Radio type         : 802.11ac
                             Band               : 5 GHz
                        BSSID 16                 : 7c:57:3c:be:5e:40
                             Signal             : 35% \s
                             Radio type         : 802.11ac
                             Band               : 5 GHz
                        BSSID 17                 : 84:d4:7e:69:80:60
                             Signal             : 86% \s
                             Radio type         : 802.11n
                             Band               : 2.4 GHz
                        BSSID 18                 : 84:d4:7e:69:82:80
                             Signal             : 87% \s
                             Radio type         : 802.11n
                             Band               : 2.4 GHz
                    SSID 3 : HP-Print-29-LaserJet 400 color
                        Authentication          : Open
                        Encryption              : None\s
                        BSSID 1                 : 08:3e:8e:72:b3:29
                             Signal             : 29% \s
                             Radio type         : 802.11g
                             Band               : 2.4 GHz
                    SSID 4 : DIRECT-9C-HP DeskJet 4100 series
                        Authentication          : WPA2-Personal
                        Encryption              : CCMP\s
                        BSSID 1                 : 50:81:40:1b:a6:9d
                             Signal             : 38% \s
                             Radio type         : 802.11n
                             Band               : 2.4 GHz
                    SSID 5 : NETGEAR00
                        Authentication          : WPA2-Personal
                        Encryption              : CCMP\s
                        BSSID 1                 : a0:63:91:32:e7:40
                             Signal             : 31% \s
                             Radio type         : 802.11n
                             Band               : 2.4 GHz
                    SSID 6 : DIRECT-73-HP M479fdw Color LJ
                        Authentication          : WPA2-Personal
                        Encryption              : CCMP\s
                        BSSID 1                 : 02:68:eb:f2:51:73
                             Signal             : 38% \s
                             Radio type         : 802.11n
                             Band               : 2.4 GHz
                    SSID 7 : rtec
                        Authentication          : WPA2-Personal
                        Encryption              : CCMP\s
                        BSSID 1                 : 50:91:e3:40:3b:4e
                             Signal             : 60% \s
                             Radio type         : 802.11n
                             Band               : 2.4 GHz
                    SSID 8 : rtec_5G
                        Authentication          : WPA2-Personal
                        Encryption              : CCMP\s
                        BSSID 1                 : 50:91:e3:40:3b:4d
                             Signal             : 33% \s
                             Radio type         : 802.11ac
                             Band               : 5 GHz
                                 Connected Stations:         1
                                 Channel Utilization:        0 (0 %)
                    SSID 9 : DIRECT-91-HP OfficeJet Pro 9130e
                        Authentication          : WPA2-Personal
                        Encryption              : CCMP\s
                        BSSID 1                 : 2e:58:b9:99:64:91
                             Signal             : 40% \s
                             Radio type         : 802.11n
                             Band               : 2.4 GHz
                    SSID 10 : DIRECT-15-HP M283 LaserJet
                        Authentication          : WPA2-Personal
                        Encryption              : CCMP\s
                        BSSID 1                 : ce:5e:f8:5b:19:15
                             Signal             : 35% \s
                             Radio type         : 802.11n
                             Band               : 2.4 GHz
                
                
                
                
                    SSID 1 : SDSU_Guest
                        Authentication          : Open
                        Encryption              : None\s
                        BSSID 1                 : 84:d4:7e:69:95:b1
                             Signal             : 35% \s
                             Radio type         : 802.11ac
                             Band               : 5 GHz
                        BSSID 2                 : 84:d4:7e:69:8a:01
                             Signal             : 65% \s
                             Radio type         : 802.11n
                             Band               : 2.4 GHz
                        BSSID 3                 : 84:d4:7e:69:7d:01
                             Signal             : 29% \s
                             Radio type         : 802.11n
                             Band               : 2.4 GHz
                        BSSID 4                 : 84:d4:7e:69:87:81
                             Signal             : 35% \s
                             Radio type         : 802.11n
                             Band               : 2.4 GHz
                        BSSID 5                 : 84:d4:7e:69:89:91
                             Signal             : 26% \s
                             Radio type         : 802.11ac
                             Band               : 5 GHz
                        BSSID 6                 : 84:d4:7e:69:95:a1
                             Signal             : 40% \s
                             Radio type         : 802.11n
                             Band               : 2.4 GHz
                        BSSID 7                 : 84:d4:7e:69:89:81
                             Signal             : 50% \s
                             Radio type         : 802.11n
                             Band               : 2.4 GHz
                        BSSID 8                 : 84:d4:7e:69:8a:11
                             Signal             : 26% \s
                             Radio type         : 802.11ac
                             Band               : 5 GHz
                        BSSID 9                 : 84:d4:7e:69:8e:01
                             Signal             : 72% \s
                             Radio type         : 802.11n
                             Band               : 2.4 GHz
                        BSSID 10                 : 84:d4:7e:69:83:21
                             Signal             : 72% \s
                             Radio type         : 802.11n
                             Band               : 2.4 GHz
                        BSSID 11                 : 7c:57:3c:be:5e:41
                             Signal             : 40% \s
                             Radio type         : 802.11ac
                             Band               : 5 GHz
                        BSSID 12                 : 84:d4:7e:69:7d:71
                             Signal             : 35% \s
                             Radio type         : 802.11ac
                             Band               : 5 GHz
                        BSSID 13                 : 84:d4:7e:69:82:91
                             Signal             : 82% \s
                             Radio type         : 802.11ac
                             Band               : 5 GHz
                        BSSID 14                 : 84:d4:7e:69:83:31
                             Signal             : 83% \s
                             Radio type         : 802.11ac
                             Band               : 5 GHz
                        BSSID 15                 : 84:d4:7e:69:8e:11
                             Signal             : 57% \s
                             Radio type         : 802.11ac
                             Band               : 5 GHz
                        BSSID 16                 : 84:d4:7e:69:7d:11
                             Signal             : 20% \s
                             Radio type         : 802.11ac
                             Band               : 5 GHz
                        BSSID 17                 : 84:d4:7e:69:80:71
                             Signal             : 80% \s
                             Radio type         : 802.11ac
                             Band               : 5 GHz
                        BSSID 18                 : 7c:57:3c:be:5e:51
                             Signal             : 35% \s
                             Radio type         : 802.11ac
                             Band               : 5 GHz
                        BSSID 19                 : 84:d4:7e:69:80:61
                             Signal             : 86% \s
                             Radio type         : 802.11n
                             Band               : 2.4 GHz
                        BSSID 20                 : 84:d4:7e:69:82:81
                             Signal             : 87% \s
                             Radio type         : 802.11n
                             Band               : 2.4 GHz
                    SSID 2 : eduroam
                        Authentication          : WPA2-Enterprise
                        Encryption              : CCMP\s
                        BSSID 1                 : 84:d4:7e:69:95:b0
                             Signal             : 33% \s
                             Radio type         : 802.11ac
                             Band               : 5 GHz
                        BSSID 2                 : 84:d4:7e:69:8a:00
                             Signal             : 65% \s
                             Radio type         : 802.11n
                             Band               : 2.4 GHz
                        BSSID 3                 : 84:d4:7e:69:89:80
                             Signal             : 50% \s
                             Radio type         : 802.11n
                             Band               : 2.4 GHz
                        BSSID 4                 : 84:d4:7e:69:95:a0
                             Signal             : 40% \s
                             Radio type         : 802.11n
                             Band               : 2.4 GHz
                        BSSID 5                 : 84:d4:7e:69:89:90
                             Signal             : 24% \s
                             Radio type         : 802.11ac
                             Band               : 5 GHz
                        BSSID 6                 : 84:d4:7e:69:7d:70
                             Signal             : 40% \s
                             Radio type         : 802.11ac
                             Band               : 5 GHz
                        BSSID 7                 : 84:d4:7e:69:8e:00
                             Signal             : 75% \s
                             Radio type         : 802.11n
                             Band               : 2.4 GHz
                        BSSID 8                 : fc:7f:f1:f9:01:30
                             Signal             : 24% \s
                             Radio type         : 802.11ax
                             Band               : 5 GHz
                        BSSID 9                 : 84:d4:7e:69:83:20
                             Signal             : 72% \s
                             Radio type         : 802.11n
                             Band               : 2.4 GHz
                        BSSID 10                 : 84:d4:7e:69:82:90
                             Signal             : 82% \s
                             Radio type         : 802.11ac
                             Band               : 5 GHz
                        BSSID 11                 : 84:d4:7e:69:83:30
                             Signal             : 80% \s
                             Radio type         : 802.11ac
                             Band               : 5 GHz
                        BSSID 12                 : 84:d4:7e:69:8e:10
                             Signal             : 57% \s
                             Radio type         : 802.11ac
                             Band               : 5 GHz
                        BSSID 13                 : 84:d4:7e:69:7d:10
                             Signal             : 22% \s
                             Radio type         : 802.11ac
                             Band               : 5 GHz
                        BSSID 14                 : 84:d4:7e:69:80:70
                             Signal             : 80% \s
                             Radio type         : 802.11ac
                             Band               : 5 GHz
                        BSSID 15                 : 7c:57:3c:be:5e:50
                             Signal             : 33% \s
                             Radio type         : 802.11ac
                             Band               : 5 GHz
                        BSSID 16                 : 7c:57:3c:be:5e:40
                             Signal             : 35% \s
                             Radio type         : 802.11ac
                             Band               : 5 GHz
                        BSSID 17                 : 84:d4:7e:69:80:60
                             Signal             : 86% \s
                             Radio type         : 802.11n
                             Band               : 2.4 GHz
                        BSSID 18                 : 84:d4:7e:69:82:80
                             Signal             : 87% \s
                             Radio type         : 802.11n
                             Band               : 2.4 GHz
                    SSID 3 : HP-Print-29-LaserJet 400 color
                        Authentication          : Open
                        Encryption              : None\s
                        BSSID 1                 : 08:3e:8e:72:b3:29
                             Signal             : 29% \s
                             Radio type         : 802.11g
                             Band               : 2.4 GHz
                    SSID 4 : DIRECT-9C-HP DeskJet 4100 series
                        Authentication          : WPA2-Personal
                        Encryption              : CCMP\s
                        BSSID 1                 : 50:81:40:1b:a6:9d
                             Signal             : 38% \s
                             Radio type         : 802.11n
                             Band               : 2.4 GHz
                    SSID 5 : NETGEAR00
                        Authentication          : WPA2-Personal
                        Encryption              : CCMP\s
                        BSSID 1                 : a0:63:91:32:e7:40
                             Signal             : 31% \s
                             Radio type         : 802.11n
                             Band               : 2.4 GHz
                    SSID 6 : DIRECT-73-HP M479fdw Color LJ
                        Authentication          : WPA2-Personal
                        Encryption              : CCMP\s
                        BSSID 1                 : 02:68:eb:f2:51:73
                             Signal             : 38% \s
                             Radio type         : 802.11n
                             Band               : 2.4 GHz
                    SSID 7 : rtec
                        Authentication          : WPA2-Personal
                        Encryption              : CCMP\s
                        BSSID 1                 : 50:91:e3:40:3b:4e
                             Signal             : 60% \s
                             Radio type         : 802.11n
                             Band               : 2.4 GHz
                    SSID 8 : rtec_5G
                        Authentication          : WPA2-Personal
                        Encryption              : CCMP\s
                        BSSID 1                 : 50:91:e3:40:3b:4d
                             Signal             : 33% \s
                             Radio type         : 802.11ac
                             Band               : 5 GHz
                                 Connected Stations:         1
                                 Channel Utilization:        0 (0 %)
                    SSID 9 : DIRECT-91-HP OfficeJet Pro 9130e
                        Authentication          : WPA2-Personal
                        Encryption              : CCMP\s
                        BSSID 1                 : 2e:58:b9:99:64:91
                             Signal             : 40% \s
                             Radio type         : 802.11n
                             Band               : 2.4 GHz
                    SSID 10 : DIRECT-15-HP M283 LaserJet
                        Authentication          : WPA2-Personal
                        Encryption              : CCMP\s
                        BSSID 1                 : ce:5e:f8:5b:19:15
                             Signal             : 35% \s
                             Radio type         : 802.11n
                             Band               : 2.4 GHz
                    SSID 1 : eduroam
                        Authentication          : WPA2-Enterprise
                        Encryption              : CCMP\s
                        BSSID 1                 : 84:d4:7e:69:82:80
                             Signal             : 100% \s
                             Radio type         : 802.11n
                             Band               : 2.4 GHz
                        BSSID 2                 : 84:d4:7e:69:82:90
                             Signal             : 100% \s
                             Radio type         : 802.11ac
                             Band               : 5 GHz
                        BSSID 3                 : 84:d4:7e:69:80:60
                             Signal             : 90% \s
                             Radio type         : 802.11n
                             Band               : 2.4 GHz
                        BSSID 4                 : 84:d4:7e:69:83:20
                             Signal             : 86% \s
                             Radio type         : 802.11n
                             Band               : 2.4 GHz
                        BSSID 5                 : 84:d4:7e:69:83:30
                             Signal             : 84% \s
                             Radio type         : 802.11ac
                             Band               : 5 GHz
                        BSSID 6                 : 84:d4:7e:69:8e:00
                             Signal             : 68% \s
                             Radio type         : 802.11n
                             Band               : 2.4 GHz
                        BSSID 7                 : 84:d4:7e:69:8e:10
                             Signal             : 60% \s
                             Radio type         : 802.11ac
                             Band               : 5 GHz
                        BSSID 8                 : 84:d4:7e:69:80:70
                             Signal             : 60% \s
                             Radio type         : 802.11ac
                             Band               : 5 GHz
                        BSSID 9                 : 84:d4:7e:69:7d:70
                             Signal             : 58% \s
                             Radio type         : 802.11ac
                             Band               : 5 GHz
                        BSSID 10                 : 84:d4:7e:69:8a:10
                             Signal             : 42% \s
                             Radio type         : 802.11ac
                             Band               : 5 GHz
                        BSSID 11                 : 84:d4:7e:69:7d:10
                             Signal             : 42% \s
                             Radio type         : 802.11ac
                             Band               : 5 GHz
                        BSSID 12                 : 84:d4:7e:69:95:b0
                             Signal             : 40% \s
                             Radio type         : 802.11ac
                             Band               : 5 GHz
                        BSSID 13                 : 7c:57:3c:be:5e:40
                             Signal             : 34% \s
                             Radio type         : 802.11ac
                             Band               : 5 GHz
                        BSSID 14                 : fc:7f:f1:f9:01:30
                             Signal             : 32% \s
                             Radio type         : 802.11ax
                             Band               : 5 GHz
                        BSSID 15                 : a8:bd:27:f6:f4:f0
                             Signal             : 22% \s
                             Radio type         : 802.11ac
                             Band               : 5 GHz
                        BSSID 16                 : 84:d4:7e:69:89:90
                             Signal             : 20% \s
                             Radio type         : 802.11ac
                             Band               : 5 GHz
                        BSSID 17                 : 84:d4:7e:69:89:d0
                             Signal             : 18% \s
                             Radio type         : 802.11ac
                             Band               : 5 GHz
                    SSID 2 : SDSU_Guest
                        Authentication          : Open
                        Encryption              : None\s
                        BSSID 1                 : 84:d4:7e:69:82:81
                             Signal             : 100% \s
                             Radio type         : 802.11n
                             Band               : 2.4 GHz
                        BSSID 2                 : 84:d4:7e:69:82:91
                             Signal             : 100% \s
                             Radio type         : 802.11ac
                             Band               : 5 GHz
                        BSSID 3                 : 84:d4:7e:69:80:61
                             Signal             : 90% \s
                             Radio type         : 802.11n
                             Band               : 2.4 GHz
                        BSSID 4                 : 84:d4:7e:69:83:21
                             Signal             : 86% \s
                             Radio type         : 802.11n
                             Band               : 2.4 GHz
                        BSSID 5                 : 84:d4:7e:69:83:31
                             Signal             : 84% \s
                             Radio type         : 802.11ac
                             Band               : 5 GHz
                        BSSID 6                 : 84:d4:7e:69:8e:01
                             Signal             : 70% \s
                             Radio type         : 802.11n
                             Band               : 2.4 GHz
                        BSSID 7                 : 84:d4:7e:69:8e:11
                             Signal             : 62% \s
                             Radio type         : 802.11ac
                             Band               : 5 GHz
                        BSSID 8                 : 84:d4:7e:69:80:71
                             Signal             : 60% \s
                             Radio type         : 802.11ac
                             Band               : 5 GHz
                        BSSID 9                 : 84:d4:7e:69:7d:71
                             Signal             : 60% \s
                             Radio type         : 802.11ac
                             Band               : 5 GHz
                        BSSID 10                 : 84:d4:7e:69:8a:11
                             Signal             : 44% \s
                             Radio type         : 802.11ac
                             Band               : 5 GHz
                        BSSID 11                 : 84:d4:7e:69:7d:11
                             Signal             : 42% \s
                             Radio type         : 802.11ac
                             Band               : 5 GHz
                        BSSID 12                 : 84:d4:7e:69:95:b1
                             Signal             : 40% \s
                             Radio type         : 802.11ac
                             Band               : 5 GHz
                        BSSID 13                 : fc:7f:f1:f9:01:31
                             Signal             : 32% \s
                             Radio type         : 802.11ax
                             Band               : 5 GHz
                        BSSID 14                 : 7c:57:3c:be:5e:41
                             Signal             : 32% \s
                             Radio type         : 802.11ac
                             Band               : 5 GHz
                        BSSID 15                 : 7c:57:3c:be:26:01
                             Signal             : 26% \s
                             Radio type         : 802.11ac
                             Band               : 5 GHz
                        BSSID 16                 : a8:bd:27:f6:f4:f1
                             Signal             : 22% \s
                             Radio type         : 802.11ac
                             Band               : 5 GHz
                        BSSID 17                 : 84:d4:7e:69:89:91
                             Signal             : 18% \s
                             Radio type         : 802.11ac
                             Band               : 5 GHz
                    SSID 3 : SETUP
                        Authentication          : Open
                        Encryption              : None\s
                        BSSID 1                 : 9e:2b:77:8e:45:c7
                             Signal             : 80% \s
                             Radio type         : 802.11b
                             Band               : 2.4 GHz
                    SSID 4 : rtec
                        Authentication          : WPA2-Personal
                        Encryption              : CCMP\s
                        BSSID 1                 : 50:91:e3:40:3b:4e
                             Signal             : 80% \s
                             Radio type         : 802.11n
                             Band               : 2.4 GHz
                    SSID 5 : DIRECT-15-HP M283 LaserJet
                        Authentication          : WPA2-Personal
                        Encryption              : CCMP\s
                        BSSID 1                 : ce:5e:f8:5b:19:15
                             Signal             : 60% \s
                             Radio type         : 802.11n
                             Band               : 2.4 GHz
                    SSID 6 : HP-Print-29-LaserJet 400 color
                        Authentication          : Open
                        Encryption              : None\s
                        BSSID 1                 : 08:3e:8e:72:b3:29
                             Signal             : 56% \s
                             Radio type         : 802.11g
                             Band               : 2.4 GHz
                    SSID 7 : DIRECT-9C-HP DeskJet 4100 series
                        Authentication          : WPA2-Personal
                        Encryption              : CCMP\s
                        BSSID 1                 : 50:81:40:1b:a6:9d
                             Signal             : 50% \s
                             Radio type         : 802.11n
                             Band               : 2.4 GHz
                    SSID 8 : rtec_5G
                        Authentication          : WPA2-Personal
                        Encryption              : CCMP\s
                        BSSID 1                 : 50:91:e3:40:3b:4d
                             Signal             : 46% \s
                             Radio type         : 802.11ac
                             Band               : 5 GHz
            """;
        return parseStringToListOfJSON(wifiData);
    }

    //main for testing purposes only
    public static void main(String[] args) {
        ParseWiFiDataWindows test = new ParseWiFiDataWindows(); //create new instance of the class
        List<JSONObject> jsonWiFiObjects; // create list of JSONObjects to hold the method output
//        String wifiData = SingleWiFiScanWindows.scan("Wi-Fi");
        jsonWiFiObjects = test.parseStringToListOfJSON(); //run the overloaded function to parse the sample string
        System.out.println("\n\n\n\n\n");
        for (JSONObject data : jsonWiFiObjects) { //iterate through the JSON objects, printing each
            System.out.println(data.toString(4));
        }
    }
}