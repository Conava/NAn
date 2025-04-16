package org.cs250.nan.backend.service;

import org.json.JSONObject;

import java.util.List;

/**
 * Service interface for merging GPS data into a list of WiFi JSON objects.
 */
public interface MergeJsonDataService {

    /**
     * Merges the key/value pairs from the GPS JSON object into each JSON object in the provided list.
     *
     * @param wifiJsonList a list of WiFi JSON objects
     * @param gpsJson      an object containing GPS data
     * @return the list with merged JSON objects
     */
    List<JSONObject> mergeJSONObjects(List<JSONObject> wifiJsonList, JSONObject gpsJson);
}