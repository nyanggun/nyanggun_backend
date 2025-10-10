package org.kosa.congmouse.nyanggoon.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ChatbotService {

    @Qualifier("groqWebClient")
    private final WebClient groqWebClient;

    @Value("${groq.api.model:llama-3.1-70b-versatile}")
    private String model;

    public String getChatResponse(String message) {
        Map<String, Object> requestBody = Map.of(
                "model", model,
                "messages", List.of(
                        Map.of("role", "user", "content", message)
                ),
                "temperature", 0.7, // 선택적
                "max_tokens", 512   // 응답 길이 제한
        );

        try {
            Map<String, Object> response = groqWebClient.post()
                    .uri("/chat/completions")
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(requestBody)
                    .retrieve()
                    .bodyToMono(Map.class)
                    .block();

            if (response == null || response.get("choices") == null) {
                return "AI 응답이 비어 있습니다.";
            }

            Map<String, Object> choice = (Map<String, Object>) ((List<?>) response.get("choices")).get(0);
            Map<String, Object> messageObj = (Map<String, Object>) choice.get("message");

            return messageObj.get("content").toString();

        } catch (Exception e) {
            e.printStackTrace();
            return "AI 응답 중 오류가 발생했습니다: " + e.getMessage();
        }
    }
}
