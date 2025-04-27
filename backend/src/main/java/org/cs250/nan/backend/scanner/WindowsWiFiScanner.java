package org.cs250.nan.backend.scanner;

import org.cs250.nan.backend.parser.WindowsWiFiDataParser;
import org.json.JSONObject;
import org.springframework.stereotype.Component;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;
import java.util.List;

/**
 * Windows WiFi scanner using original parsing logic.
 */
@Component
public class WindowsWiFiScanner implements WiFiScanner {
    @Override
    public List<JSONObject> scan() throws IOException {
        ProcessBuilder pb = new ProcessBuilder(
                "powershell.exe", "-Command",
                "netsh wlan show networks mode=Bssid | " +
                        "Select-String 'SSID|Signal|Band|Encryption|Authentication|Radio type|Connected stations|Channel utilization'"
        );
        var proc = pb.start();
        var out = new StringBuilder();
        try (var reader = new BufferedReader(new InputStreamReader(proc.getInputStream()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                out.append(line).append("\n");
            }
        }
        return WindowsWiFiDataParser.parseStringToListOfJSON(out.toString());
    }
}