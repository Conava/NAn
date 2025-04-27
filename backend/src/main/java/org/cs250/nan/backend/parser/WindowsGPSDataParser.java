package org.cs250.nan.backend.parser;

import com.fazecast.jSerialComm.SerialPort;
import org.json.JSONObject;
import java.util.HashMap;
import java.util.Map;

/**
 * Windows‑specific GPS data parser: identical to old ParseGPSData.
 */
public class WindowsGPSDataParser {
    public static JSONObject parseGPS(String rawData) {
        Map<String, String> gpsDataMap = new HashMap<>();
        String[] labelsGPGGA = {"timeUTC","latitude","northSouth","longitude","eastWest","positionFix","satelliteCount","hdop","altitude","altitudeUnits","geoIdSep","sepUnits","dgpsAge","dgpsStationId"};
        String[] labelsGPGSA = {"selectionMode","fixType","sat01","sat02","sat03","sat04","sat05","sat06","sat07","sat08","sat09","sat10","sat11","sat12","pdop","hdop","vdop"};
        String[] labelsGPRMC = {"timeUTC","status","latitude","northSouth","longitude","eastWest","spdOverGrnd","courseOverGrnd","dateUTC","magVariation","magDirection","modeIndicator"};
        Map<String, String[]> mapLabels = Map.of(
                "GPGGA", labelsGPGGA,
                "GPGSA", labelsGPGSA,
                "GPRMC", labelsGPRMC
        );

        StringBuilder out = new StringBuilder();
        Map<String,String> sentences = new HashMap<>();
        // read 3 sentence types
        SerialPort port = SerialPort.getCommPorts()[0];
        port.setBaudRate(4800);
        port.openPort();
        boolean firstDiscard = false;
        StringBuilder sb = new StringBuilder();
        while (sentences.size() < 3) {
            if (port.bytesAvailable() > 0) {
                byte[] buf = new byte[256];
                int n = port.readBytes(buf, buf.length);
                for (int i = 0; i < n; i++) {
                    char c = (char)buf[i]; sb.append(c);
                    if (c == '\n') {
                        if (firstDiscard) {
                            String line = sb.toString().trim();
                            String key = line.substring(1, line.indexOf(','));
                            if (mapLabels.containsKey(key)) sentences.put(key, line);
                        } else {
                            firstDiscard = true;
                        }
                        sb.setLength(0);
                    }
                }
            }
        }
        port.closePort();
        for (String val : sentences.values()) out.append(val).append("\n");
        // parse
        JSONObject gpsJson = new JSONObject();
        String[] lines = out.toString().split("\\n");
        for (String sentence : lines) {
            String noChk = sentence.substring(1, sentence.indexOf('*'));
            String[] parts = noChk.split(",", -1);
            String key = parts[0].trim();
            String[] labels = mapLabels.get(key);
            for (int i = 0; i < labels.length; i++) {
                gpsJson.put(labels[i], parts[i+1].trim());
            }
        }
        return gpsJson;
    }
}