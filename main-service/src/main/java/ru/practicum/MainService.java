package ru.practicum;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class MainService {
    public static void main(String[] args) {
        SpringApplication.run(MainService.class, args);
    }

    @Bean
    public static StatsClient getStatsClient(@Value("${spring.application.name}") String app,
                                             @Value("${services.stats-service.url}") String url) {
        return new StatsClient(
                app,
                url,
                new ObjectMapper());
    }
}
