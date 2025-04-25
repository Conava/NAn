package org.cs250.nan.backend.database;

import org.bson.Document;
import org.bson.types.ObjectId;
import org.cs250.nan.backend.shell.MongoConnectionManager;
import org.json.JSONObject;

public class SaveToMongoDB {

    private final String collectionName;

    public SaveToMongoDB(String collectionName) {
        this.collectionName = collectionName;
    }

    public void saveJsonObject(JSONObject jsonObject) {
        // Check if MongoDB is connected before proceeding
        if (!MongoConnectionManager.isConnected()) {
            System.out.println("MongoDB is not connected. Document not saved.");
            return;  // Exit the method if not connected
        }

        // Convert JSONObject to BSON Document
        Document doc = Document.parse(jsonObject.toString());

        // Add a unique MongoDB _id if not present
        if (!doc.containsKey("_id")) {
            doc.put("_id", new ObjectId()); // Creates a unique MongoDB ObjectId
        }

        // Get the MongoDB collection and insert the document
        MongoConnectionManager.getCollection(collectionName).insertOne(doc);

        // Output the _id of the saved document
        System.out.println("Document saved with _id: " + doc.get("_id"));
    }
}