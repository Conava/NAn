package org.cs250.nan.backend.scanner;

import org.json.JSONObject;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;
import java.util.List;

@Component
public class LinuxWiFiScanner implements WiFiScanner {
    @Override
    public List<JSONObject> scan() throws IOException {
        ProcessBuilder pb = new ProcessBuilder(
                "bash", "-c",
                "nmcli -t -f SSID,SIGNAL,BARS device wifi list"
        );
        var process = pb.start();
        var output = new StringBuilder();
        try (var reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                output.append(line).append("\n");
            }
        }
        return org.cs250.nan.backend.parser.WiFiDataParser.parseStringToListOfJSON(output.toString());
    }
}