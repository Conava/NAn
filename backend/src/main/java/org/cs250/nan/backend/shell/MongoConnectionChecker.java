// src/main/java/org/cs250/nan/backend/database/MongoConnectionChecker.java
package org.cs250.nan.backend.database;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import org.cs250.nan.backend.config.AppProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class MongoConnectionChecker {
    private static final Logger LOGGER = LoggerFactory.getLogger(MongoConnectionChecker.class);

    private final boolean remoteEnabled;
    private final MongoClient client;

    public MongoConnectionChecker(AppProperties props) {
        this.remoteEnabled = props.getDb().isRemoteEnabled();
        if (remoteEnabled) {
            // create client once
            this.client = MongoClients.create(props.getDb().getUri());
        } else {
            this.client = null;
        }
    }

    /**
     * @return true if remoteEnabled and the server is reachable, false otherwise.
     */
    public boolean isConnected() {
        if (!remoteEnabled) {
            return false;
        }
        try {
            // quick ping
            client.listDatabaseNames().first();
            return true;
        } catch (Exception e) {
            LOGGER.warn("MongoDB ping failed, disabling DB logic", e);
            return false;
        }
    }
}
