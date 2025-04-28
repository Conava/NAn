package org.cs250.nan.backend.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import lombok.Data;

/**
 * Application‑level configuration properties under prefix `app`.
 * Immutable, validated and bound via constructor.
 */
@Data
@ConfigurationProperties(prefix = "app")
public class AppProperties {
    private String baseDir;
    private String dataStorage;
    private boolean keepHistory;
    private boolean activateGui;
    private String logFile;
    private final Monitor monitor = new Monitor();
    private final Db db = new Db();

    @Data
    public static class Monitor {
        private int scanInterval;
        private boolean gpsOn;
        private boolean kmlOutput;
        private boolean csvOutput;
        private String jsonFileName;
        private String kmlFileName;
        private String csvFileName;
    }

    /**
     * Database connectivity settings.
     */
    @Data
    public static class Db {
        /** If false, all Mongo logic is disabled at runtime. */
        private boolean remoteEnabled;
        /** Mongo URI when enabled */
        private String remoteUrl;
    }
}
