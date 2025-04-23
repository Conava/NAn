package org.cs250.nan.backend.scanner;

import org.json.JSONObject;

import java.io.IOException;

/**
 * Generic GPS scanner interface. OS-specific scanners implement this.
 */
public interface GPSScanner {
    JSONObject scan() throws IOException;
}
