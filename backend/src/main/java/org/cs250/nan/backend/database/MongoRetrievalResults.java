package org.cs250.nan.backend.database;
import org.json.JSONObject;
import java.util.List;

public class MongoRetrievalResults {
    private List<JSONObject> results; // List of results (JSON objects)
    private String searchKey; // Store the key used in the search
    private String searchValue; // Store the value or fragment used in the search
    private long timestamp; // Store when the search was performed

    // Constructor
    public MongoRetrievalResults(List<JSONObject> results, String searchKey, String searchValue) {
        this.results = results;
        this.searchKey = searchKey;
        this.searchValue = searchValue;
        this.timestamp = System.currentTimeMillis(); // Capture the time of the search
    }

    // Getters and Setters
    public List<JSONObject> getResults() {
        return results;
    }

    public void setResults(List<JSONObject> results) {
        this.results = results;
    }

    public String getSearchKey() {
        return searchKey;
    }

    public void setSearchKey(String searchKey) {
        this.searchKey = searchKey;
    }

    public String getSearchValue() {
        return searchValue;
    }

    public void setSearchValue(String searchValue) {
        this.searchValue = searchValue;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public String toString() {
        return "MongoRetrievalResults{" + "results=" + results.size() + " entries" +'}';
    }
}