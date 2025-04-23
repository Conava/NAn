package org.cs250.nan.backend.scanner;

import org.json.JSONObject;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;

@Component
public class LinuxGPSScanner implements GPSScanner {
    @Override
    public JSONObject scan() throws IOException {
        ProcessBuilder pb = new ProcessBuilder("bash", "-c", "gpspipe -w -n 10");
        var proc = pb.start();
        var raw = new StringBuilder();
        try (var reader = new BufferedReader(new InputStreamReader(proc.getInputStream()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                raw.append(line).append("\n");
            }
        }
        return org.cs250.nan.backend.parser.GPSDataParser.parseStringToJSON(raw.toString());
    }
}
