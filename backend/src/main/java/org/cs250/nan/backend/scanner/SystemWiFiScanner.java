package org.cs250.nan.backend.scanner;

import org.json.JSONObject;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;

@Component
public class SystemWiFiScanner implements WiFiScanner {
    private final WindowsWiFiScanner win;
    private final LinuxWiFiScanner linux;

    public SystemWiFiScanner(WindowsWiFiScanner win, LinuxWiFiScanner linux) {
        this.win = win;
        this.linux = linux;
    }

    @Override
    public List<JSONObject> scan() throws IOException {
        String os = System.getProperty("os.name").toLowerCase();
        if (os.contains("win")) {
            return win.scan();
        } else {
            return linux.scan();
        }
    }
}