package org.cs250.nan.backend.parser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class WiFiDataParser {

    /**
     * Attempts to parse rawData as a JSON array.  If that fails,
     * splits on newlines and wraps each non-empty line in {"raw": "..."}.
     */
    public static List<JSONObject> parseStringToListOfJSON(String rawData) {
        List<JSONObject> parsedResults = new ArrayList<>();
        if (rawData == null || rawData.isBlank()) {
            return parsedResults;
        }

        String trimmed = rawData.trim();
        // 1) Try JSON array
        if (trimmed.startsWith("[")) {
            try {
                JSONArray arr = new JSONArray(trimmed);
                for (int i = 0; i < arr.length(); i++) {
                    parsedResults.add(arr.getJSONObject(i));
                }
                return parsedResults;
            } catch (JSONException e) {
                // not a JSON array — fall through to line-based fallback
            }
        }

        // 2) Fallback: treat each line as raw text
        for (String line : trimmed.split("\\r?\\n")) {
            if (!line.isBlank()) {
                JSONObject o = new JSONObject();
                o.put("raw", line);
                parsedResults.add(o);
            }
        }
        return parsedResults;
    }
}