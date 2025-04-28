package org.cs250.nan.backend.scanner;

import org.json.JSONObject;
import org.springframework.stereotype.Component;

import org.cs250.nan.backend.parser.LinuxGPSDataParser;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;

@Component
public class LinuxGPSScanner implements GPSScanner {
    private final LinuxGPSDataParser linuxGPSDataParser;

    public LinuxGPSScanner(LinuxGPSDataParser linuxGPSDataParser) {
        this.linuxGPSDataParser = linuxGPSDataParser;
    }

    @Override
    public JSONObject scan() throws IOException {
        ProcessBuilder pb = new ProcessBuilder("gpspipe", "-w", "-n", "5");
        Process proc = pb.start();

        String tpvLine = null;
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(proc.getInputStream()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                // as soon as we see the TPV report, grab it and break
                if (line.contains("\"class\":\"TPV\"")) {
                    tpvLine = line;
                    break;
                }
            }
        } finally {
            // make absolutely sure gpspipe can't keep running
            proc.destroy();
        }

        // if we never saw a TPV, fall back to "{}"
        if (tpvLine == null) {
            tpvLine = "{}";
        }

        return linuxGPSDataParser.parseStringToJSON(tpvLine);
    }
}

