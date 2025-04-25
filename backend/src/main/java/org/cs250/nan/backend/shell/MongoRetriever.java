package org.cs250.nan.backend.shell;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.model.Filters;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class MongoRetriever {

    public static List<JSONObject> getDocumentsByKeyValueContains(String key, String valueFragment) {
        List<JSONObject> results = new ArrayList<>();

        if (!MongoConnectionManager.isConnected()) {
            System.out.println("MongoDB is not connected. Cannot retrieve documents.");
            return results;
        }

        try {
            MongoCollection<Document> collection = MongoConnectionManager.getCollection("allData");

            // Create a case-insensitive regex pattern to match documents where the value contains the input string
            Pattern regex = Pattern.compile(Pattern.quote(valueFragment), Pattern.CASE_INSENSITIVE);
            Bson filter = Filters.regex(key, regex);

            MongoCursor<Document> cursor = collection.find(filter).iterator();

            while (cursor.hasNext()) {
                Document doc = cursor.next();
                JSONObject json = new JSONObject(doc.toJson());
                results.add(json);
            }

            cursor.close();
        } catch (Exception e) {
            System.err.println("Error retrieving documents: " + e.getMessage());
        }

        return results;
    }

    public static void main(String[] args) throws IOException {
        MongoConnectionManager.initialize(
                "mongodb+srv://mleavitt1457:paPq1zK5gmrdk7rT@cs250-nan.2finb.mongodb.net/?retryWrites=true&w=majority&appName=CS250-NAn",
                "wifiData"
        );

        List<JSONObject> matches = MongoRetriever.getDocumentsByKeyValueContains("sessionID", "25042515");

        GenerateUIcsv.generateUIcsv(matches, "csvForUI");

        MongoConnectionManager.close();

//        for (JSONObject doc : matches) {
//            System.out.println(doc.toString(4));
//        }
    }
}