package com.openstates.app.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {

    @Value("${openstates.api.base-url}")
    private String baseUrl;

    @Value("${openstates.api.key}")
    private String apiKey;

    @Bean
    public WebClient openStatesWebClient() {
        return WebClient.builder()
                .baseUrl(baseUrl)
                .defaultHeader("X-API-KEY", apiKey)
                .build();
    }
}
