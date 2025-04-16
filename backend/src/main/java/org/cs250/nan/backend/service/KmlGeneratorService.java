package org.cs250.nan.backend.service;

import org.json.JSONObject;
import java.util.List;

/**
 * Service interface for generating KML files from JSON data.
 */
public interface KmlGeneratorService {

    /**
     * Converts a list of JSONObjects containing geospatial data into a KML file.
     *
     * @param jsonObjects the list of JSONObjects with geospatial details
     * @param baseFileName the base file name used to create the output file (timestamp will be prepended)
     */
    void generateKml(List<JSONObject> jsonObjects, String baseFileName);
}