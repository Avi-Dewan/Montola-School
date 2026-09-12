package com.montola.school;

import org.flywaydb.core.Flyway;
import org.springframework.boot.LazyInitializationExcludeFilter;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.flyway.FlywayMigrationInitializer;
import org.springframework.context.annotation.Bean;

/**
 * @author avidewan
 * @date 8/27/25
 */
@SpringBootApplication
public class MontolaApplication {

    public static void main(String[] args) {
        SpringApplication.run(MontolaApplication.class, args);
    }

    /**
     * Flyway is a DatabaseInitializer, so with {@code spring.main.lazy-initialization=true}
     * (used in production to speed up startup) migrations would be deferred until the
     * first request. Excluding it keeps migrations running during startup.
     */
    @Bean
    static LazyInitializationExcludeFilter eagerFlyway() {
        return LazyInitializationExcludeFilter.forBeanTypes(Flyway.class, FlywayMigrationInitializer.class);
    }
}
