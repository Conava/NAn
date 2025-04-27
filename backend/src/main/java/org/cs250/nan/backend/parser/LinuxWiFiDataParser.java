package org.cs250.nan.backend.parser;

import org.json.JSONObject;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Parses the output of `iwlist <iface> scanning` into the same keys your Windows parser produced.
 */
public class LinuxWiFiDataParser {
    private static final Pattern CELL = Pattern.compile("^\\s*Cell \\d+ - Address: ([0-9A-F:]{17})", Pattern.CASE_INSENSITIVE);
    private static final Pattern ESSID = Pattern.compile("ESSID:\"(.*)\"");
    private static final Pattern SIGNAL = Pattern.compile("Signal level=(-?\\d+\\s?dBm)");
    private static final Pattern FREQ  = Pattern.compile("Frequency:(\\d+\\.\\d+) GHz");
    private static final Pattern PROTO = Pattern.compile("Protocol:(.*)");
    private static final Pattern ENC   = Pattern.compile("Encryption key:(on|off)");
    private static final Pattern IE_WPA   = Pattern.compile("IE: WPA Version.*");
    private static final Pattern IE_RSN   = Pattern.compile("IE: RSN Version.*");

    public static List<JSONObject> parseStringToListOfJSON(String scanOutput) {
        List<JSONObject> list = new ArrayList<>();
        String[] blocks = scanOutput.split("(?m)(?=\\s*Cell \\d+ - Address:)");

        for (String block : blocks) {
            Map<String,String> m = new HashMap<>();
            String ssid = "";
            String auth = "";
            String encr = "";

            String[] lines = block.split("\\r?\\n");
            for (String line : lines) {
                Matcher c = CELL.matcher(line);
                if (c.find()) {
                    m.put("MAC", c.group(1).trim());
                    continue;
                }
                Matcher e = ESSID.matcher(line);
                if (e.find()) {
                    ssid = e.group(1);
                    continue;
                }
                Matcher p = PROTO.matcher(line);
                if (p.find()) {
                    m.put("Radio type", p.group(1).trim());
                    continue;
                }
                Matcher f = FREQ.matcher(line);
                if (f.find()) {
                    double ghz = Double.parseDouble(f.group(1));
                    m.put("Band", ghz < 3 ? "2.4 GHz" : "5 GHz");
                    continue;
                }
                Matcher s = SIGNAL.matcher(line);
                if (s.find()) {
                    m.put("Signal", s.group(1).trim());
                    continue;
                }
                Matcher enc = ENC.matcher(line);
                if (enc.find()) {
                    encr = enc.group(1).equals("on") ? "Yes" : "No";
                    continue;
                }
                if (IE_WPA.matcher(line).find()) {
                    auth = auth.isEmpty() ? "WPA" : auth + "/WPA";
                    continue;
                }
                if (IE_RSN.matcher(line).find()) {
                    auth = auth.isEmpty() ? "WPA2" : auth + "/WPA2";
                }
            }

            // put the collected SSID/auth/encryption
            m.put("SSID", ssid);
            m.put("Authentication", auth);
            m.put("Encryption", encr);

            // timestamps
            m.put("timeLocal", LocalTime.now().format(DateTimeFormatter.ofPattern("HHmmss.SSS")));
            m.put("dateLocal", LocalDate.now().format(DateTimeFormatter.ofPattern("ddMMyy")));

            list.add(new JSONObject(m));
        }
        return list;
    }
}
