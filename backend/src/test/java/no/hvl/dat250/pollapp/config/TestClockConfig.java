package no.hvl.dat250.pollapp.config;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;

import java.time.*;

@TestConfiguration
public class TestClockConfig {
    @Bean
    @Primary
    public Clock testClock() {
        return Clock.fixed(
                Instant.parse("2025-01-01T12:00:00Z"),
                ZoneOffset.UTC
        );
    }
}