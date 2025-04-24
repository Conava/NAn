// src/main/java/org/cs250/nan/backend/database/SaveToMongoDB.java
package org.cs250.nan.backend.database;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import org.cs250.nan.backend.config.AppProperties;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class SaveToMongoDB {
    private static final Logger LOGGER = LoggerFactory.getLogger(SaveToMongoDB.class);

    private final boolean remoteEnabled;
    private final MongoCollection<Document> collection;

    public SaveToMongoDB(AppProperties props, MongoConnectionChecker checker) {
        this.remoteEnabled = props.getDb().isRemoteEnabled();
        if (remoteEnabled && checker.isConnected()) {
            MongoClient client = MongoClients.create(props.getDb().getRemoteUrl());
            MongoDatabase db = client.getDatabase("mydb"); // adjust your DB name
            this.collection = db.getCollection("scans");
        } else {
            this.collection = null;
        }
    }

    /**
     * Inserts a scan result. If remote disabled, just logs once.
     */
    public void insertJSONObject(JSONObject json) {
        if (!remoteEnabled || collection == null) {
            LOGGER.info("Remote DB disabled – would have saved: {}", json);
            return;
        }
        try {
            collection.insertOne(Document.parse(json.toString()));
        } catch (Exception e) {
            LOGGER.error("Failed to insert scan into MongoDB", e);
        }
    }
}
