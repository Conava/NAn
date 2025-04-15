package org.cs250.nan.backend.service;

import org.json.JSONArray;
import org.json.JSONObject;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class WriteJSONfile {

    public static void writeJSONfile(List<JSONObject> collectedScans, String fileName) {

        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss");
        String currentDateTime = sdf.format(new Date());
        fileName = currentDateTime + "_" + fileName + ".csv";

        // Create a JSON array to hold all the objects
        JSONArray jsonArray = new JSONArray();

        // Add all JSON objects to the JSON array
        for (JSONObject jsonObject : collectedScans) {
            jsonArray.put(jsonObject);
        }

        // Write the JSON array to a file
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileName))) {
            writer.write(jsonArray.toString(4)); // The '4' adds pretty-printing indentation
            System.out.println("Successfully saved to " + fileName);
        } catch (IOException e) {
            System.err.println("Error saving JSON file: " + e.getMessage());
        }
    }
}
