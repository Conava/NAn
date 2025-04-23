package org.cs250.nan.backend.service;

import org.json.JSONObject;
import java.util.List;

/**
 * Interface for services that write scan data to JSON files.
 */
public interface JsonWriterService {

    /**
     * Writes scan results to a JSON file with the specified name.
     *
     * @param collectedScans the list of JSON objects to write
     * @param fileName       the base name for the output file (without extension)
     * @return the path to the created file or null if the operation failed
     */
    String writeJsonFile(List<JSONObject> collectedScans, String fileName);
}