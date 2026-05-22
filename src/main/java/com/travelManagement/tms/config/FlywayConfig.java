package com.travelManagement.tms.config;

import org.flywaydb.core.Flyway;
import org.springframework.boot.autoconfigure.flyway.FlywayMigrationStrategy;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Custom Flyway migration strategy.
 * Runs repair() before migrate() to clear any failed migration state
 * (e.g. after a previously failed V2 run).
 */
@Configuration
public class FlywayConfig {

    @Bean
    public FlywayMigrationStrategy flywayMigrationStrategy() {
        return flyway -> {
            flyway.repair();   // Clear failed migration checksums
            flyway.migrate();  // Run pending migrations
        };
    }
}
