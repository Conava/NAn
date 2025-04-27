package org.cs250.nan.backend.scanner;

import org.json.JSONObject;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class SystemGPSScanner implements GPSScanner {
    private final WindowsGPSScanner windows;
    private final LinuxGPSScanner linux;
    private final boolean isWindows;

    public SystemGPSScanner(WindowsGPSScanner windows, LinuxGPSScanner linux) {
        this.windows = windows;
        this.linux = linux;
        this.isWindows = System.getProperty("os.name").toLowerCase().contains("win");
    }

    @Override
    public JSONObject scan() throws IOException {
        return isWindows
                ? windows.scan()
                : linux.scan();
    }
}