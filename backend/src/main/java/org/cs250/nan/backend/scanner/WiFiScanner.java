package org.cs250.nan.backend.scanner;

import org.json.JSONObject;

import java.io.IOException;
import java.util.List;

/**
 * Generic Wi-Fi scanner interface. OS-specific scanners implement this.
 */
public interface WiFiScanner {
    List<JSONObject> scan() throws IOException;
}