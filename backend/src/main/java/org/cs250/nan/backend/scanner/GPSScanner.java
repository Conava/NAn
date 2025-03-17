package org.cs250.nan.backend.scanner;
import org.cs250.nan.backend.toBeDeprecated.SingleGPSScanWindows;
import java.io.IOException;

public class GPSScanner {

    /**
     * Scans for GPS data using the appropriate OS‐specific implementation.
     *
     * @return the scan result as a String.
     * @throws IOException if an error occurs during scanning.
     */
    public String scan() throws IOException {
        String os = System.getProperty("os.name").toLowerCase();
        if (os.contains("win")) {
            return scanWindows(true);
        } else {
            return scanLinux();
        }
    }

    //todo: This method should be adjusted to handle the ignoreGPGSV parameter correctly. (btw what even is it?)
    //todo: All logic for windows gps scanning should be moved to one single class
    private String scanWindows(boolean ignoreGPGSV) throws IOException {
        // Depending on your logic, use the ignoreGPGSV parameter as needed.
        if (ignoreGPGSV) {
            SingleGPSScanWindows gpsScanner = new SingleGPSScanWindows();
            return gpsScanner.getGPSDataIgnoreGPGSV();
        } else {
            // Adjust if you have a different Windows GPS scanning method.
            SingleGPSScanWindows gpsScanner = new SingleGPSScanWindows();
            return gpsScanner.getGPSDataIgnoreGPGSV();
        }
    }

    //todo: Linux-specific GPS scan logic.
    private String scanLinux() throws IOException {
        /*
        SingleGPSScanLinux gpsScanner = new SingleGPSScanLinux();
        return gpsScanner.getGPSData();
         */
        // For now, we will just return a placeholder string.
        return "Linux GPS scan not implemented yet.";
    }
}