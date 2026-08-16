package org.afiapass.gateway;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Testcontainers
class AfiapassApplicationTests {

    // 1. Define the isolated MySQL container
    @Container
    static MySQLContainer<?> mysql = new MySQLContainer<>("mysql:8.0")
            .withDatabaseName("afiapass_test")
            .withUsername("testuser")
            .withPassword("testpass");

    // 2. Dynamically inject the container's connection details into Spring Boot
    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", mysql::getJdbcUrl);
        registry.add("spring.datasource.username", mysql::getUsername);
        registry.add("spring.datasource.password", mysql::getPassword);

        // Ensure Flyway runs against this test database
        registry.add("spring.flyway.enabled", () -> "true");

        // Inject a dummy Stellar Secret via the registry to avoid using System.setProperty
        registry.add("afiapass.platform-secret-seed", () -> "SB2M2B3R2Z7X3N3H5T4O5W5M4R3N2M3N4X3N2M3N4X3N2M3N4X3N2M3N");
    }

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Test
    void contextLoadsAndFlywayMigratesSuccessfully() {
        // Assert the container is running
        assertThat(mysql.isRunning()).isTrue();

        // If Flyway ran successfully, the permit_record table will exist.
        // We can prove this by querying the information_schema
        Integer tableCount = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM information_schema.tables WHERE table_schema = 'afiapass_test' AND table_name = 'permit_record'",
                Integer.class
        );

        assertThat(tableCount).isEqualTo(1);
    }
}