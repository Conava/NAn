package org.cs250.nan.backend.scanner;

import org.springframework.stereotype.Component;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;

@Component
public class SystemWiFiScanner implements WiFiScanner {
    private final WindowsWiFiScanner windows;
    private final LinuxWiFiScanner linux;
    private final boolean isWindows;

    public SystemWiFiScanner(WindowsWiFiScanner windows, LinuxWiFiScanner linux) {
        this.windows = windows;
        this.linux = linux;
        this.isWindows = System.getProperty("os.name").toLowerCase().contains("win");
    }

    @Override
    public List<JSONObject> scan() throws IOException {
        return isWindows
                ? windows.scan()
                : linux.scan();
    }
}