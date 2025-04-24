package org.cs250.nan.backend.config;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.Map;

/**
 * Persist the current AppProperties back to ${app.base-dir}/settings.yml
 * in a form that Spring will re-import on startup.
 */
@Service
public class SettingsService {

    private final ObjectMapper yamlMapper;
    private final Path externalFile;

    public SettingsService(
            @Value("${app.base-dir}") String baseDir
    ) {
        // 1) Create a YAML mapper that outputs kebab-case
        this.yamlMapper = new ObjectMapper(new YAMLFactory())
                .setPropertyNamingStrategy(PropertyNamingStrategies.KEBAB_CASE)
                // 2) Make sure our AppProperties and its nested classes ignore Spring internals
                .addMixIn(AppProperties.class, IgnoreSpringInternalMixIn.class)
                .addMixIn(AppProperties.Db.class, IgnoreSpringInternalMixIn.class)
                .addMixIn(AppProperties.Monitor.class, IgnoreSpringInternalMixIn.class);

        // 3) Always write to settings.yml under the base-dir
        this.externalFile = Paths.get(baseDir, "settings.yml");
    }

    /**
     * Write out exactly:
     *
     * <pre>
     * app:
     *   base-dir: …
     *   data-storage: …
     *   …
     *   db:
     *     remote-enabled: …
     *     remote-url: …
     *   monitor:
     *     scan-interval: …
     *     …
     * </pre>
     */
    public void saveExternalConfig(AppProperties snapshot) throws IOException {
        // Ensure the directory exists
        Files.createDirectories(externalFile.getParent());

        // Wrap under “app:” so Spring will bind it to app.* on next startup
        Map<String, AppProperties> root = Collections.singletonMap("app", snapshot);

        yamlMapper
                .writerWithDefaultPrettyPrinter()
                .writeValue(externalFile.toFile(), root);
    }

    /**
     * Mixin to drop any Spring/CGLIB internals from serialization.
     */
    @JsonIgnoreProperties({
            "beanFactory",
            "applicationContext",
            "$$enhancerBySpringCGLIB"
    })
    private abstract static class IgnoreSpringInternalMixIn {
    }
}
