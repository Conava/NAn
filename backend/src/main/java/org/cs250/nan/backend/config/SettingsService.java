package org.cs250.nan.backend.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Saves the current AppProperties back to the external application.yml
 * so that overrides survive restart.
 */
@Service
public class SettingsService {
    private final ObjectMapper mapper;
    private final Path externalFile;

    public SettingsService(ObjectMapper mapper,
                           @Value("${spring.config.location:file:${app.base-dir}/settings.yml}") String path) {
        this.mapper = mapper;
        this.externalFile = Paths.get(path.replaceFirst("^file:", ""));
    }

    /**
     * now takes the caller’s copy, not the Spring bean
     */
    public void saveExternalConfig(AppProperties snapshot) throws IOException {
        // ensure dir
        Files.createDirectories(externalFile.getParent());
        // write out a nice YAML (or JSON) of exactly the fields on your snapshot:
        mapper
                .writerWithDefaultPrettyPrinter()
                .writeValue(externalFile.toFile(), snapshot);
    }
}
