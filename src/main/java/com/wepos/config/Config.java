package com.wepos.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class Config {
    @Value("${giftlov.api.base-url}")
    private String baseUrl;

    @Value("${giftlov.api.username}")
    private String username;

    @Value("${giftlov.api.password}")
    private String password;

    @Value("${giftlov.api.secret}")
    private String apiSecret;

    // Getters
    public String getBaseUrl() {
        return baseUrl;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getApiSecret() {
        return apiSecret;
    }

    @Bean
    public RestTemplate restTemplate(RestTemplateBuilder builder){
        return builder.build();
    }
}
