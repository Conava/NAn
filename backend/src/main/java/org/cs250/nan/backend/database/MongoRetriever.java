package org.cs250.nan.backend.database;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.cs250.nan.backend.config.AppProperties;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Pattern;

@Service
public class MongoRetriever {
    private static final Logger LOGGER = LoggerFactory.getLogger(MongoRetriever.class);

    private boolean remoteEnabled = false;
    private MongoCollection<Document> collection = null;

    public MongoRetriever(AppProperties props, MongoConnectionChecker checker) {
        remoteEnabled = props.getDb().isRemoteEnabled();
        if (remoteEnabled && checker.isConnected()) {
//            MongoClient client = MongoClients.create(props.getDb().getRemoteUrl());
            MongoClient client = checker.client;
            MongoDatabase db = client.getDatabase("wifiData"); // adjust your DB name
            collection = db.getCollection("allData");
        } else {
            collection = null;
        }
    }

    /**
     * Get documents from MongoDB, return as a list of JSON objects.
     */
    public List<JSONObject> getDocumentsByKeyValueContains(String key, String valueFragment) {
        List<JSONObject> results = new ArrayList<>();

        if (!remoteEnabled || collection == null) {
            LOGGER.info("Remote DB disabled, cannot retrieve data until connection is established.");
            return results;
        }

        Scanner scanner = new Scanner(System.in);

        if (key == null || key.isEmpty()) {
            System.out.print("Enter key to search: ");
            key = scanner.nextLine();
        }

        if (valueFragment == null || valueFragment.isEmpty()) {
            System.out.print("Enter value fragment to search: ");
            valueFragment = scanner.nextLine();
        }

        try {
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

//    public static void main(String[] args) throws IOException {
////        // Create a Scanner object for reading input
////        Scanner scanner = new Scanner(System.in);
////        // Prompt user for the first string
////        System.out.print("Enter the key (ex: sessionID): ");
////        String key = scanner.nextLine();
////        // Prompt user for the second string
////        System.out.print("Enter the value/value fragment (ex: YYMMDDHHMM [as numbers]): ");
////        String value = scanner.nextLine();
//
//        MongoConnectionManager.initialize("mongodb+srv://mleavitt1457:paPq1zK5gmrdk7rT@cs250-nan.2finb.mongodb.net/?retryWrites=true&w=majority&appName=CS250-NAn", "wifiData");
//
//        String key = "sessionID";
//        String value = "250425151755";
//
//        List<JSONObject> matches = MongoRetriever.getDocumentsByKeyValueContains(key, value);
//
//        GenerateUIcsv.generateUIcsv(matches, "csvForUI");
//
//        MongoConnectionManager.close();
//
////        for (JSONObject doc : matches) {
////            System.out.println(doc.toString(4));
////        }
//    }
}