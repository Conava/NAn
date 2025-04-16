package org.cs250.nan.backend.service;

import org.json.JSONObject;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Implementation of {@link MergeJsonDataService} that merges GPS data into WiFi JSON objects.
 */
@Service
public class MergeJsonDataServiceImpl implements MergeJsonDataService {

    /**
     * Merges the key/value pairs from the GPS JSON object into each WiFi JSON object in the list.
     *
     * @param wifiJsonList a list of WiFi JSON objects
     * @param gpsJson      an object containing GPS data
     * @return the list with merged JSON objects
     */
    @Override
    public List<JSONObject> mergeJSONObjects(List<JSONObject> wifiJsonList, JSONObject gpsJson) {
        if (gpsJson == null) {
            return wifiJsonList;
        }
        for (JSONObject wifiObject : wifiJsonList) {
            for (String key : gpsJson.keySet()) {
                wifiObject.put(key, gpsJson.get(key));
            }
        }
        return wifiJsonList;
    }
}