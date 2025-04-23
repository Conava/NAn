package org.cs250.nan.backend.scanner;

import org.json.JSONObject;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class SystemGPSScanner implements GPSScanner {
    private final WindowsGPSScanner win;
    private final LinuxGPSScanner linux;

    public SystemGPSScanner(WindowsGPSScanner win, LinuxGPSScanner linux) {
        this.win = win;
        this.linux = linux;
    }

    @Override
    public JSONObject scan() throws IOException {
        String os = System.getProperty("os.name").toLowerCase();
        if (os.contains("win")) {
            return win.scan();
        } else {
            return linux.scan();
        }
    }
}