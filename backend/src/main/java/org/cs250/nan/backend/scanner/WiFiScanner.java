package org.cs250.nan.backend.scanner;

import java.io.IOException;

public class WiFiScanner {

    /**
     * Scans for Wi-Fi data using the appropriate OS‐specific implementation.
     *
     * @return the scan result as a String.
     * @throws IOException if an error occurs during scanning.
     */
    public String scan() throws IOException {
        String os = System.getProperty("os.name").toLowerCase();
        if (os.contains("win")) {
            return scanWindows();
        } else {
            return scanLinux();
        }
    }

    //todo: Merge all Windows-specific scanning logic into one class.
    private String scanWindows() throws IOException {
        // Call your Windows-specific scanning logic here
        return "WiFi Scan Windows not implemented yet.";
    }

    //todo: Create Linux-specific scanning logic.
    private String scanLinux() throws IOException {
        // Call your Linux-specific scanning logic here
        return "WiFi Scan Linux not implemented yet.";
    }
}