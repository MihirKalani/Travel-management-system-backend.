package com.travelManagement.tms.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

/**
 * DataSeeder is now a no-op.
 * All seed data is handled by Flyway migration V3__seed_data.sql.
 */
@Component
public class DataSeeder implements CommandLineRunner {

    @Override
    public void run(String... args) {
        // Flyway handles seeding via V3__seed_data.sql
        System.out.println("[TMS] Flyway has initialized the database schema and seed data.");
    }
}
