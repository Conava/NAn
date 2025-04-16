package org.cs250.nan.backend.service;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

/**
 * Implementation of the {@link CsvWriterService} that writes JSON data to a CSV file.
 */
@Service
public class CsvWriterServiceImpl implements CsvWriterService {

    private static final Logger LOGGER = LoggerFactory.getLogger(CsvWriterServiceImpl.class);

    /**
     * Writes the supplied list of JSONObjects to a CSV file.
     *
     * @param jsonList the list of JSON objects to be written
     * @param baseFileName the base name for the CSV file (a timestamp and extension will be added)
     * @throws IOException if an I/O error occurs during file writing
     */
    @Override
    public void writeJsonListToCsv(List<JSONObject> jsonList, String baseFileName) throws IOException {
        if (jsonList.isEmpty()) {
            LOGGER.info("Empty JSON list, nothing to write.");
            return;
        }

        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss");
        String currentDateTime = sdf.format(new Date());
        String fileName = currentDateTime + "_" + baseFileName + ".csv";

        // Extract headers from the first JSON object
        Set<String> headers = jsonList.get(0).keySet();

        try (FileWriter writer = new FileWriter(fileName)) {
            // Write CSV header
            writer.append(String.join(",", headers));
            writer.append("\n");

            // Write CSV rows for each JSON object
            for (JSONObject json : jsonList) {
                List<String> row = new ArrayList<>();
                for (String header : headers) {
                    row.add(json.optString(header, ""));
                }
                writer.append(String.join(",", row));
                writer.append("\n");
            }
            LOGGER.info("CSV file created successfully: {}", fileName);
        } catch (IOException e) {
            LOGGER.error("Error writing CSV file: {}", e.getMessage());
            throw e;
        }
    }
}