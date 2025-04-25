package org.cs250.nan.backend.shell;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.MongoCollection;
import org.bson.Document;

public class MongoConnectionManager {

    private static MongoClient mongoClient;
    private static String defaultDatabase;

    // Initialize connection (call this once at app start)
    public static void initialize(String uri, String dbName) {
        if (mongoClient == null) {
            mongoClient = MongoClients.create(uri);
            defaultDatabase = dbName;
            System.out.println("MongoDB connection initialized.");
        }
    }

    public static MongoClient getClient() {
        if (mongoClient == null) {
            throw new IllegalStateException("MongoClient not initialized. Call initialize() first.");
        }
        return mongoClient;
    }

    public static MongoDatabase getDatabase() {
        return getClient().getDatabase(defaultDatabase);
    }

    public static MongoDatabase getDatabase(String dbName) {
        return getClient().getDatabase(dbName);
    }

    public static MongoCollection<Document> getCollection(String collectionName) {
        return getDatabase().getCollection(collectionName);
    }

//    public static boolean isConnected() {
//        try {
//            // Run a simple ping command to check if the connection is valid
//            Document commandResult = getDatabase().runCommand(new Document("ping", 1));
//            return commandResult.containsKey("ok") && commandResult.getDouble("ok") == 1.0;
//        } catch (Exception e) {
//            System.err.println("Error connecting to MongoDB: " + e.getMessage());
//            return false;
//        }
//    }

    public static boolean isConnected() {
        try {
            // Run a simple ping command to check if the connection is valid
            Document commandResult = getDatabase().runCommand(new Document("ping", 1));

            // Safely check and retrieve the "ok" field, considering it might be an Integer or Double
            if (commandResult.containsKey("ok")) {
                Object okValue = commandResult.get("ok");
                if (okValue instanceof Double) {
                    return (Double) okValue == 1.0;
                } else if (okValue instanceof Integer) {
                    return ((Integer) okValue).doubleValue() == 1.0;
                }
            }

            return false;  // Default to false if the "ok" field is not found or not of expected types
        } catch (Exception e) {
            System.err.println("Error connecting to MongoDB: " + e.getMessage());
            return false;
        }
    }

    public static void close() {
        if (mongoClient != null) {
            mongoClient.close();
            mongoClient = null;
            System.out.println("MongoDB connection closed.");
        }
    }
}
