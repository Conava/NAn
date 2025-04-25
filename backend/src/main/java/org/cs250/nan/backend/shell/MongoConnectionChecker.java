package org.cs250.nan.backend.shell;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.MongoCollection;
import org.bson.Document;

public class MongoConnectionChecker {
    public static boolean checkConnection() {
        String uri = "mongodb+srv://mleavitt1457:paPq1zK5gmrdk7rT@cs250-nan.2finb.mongodb.net/?retryWrites=true&w=majority&appName=CS250-NAn";

        try (MongoClient mongoClient = MongoClients.create(uri)) {
            // Replace with your actual DB and collection names
            MongoDatabase database = mongoClient.getDatabase("wifiData");
            MongoCollection<Document> collection = database.getCollection("allData");

            Document commandResult = database.runCommand(new Document("ping", 1));
            System.out.println("Ping result: " + commandResult.toJson());
            System.out.println("Connection successful!");
            return true;
        } catch (Exception e) {
            System.err.println("Connection failed: " + e.getMessage());
            return false;
        }
    }
    public static void main(String[] args) {
        System.out.println(checkConnection());
    }
}
