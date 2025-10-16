package org.kosa.congmouse.nyanggoon.service;

import lombok.RequiredArgsConstructor;
import org.kosa.congmouse.nyanggoon.dto.ChatResponseDto;
import org.kosa.congmouse.nyanggoon.entity.HeritageEncyclopedia;
import org.kosa.congmouse.nyanggoon.repository.HeritageEncyclopediaRepository;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ChatbotService {

    @Qualifier("groqWebClient")
    private final WebClient groqWebClient;

    private final HeritageEncyclopediaRepository heritageEncyclopediaRepository;

    @Value("${groq.api.model:openai/gpt-oss-20b}")
    private String model;

    /**
     * 메시지 전송 처리 (userId를 통해 로그인 여부 체크)
     */
    public ChatResponseDto sendMessage(String message, Long userId) {

        // 로그인 안 되어 있으면
        if (userId == null) {
            return ChatResponseDto.builder()
                    .response("로그인 후 이용 가능합니다.")
                    .options(Collections.emptyList())
                    .build();
        }

        String lowerMsg = message.toLowerCase();

        //“서울” 관련 질의
        if (lowerMsg.contains("서울")) {
            // “몇 개” or “얼마나” 등의 표현이 포함되어 있으면 → 개수
            if (lowerMsg.contains("몇") || lowerMsg.contains("얼마")) {
                // Repository에는 countByAddressContaining 없으므로 직접 조회 후 count 처리
                List<HeritageEncyclopedia> seoulList =
                        heritageEncyclopediaRepository.findByNameContainingIgnoreCase("서울");
                long count = seoulList.size();

                return ChatResponseDto.builder()
                        .response("서울에 등록된 문화재는 총 **" + count + "개**입니다.")
                        .options(Collections.emptyList())
                        .build();
            }

            // “문화재”, “리스트”, “보여줘” 등 포함 시 → list로 처리
            if (lowerMsg.contains("문화재") || lowerMsg.contains("리스트") || lowerMsg.contains("보여")) {
                List<HeritageEncyclopedia> seoulList =
                        heritageEncyclopediaRepository.findByNameContainingIgnoreCase("서울");

                if (seoulList.isEmpty()) {
                    return ChatResponseDto.builder()
                            .response("서울에 등록된 문화재 정보를 찾을 수 없습니다.")
                            .options(Collections.emptyList())
                            .build();
                }

                String listText = seoulList.stream()
                        .limit(5)
                        .map(h -> "• " + h.getName())
                        .collect(Collectors.joining("<br/>"));

                return ChatResponseDto.builder()
                        .response("서울에 있는 주요 문화재입니다:<br/><br/>" + listText +
                                "<br/><br/>더 많은 정보는 [서울 문화재 목록](/heritage?region=서울)에서 확인할 수 있습니다.")
                        .options(seoulList.stream().limit(5).map(HeritageEncyclopedia::getName).collect(Collectors.toList()))
                        .build();
            }
        }

        // DB에서 문화재 이름 기반 검색
        List<HeritageEncyclopedia> heritageResults =
                heritageEncyclopediaRepository.findHeritageByMessageContent(message);

        String finalResponse;
        List<String> options = new ArrayList<>();

        if (heritageResults.size() == 1) {
            // 하나만 매칭 → 상세정보 반환
            finalResponse = buildHeritageResponse(heritageResults.get(0));
        } else if (heritageResults.size() > 1) {
            // 여러 개 매칭 → 선택 유도
            finalResponse = "검색 결과가 여러 개입니다. 어떤 문화재를 알고 싶으신가요?";
            options = heritageResults.stream()
                    .limit(5)
                    .map(HeritageEncyclopedia::getName)
                    .collect(Collectors.toList());
        } else {
            // DB에 없음 → AI 요청
            String aiResponse = callGroqApi(message);

            // AI 답변 내에서 DB 문화재 이름 매칭 시도
            HeritageEncyclopedia suggestedHeritage =
                    heritageEncyclopediaRepository.findSingleHeritageByNameInString(aiResponse);

            if (suggestedHeritage != null) {
                finalResponse = String.format(
                        "꺼비가 답변에서 언급한 문화재는 **%s**을(를) 말씀하시는 건가요?<br/>자세한 정보를 드릴 수 있습니다.",
                        suggestedHeritage.getName()
                );
                options.add(suggestedHeritage.getName());
            } else {
                finalResponse = aiResponse;
            }
        }

        return ChatResponseDto.builder()
                .response(finalResponse)
                .options(options)
                .build();
    }

    // --- 보조 메서드 ---
    private String buildHeritageResponse(HeritageEncyclopedia heritage) {
        return String.format(
                "**%s** (%s)<br/><br/>" +
                        "- 소재지: %s<br/>" +
                        "- 시대: %s<br/>" +
                        "- 더보기: [상세 페이지](/heritage/%d)",
                heritage.getName(),
                heritage.getHeritageCode(),
                heritage.getAddress(),
                heritage.getPeriod(),
                heritage.getId()
        );
    }

    private String callGroqApi(String message) {
        Map<String, Object> requestBody = Map.of(
                "model", model,
                "messages", List.of(
                        Map.of("role", "system", "content",
                                "너는 한국 문화재 안내 도우미 챗봇이야. 오직 문화재 관련 질문만 답변하고, 확실하지 않으면 '죄송합니다. 잘 모르겠습니다.'라고 답변해. 설명은 3줄 이하로."),
                        Map.of("role", "user", "content", message)
                ),
                "temperature", 0.6,
                "max_tokens", 150
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

            return aiResponse.isEmpty() ? "죄송합니다. 잘 모르겠습니다." : aiResponse;

        } catch (Exception e) {
            e.printStackTrace();
            return "죄송합니다. 서버 통신에 문제가 발생했습니다.";
        }
    }
}
