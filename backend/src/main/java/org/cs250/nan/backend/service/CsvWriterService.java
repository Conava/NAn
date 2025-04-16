package org.cs250.nan.backend.service;

import org.json.JSONObject;

import java.io.IOException;
import java.util.List;

/**
 * Service interface for writing a list of JSON objects to a CSV file.
 */
public interface CsvWriterService {

    /**
     * Writes the provided list of JSONObjects to a CSV file.
     *
     * @param jsonList     the list of JSON objects to write to CSV
     * @param baseFileName the base file name used to create the output file (timestamp will be prepended and .csv appended)
     * @throws IOException if an error occurs during file writing
     */
    void writeJsonListToCsv(List<JSONObject> jsonList, String baseFileName) throws IOException;
}