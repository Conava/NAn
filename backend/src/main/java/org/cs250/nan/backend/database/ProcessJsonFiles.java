package org.cs250.nan.backend.database;

import java.io.File;

public class ProcessJsonFiles {

    public static void getFiles(String directoryPath) {
        File dir = new File(directoryPath);

        if (!dir.exists() || !dir.isDirectory()) {
            System.err.println("Invalid directory: " + directoryPath);
            return;
        }

        // Filter for .json files
        File[] jsonFiles = dir.listFiles((d, name) -> name.toLowerCase().endsWith(".json"));

        if (jsonFiles == null || jsonFiles.length == 0) {
            System.out.println("No .json files found in directory: " + directoryPath);
            return;
        }

        for (File jsonFile : jsonFiles) {
            String path = jsonFile.getAbsolutePath();
            FromFileSaveToMongoDB.fromFileSaveToMongoDB(path);
        }
    }
}
