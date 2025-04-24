package org.cs250.nan.backend.scanner;

import org.json.JSONObject;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class LinuxWiFiScanner implements WiFiScanner {
    private static final String FIELDS = "NAME,SSID,SSID-HEX,BSSID,MODE,CHAN,FREQ,RATE,BANDWIDTH,SIGNAL,BARS,SECURITY,WPA-FLAGS,RSN-FLAGS";

    @Override
    public List<JSONObject> scan() throws IOException {
        return scan(null);
    }

    public List<JSONObject> scan(String interfaceName) throws IOException {
        runCommand("nmcli device wifi rescan");
        String cmd = "nmcli -t -f " + FIELDS + " device wifi list"
                + (interfaceName != null && !interfaceName.isBlank()
                ? " ifname " + interfaceName
                : "");
        String output = runCommand(cmd);

        if (output.isBlank()) {
            System.out.println("No output; ensure Wi‑Fi is enabled.");
            return List.of();
        }

        return org.cs250.nan.backend.parser.WiFiDataParser
                .parseStringToListOfJSON(output);
    }

    private String runCommand(String command) throws IOException {
        ProcessBuilder pb = new ProcessBuilder("bash", "-c", command);
        Process process = pb.start();

        String stdout = readStream(process.getInputStream());
        String stderr = readStream(process.getErrorStream());
        int exitCode;
        try {
            exitCode = process.waitFor();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IOException("Command interrupted: " + command, e);
        }

        if (exitCode != 0) {
            System.err.println("Command failed: " + stderr);
        }

        return stdout;
    }

    private String readStream(InputStream is) throws IOException {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(is))) {
            return reader.lines().collect(Collectors.joining("\n"));
        }
    }
}