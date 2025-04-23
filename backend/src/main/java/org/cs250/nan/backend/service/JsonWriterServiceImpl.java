package org.cs250.nan.backend.service;

import org.cs250.nan.backend.config.AppProperties;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@Service
public class JsonWriterServiceImpl implements JsonWriterService {

    private static final Logger LOGGER = LoggerFactory.getLogger(JsonWriterServiceImpl.class);
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyyMMdd_HHmmss");

    private final AppProperties props;

    public JsonWriterServiceImpl(AppProperties props) {
        this.props = props;
    }

    @Override
    public String writeJsonFile(List<JSONObject> collectedScans, String fileName) {
        if (collectedScans == null || collectedScans.isEmpty()) {
            LOGGER.warn("Empty scan list; nothing to write.");
            return null;
        }

        try {
            // 1) Determine absolute data-storage directory
            String dataDir = props.getDataStorage();
            Path outputDir = Paths.get(dataDir).toAbsolutePath();
            if (!Files.exists(outputDir)) {
                Files.createDirectories(outputDir);
                LOGGER.debug("Created data-storage directory at {}", outputDir);
            }

            // 2) Build timestamped filename
            String timestamp = DATE_FORMAT.format(new Date());
            String fullFileName = String.format("%s_%s.json", timestamp, fileName);
            Path filePath = outputDir.resolve(fullFileName).toAbsolutePath();

            // 3) Serialize to JSON array
            JSONArray arr = new JSONArray();
            collectedScans.forEach(arr::put);

            // 4) Write out
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath.toFile()))) {
                writer.write(arr.toString(4));
            }

            LOGGER.info("Saved JSON scan to {}", filePath);
            return filePath.toString();
        } catch (IOException e) {
            LOGGER.error("Failed to write JSON file in data-storage: {}", e.getMessage(), e);
            return null;
        }
    }
}
