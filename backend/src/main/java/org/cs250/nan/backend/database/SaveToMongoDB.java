package org.cs250.nan.backend.database;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;

@Service
public class SaveToMongoDB {

    private final MongoTemplate mongoTemplate;

    @Autowired
    public SaveToMongoDB(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    public void insertJSONObject(JSONObject jsonObject) {
        Document doc = Document.parse(jsonObject.toString());

        if (!doc.containsKey("_id")) {
            doc.put("_id", new ObjectId());
        }

        mongoTemplate.getCollection("all_data").insertOne(doc); // collection name currently hard-coded, add a setting later to allow user to specify and pass as a parameter

        System.out.println("Inserted document with _id: " + doc.get("_id"));
    }
}