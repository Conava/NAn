package org.cs250.nan.backend;

import lombok.extern.slf4j.Slf4j;
import org.cs250.nan.backend.config.AppProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.context.annotation.Bean;

/**
 * Entry point for the NAn NetworkAnalyzer Spring Boot application.
 */
@SpringBootApplication
@EnableAsync
@EnableScheduling
@EnableConfigurationProperties(AppProperties.class)
public class BackendApplication {

    private static final Logger LOGGER = LoggerFactory.getLogger(BackendApplication.class);

    public static void main(String[] args) {
        SpringApplication.run(BackendApplication.class, args);
    }

    /**
     * Logs key environment details once the application has started.
     */
    @Bean
    public ApplicationRunner logEnvironment() {
        return args -> {
            LOGGER.info("NAn NetworkAnalyzer started successfully 🚀");
            LOGGER.info("  OS   : {}", System.getProperty("os.name"));
            LOGGER.info("  Arch : {}", System.getProperty("os.arch"));
            LOGGER.info("  Java : {}", System.getProperty("java.version"));
            LOGGER.info("  Home : {}", System.getProperty("user.home"));
            LOGGER.info("  CWD  : {}", System.getProperty("user.dir"));
        };
    }
}