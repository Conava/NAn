package org.cs250.nan.backend.parser;

import org.json.JSONObject;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * Parses raw NMEA sentences into a JSON object,
 * exactly as ParseGPSDataWindows did.
 */
@Component
public class LinuxGPSDataParser {

    private final Map<String,String[]> dataLabels;
    private static final String[] GPGGA = {
            "timeUTC","latitude","northSouth","longitude","eastWest","positionFix",
            "satelliteCount","hdop","altitude","altitudeUnits","geoIdSep","sepUnits",
            "dgpsAge","dgpsStationId"
    };
    private static final String[] GPGSA = {
            "selectionMode","fixType","sat01","sat02","sat03","sat04","sat05","sat06",
            "sat07","sat08","sat09","sat10","sat11","sat12","pdop","hdop","vdop"
    };
    private static final String[] GPRMC = {
            "timeUTC","status","latitude","northSouth","longitude","eastWest",
            "spdOverGrnd","courseOverGrnd","dateUTC","magVariation","magDirection","modeIndicator"
    };

    public LinuxGPSDataParser() {
        dataLabels = new HashMap<>();
        dataLabels.put("GPGGA", GPGGA);
        dataLabels.put("GPGSA", GPGSA);
        dataLabels.put("GPRMC", GPRMC);
    }

    public JSONObject parseStringToJSON(String raw) {
        Map<String,String> map = new HashMap<>();
        for (String line : raw.split("\\r?\\n")) {
            if (!line.startsWith("$") || !line.contains("*")) continue;
            String noChecksum = line.substring(1, line.indexOf('*'));
            String[] parts = noChecksum.split(",", -1);
            String key = parts[0];
            String[] labels = dataLabels.get(key);
            if (labels==null) continue;
            for (int i=0; i<labels.length && i+1<parts.length; i++) {
                map.put(labels[i], parts[i+1]);
            }
        }
        System.out.println("Parsed GPS data: " + map);
        return new JSONObject(map);
    }
}
