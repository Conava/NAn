package org.cs250.nan.backend.parser;

import org.json.JSONObject;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class LinuxWiFiDataParser {

    // match the “BSS xx:xx:xx…” line
    private static final Pattern BSS_HEADER = Pattern.compile(
            "^BSS\\s+([0-9a-f:]{17})", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE
    );
    private static final Pattern SSID_PAT = Pattern.compile("^\\s*SSID:\\s*(.*)$");
    private static final Pattern SIGNAL_PAT = Pattern.compile("^\\s*signal:\\s*([-0-9.]+) dBm");
    private static final Pattern FREQ_PAT = Pattern.compile("^\\s*freq:\\s*([0-9.]+)");
    private static final Pattern RSN_GROUP = Pattern.compile("\\* Group cipher:\\s*(\\S+)");
    private static final Pattern RSN_AUTH = Pattern.compile("\\* Authentication suites:\\s*(\\S+)");

    private static final DateTimeFormatter TIME_FMT = DateTimeFormatter.ofPattern("HHmmss.SSS");
    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("ddMMyy");

    /**
     * Split raw “iw scan” output on each “BSS …” header, then pull out
     * exactly the same keys your Windows parser emits.
     */
    public List<JSONObject> parseStringToListOfJSON(String raw) {
        List<JSONObject> results = new ArrayList<>();
        if (raw == null || raw.isBlank()) return results;

        // split out each BSS block
        String[] blocks = raw.split("(?m)(?=^BSS )");

        for (String block : blocks) {
            String[] lines = block.split("\\r?\\n");
            Matcher header = BSS_HEADER.matcher(lines[0]);
            if (!header.find()) continue;

            // we’ll keep insertion order so the JSON keys line up nicely
            Map<String, String> map = new LinkedHashMap<>();
            map.put("MAC", header.group(1));
            map.put("SSID", "");
            map.put("Authentication", "");
            map.put("Encryption", "");
            map.put("timeLocal", LocalTime.now().format(TIME_FMT));
            map.put("dateLocal", LocalDate.now().format(DATE_FMT));

            String foundCipher = null, foundAuth = null;

            for (String line : lines) {
                Matcher m;

                if ((m = SSID_PAT.matcher(line)).find()) {
                    map.put("SSID", m.group(1).trim());
                    continue;
                }
                if ((m = SIGNAL_PAT.matcher(line)).find()) {
                    // match Windows’ Value + “ dBm”
                    map.put("Signal", m.group(1).trim() + " dBm");
                    continue;
                }
                if ((m = FREQ_PAT.matcher(line)).find()) {
                    double freq = Double.parseDouble(m.group(1));
                    map.put("Band", freq < 2500 ? "2.4 GHz" : "5 GHz");
                    continue;
                }
                if ((m = RSN_GROUP.matcher(line)).find()) {
                    foundCipher = m.group(1).trim();
                    continue;
                }
                if ((m = RSN_AUTH.matcher(line)).find()) {
                    foundAuth = m.group(1).trim();
                    continue;
                }

                // any other “Key: Value” lines get copied verbatim
                String trimmed = line.trim();
                if (trimmed.contains(":")) {
                    String[] parts = trimmed.split(":", 2);
                    String key = parts[0].trim(), val = parts[1].trim();
                    if (!key.isEmpty() && !val.isEmpty()) {
                        map.put(key, val);
                    }
                }
            }

            // finalize the RSN fields
            if (foundCipher != null) map.put("Encryption", foundCipher);
            if (foundAuth != null) map.put("Authentication", foundAuth);

            results.add(new JSONObject(map));
        }

        return results;
    }
}
