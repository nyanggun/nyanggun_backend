package org.kosa.congmouse.nyanggoon.service;

import lombok.RequiredArgsConstructor;
import org.kosa.congmouse.nyanggoon.entity.HeritageEncyclopedia;
import org.kosa.congmouse.nyanggoon.repository.HeritageEncyclopediaRepository;
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

    private final HeritageEncyclopediaRepository heritageRepository;

    @Value("${groq.api.model:openai/gpt-oss-20b}")
    private String model;

    public String getChatResponse(String message) {
        // DB에서 키워드 검색
        List<HeritageEncyclopedia> results = heritageRepository.findByNameContainingIgnoreCase(message);

        if (!results.isEmpty()) {
            HeritageEncyclopedia heritage = results.get(0);
            return String.format(
                    "**%s** (%s)\n\n- 소재지: %s\n- 시대: %s\n- 더보기: [상세 페이지](/heritage/%d)",
                    heritage.getName(),
                    heritage.getHeritageCode(),
                    heritage.getAddress(),
                    heritage.getPeriod(),
                    heritage.getId()
            );
        }

        // DB에 없으면 AI 호출
        Map<String, Object> requestBody = Map.of(
                "model", model,
                "messages", List.of(
                        Map.of("role", "system", "content",
                                "너는 문화재 안내 도우미 챗봇이야. " +
                                        "답변은 최대 3줄로 줄이고, 확실하지 않으면 '죄송합니다. 잘 모르겠습니다.'라고 답변해줘."),
                        Map.of("role", "user", "content", message)
                ),
                "temperature", 0.7,
                "max_tokens", 150  // 글자 길이 제한, 3줄 정도
        );

        try {
            Map<String, Object> response = groqWebClient.post()
                    .uri("/chat/completions")
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(requestBody)
                    .retrieve()
                    .bodyToMono(Map.class)
                    .block();

            if (response == null || !response.containsKey("choices")) {
                return "죄송합니다. 잘 모르겠습니다.";
            }

            Map<String, Object> choice = (Map<String, Object>) ((List<?>) response.get("choices")).get(0);
            Map<String, Object> messageObj = (Map<String, Object>) choice.get("message");

            String aiResponse = messageObj.get("content").toString().trim();

            // 만약 AI가 빈 응답을 주거나 너무 짧으면 기본 메시지
            if (aiResponse.isEmpty()) {
                return "죄송합니다. 잘 모르겠습니다.";
            }
            return aiResponse;
        } catch (Exception e) {
            e.printStackTrace();
            return "죄송합니다. 잘 모르겠습니다.";
        }
    }
}
