package org.cs250.nan.backend.parser;

import org.json.JSONException;
import org.json.JSONObject;

public class GPSDataParser {

    /**
     * Attempts to parse rawData as a JSON object.  If that fails,
     * wraps the entire string as {"rawGps": "..."}.
     */
    public static JSONObject parseStringToJSON(String rawData) {
        if (rawData == null || rawData.isBlank()) {
            return new JSONObject(); // empty
        }

        String trimmed = rawData.trim();
        if (trimmed.startsWith("{")) {
            try {
                return new JSONObject(trimmed);
            } catch (JSONException e) {
                // not JSON → fallback
            }
        }

        // fallback: raw GPS output
        JSONObject o = new JSONObject();
        o.put("rawGps", trimmed);
        return o;
    }
}