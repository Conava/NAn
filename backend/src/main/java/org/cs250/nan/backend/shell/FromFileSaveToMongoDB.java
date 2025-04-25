package org.cs250.nan.backend.database;

import org.json.JSONArray;
import org.json.JSONObject;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class FromFileSaveToMongoDB {

    public static List<JSONObject> readJsonFileToList(String filePath) {
        List<JSONObject> jsonObjects = new ArrayList<>();

        try {
            // Read the entire file content as a string
            String content = new String(Files.readAllBytes(Paths.get(filePath)));

            // Parse the string content into a JSONArray
            JSONArray jsonArray = new JSONArray(content);

            // Convert each item in the array to a JSONObject and add to list
            for (int i = 0; i < jsonArray.length(); i++) {
                jsonObjects.add(jsonArray.getJSONObject(i));
            }

        } catch (IOException e) {
            System.err.println("Failed to read JSON file: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Error parsing JSON content: " + e.getMessage());
        }

        return jsonObjects;
    }

//    public static void fromFileSaveToMongoDB(String filePath) {
//
//        // If connected to MongoDB, write Each JSON object from the single scan to MongoDB
//        MongoConnectionChecker checker = SpringContext.getBean(MongoConnectionChecker.class);
//        boolean mongoOk = checker.isConnected();
//
//        if (mongoOk) {
//            List<JSONObject> scans = readJsonFileToList(filePath);
//            SaveToMongoDB mongoSaver = SpringContext.getBean(SaveToMongoDB.class);
//            for (JSONObject scan : scans) {
//                mongoSaver.insertJSONObject(scan);
//            }
//        }
//        else {
//            System.out.println("MongoDB connection failed: " + mongoOk);
//        }
//    }
}