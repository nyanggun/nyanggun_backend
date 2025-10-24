package org.kosa.congmouse.nyanggoon.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class GroqConfig {

    private String apiUrl = "https://api.groq.com/openai/v1";

    private String apiKey = "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";

    @Bean
    public WebClient groqWebClient() {
        return WebClient.builder()
                .baseUrl(apiUrl)
                .defaultHeader("Authorization", "Bearer " + apiKey)
                .defaultHeader("Content-Type", "application/json")
                .build();
    }
}
