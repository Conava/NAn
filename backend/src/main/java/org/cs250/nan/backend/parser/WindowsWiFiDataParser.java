package org.cs250.nan.backend.parser;

import org.json.JSONObject;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * Windows‑specific WiFi data parser: identical to the old ParseWiFiDataWindows.
 */
public class WindowsWiFiDataParser {
    public static List<JSONObject> parseStringToListOfJSON(String wifiData) {
        List<Map<String, String>> uniqueBSSIDs = new ArrayList<>();
        Map<String, String> currentMap = null;
        String ssid = "", auth = "", encryp = "";
        String[] lineKeyValPair;
        String[] lines = wifiData.split("\\r?\\n");
        boolean firstSSIDFound = false;

        for (String rawLine : lines) {
            String line = rawLine.trim();
            if (!firstSSIDFound && !line.startsWith("SSID")) continue;
            firstSSIDFound = true;

            if (line.startsWith("SSID")) {
                lineKeyValPair = line.split(":", 2);
                ssid = (lineKeyValPair.length == 2) ? lineKeyValPair[1].trim() : "";

            } else if (line.startsWith("Authentication")) {
                lineKeyValPair = line.split(":", 2);
                auth = (lineKeyValPair.length == 2) ? lineKeyValPair[1].trim() : "";

            } else if (line.startsWith("Encryption")) {
                lineKeyValPair = line.split(":", 2);
                encryp = (lineKeyValPair.length == 2) ? lineKeyValPair[1].trim() : "";

            } else if (line.startsWith("BSSID")) {
                if (currentMap != null) uniqueBSSIDs.add(currentMap);
                currentMap = new HashMap<>();
                lineKeyValPair = line.split(":", 2);
                currentMap.put("MAC", (lineKeyValPair.length == 2) ? lineKeyValPair[1].trim() : "");
                currentMap.put("SSID", ssid);
                currentMap.put("Authentication", auth);
                currentMap.put("Encryption", encryp);
                currentMap.put("timeLocal", LocalTime.now().format(DateTimeFormatter.ofPattern("HHmmss")));
                currentMap.put("dateLocal", LocalDate.now().format(DateTimeFormatter.ofPattern("yyMMdd")));

            } else {
                if (currentMap != null) {
                    String[] parts = line.split(":", 2);
                    if (parts.length == 2) currentMap.put(parts[0].trim(), parts[1].trim());
                    else currentMap.put(parts[0].trim(), "");
                }
            }
        }
        if (currentMap != null) uniqueBSSIDs.add(currentMap);

        List<JSONObject> jsonWiFiObjects = new ArrayList<>();
        for (Map<String, String> map : uniqueBSSIDs) {
            jsonWiFiObjects.add(new JSONObject(map));
        }
        return jsonWiFiObjects;
    }
}
