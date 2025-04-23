package org.cs250.nan.backend;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class BackendApplication {
    private static final Logger LOGGER = LoggerFactory.getLogger(BackendApplication.class);

    public static void main(String[] args) {
        SpringApplication.run(BackendApplication.class, args);
    }


    /**
     * Logs environment details once the application is up.
     */
    @Bean
    public ApplicationRunner logEnvironment() {
        return args -> {
            LOGGER.info("NAn NetworkAnalyzer started successfully 🚀");
            LOGGER.info("Environment Details:");
            LOGGER.info("  OS    : {}", System.getProperty("os.name"));
            LOGGER.info("  Arch  : {}", System.getProperty("os.arch"));
            LOGGER.info("  Java  : {}", System.getProperty("java.version"));
            LOGGER.info("  Home  : {}", System.getProperty("user.home"));
            LOGGER.info("  CWD   : {}", System.getProperty("user.dir"));
        };
    }
}
