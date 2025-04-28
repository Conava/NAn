package org.cs250.nan.backend.scanner;

import org.cs250.nan.backend.parser.LinuxWiFiDataParser;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class LinuxWiFiScanner implements WiFiScanner {
    private static final Logger LOGGER = LoggerFactory.getLogger(LinuxWiFiScanner.class);
    private final String iface;
    private final LinuxWiFiDataParser parser;

    public LinuxWiFiScanner(@Value("${app.wifi-interface:wlan0}") String iface,
                            LinuxWiFiDataParser parser) {
        this.iface = iface;
        this.parser = parser;
    }

    @Override
    public List<JSONObject> scan() throws IOException {
        // run “iw dev <iface> scan”
        ProcessBuilder pb = new ProcessBuilder("iw", "dev", iface, "scan");
        pb.redirectErrorStream(true);                     // <- merge stderr into stdout
        Process proc = pb.start();

        String raw = new BufferedReader(new InputStreamReader(proc.getInputStream()))
                .lines()
                .collect(Collectors.joining("\n"));

        int exit = 0;                       // <- waitFor so you can inspect exit code
        try {
            exit = proc.waitFor();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        LOGGER.error(">>> iw scan exit={}", exit);

        // hand it off to our parser
        return parser.parseStringToListOfJSON(raw);
    }
}
