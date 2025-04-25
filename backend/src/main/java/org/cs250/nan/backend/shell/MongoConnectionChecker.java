package org.cs250.nan.backend.database;

import com.mongodb.MongoException;
import com.mongodb.client.MongoClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class MongoConnectionChecker {

    private final boolean connected;

    @Autowired
    public MongoConnectionChecker(MongoClient mongoClient) {
        boolean connectionStatus;
        try {
            mongoClient.getDatabase("wifiData").runCommand(new org.bson.Document("ping", 1));
            connectionStatus = true;
            System.out.println("Connected to MongoDB");
        } catch (MongoException e) {
            connectionStatus = false;
            System.err.println("MongoDB connection failed: " + e.getMessage());
        }
        this.connected = connectionStatus;
    }

    public boolean isConnected() {
        return connected;
    }
}
