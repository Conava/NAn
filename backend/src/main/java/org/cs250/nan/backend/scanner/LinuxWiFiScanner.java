package org.cs250.nan.backend.scanner;

import org.cs250.nan.backend.parser.LinuxWiFiDataParser;
import org.json.JSONObject;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;
import java.util.List;

/**
 * Uses `iwlist <iface> scanning` under the hood.
 */
@Component
public class LinuxWiFiScanner implements WiFiScanner {

    @Override
    public List<JSONObject> scan() throws IOException {
        // 1) detect first wireless interface
        String iface = detectWirelessInterface();
        // 2) run scan
        ProcessBuilder pb = new ProcessBuilder("bash", "-c",
                "sudo iwlist " + iface + " scanning");
        Process p = pb.start();

        StringBuilder out = new StringBuilder();
        try (BufferedReader r = new BufferedReader(new InputStreamReader(p.getInputStream()))) {
            String line;
            while ((line = r.readLine()) != null) {
                out.append(line).append("\n");
            }
        }
        // 3) hand off to parser
        return LinuxWiFiDataParser.parseStringToListOfJSON(out.toString());
    }

    private String detectWirelessInterface() throws IOException {
        ProcessBuilder pb = new ProcessBuilder("bash", "-c",
                "iw dev | awk '$1==\"Interface\" {print $2; exit}'");
        Process p = pb.start();
        try (BufferedReader r = new BufferedReader(new InputStreamReader(p.getInputStream()))) {
            String iface = r.readLine();
            if (iface == null || iface.isBlank()) {
                throw new IOException("No wireless interface found");
            }
            return iface.trim();
        }
    }
}
