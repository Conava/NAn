package org.cs250.nan.backend.shell;

import org.json.JSONObject;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class GenerateUIcsv {

    public static String getDownloadsFolderPath() {
        String userHome = System.getProperty("user.home");
        String downloadsPath = userHome + "/Downloads";  // Works on Windows, macOS, and Linux
        return downloadsPath;
    }

    public static void generateUIcsv(List<JSONObject> jsonList, String fileName) throws IOException {

        if (jsonList.isEmpty()) {
            System.out.println("Empty JSON list, nothing to write.");
            return;
        }

        String filePath = GenerateUIcsv.getDownloadsFolderPath() + File.separator + fileName + ".csv";

        // Extract headers (keys) from the first JSON object
        Set<String> headers = jsonList.get(0).keySet();

        // Open CSV file writer
        try (FileWriter writer = new FileWriter(filePath)) {
            // Write headers
            writer.append(String.join(",", headers));
            writer.append("\n");

            // Write data rows
            for (JSONObject json : jsonList) {
                List<String> row = new ArrayList<>();
                for (String header : headers) {
                    row.add(json.optString(header, "")); // Handle missing keys
                }
                writer.append(String.join(",", row));
                writer.append("\n");
            }

            System.out.println("CSV file created successfully: " + filePath);
        }

    }

}
