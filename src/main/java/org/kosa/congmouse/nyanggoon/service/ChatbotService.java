package org.kosa.congmouse.nyanggoon.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j; // 로그 출력용
import org.kosa.congmouse.nyanggoon.dto.ChatResponseDto; // 챗봇 응답 DTO
import org.kosa.congmouse.nyanggoon.entity.HeritageEncyclopedia; // 문화재 엔티티
import org.kosa.congmouse.nyanggoon.repository.HeritageEncyclopediaRepository; // 문화재 Repository
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient; // 외부 API 호출용

import java.util.*;
import java.util.stream.Collectors;

@Service // Spring Bean으로 등록, 서비스 계층에서 비즈니스 로직 담당
@RequiredArgsConstructor // final 필드 생성자 자동 주입
@Slf4j // 로그 출력 가능
public class ChatbotService {

    // --- Groq/OpenAI API 호출용 WebClient 주입 ---
    @Qualifier("groqWebClient")
    private final WebClient groqWebClient;

    // --- 문화재 DB 조회용 Repository ---
    private final HeritageEncyclopediaRepository heritageEncyclopediaRepository;

    // --- AI 모델명(application.properties에서 groq.api.model 값 읽어오기) ---
//    @Value("${groq.api.model:openai/gpt-oss-20b}")
    private final String model="openai/gpt-oss-20b";

    /**
     * 챗봇 메시지 처리 메인 함수
     * 1) 로그인 확인
     * 2) 특정 지역(서울) 질문 처리
     * 3) DB 검색 (단어별 부분 문자열 검색)
     * 4) DB 검색 결과 처리
     * 5) AI 호출
     * 6) 최종 DTO 반환
     *
     * @param message 사용자 입력 메시지
     * @param userId  로그인한 사용자 ID (null이면 비로그인)
     * @return ChatResponseDto 응답 메시지 + 선택 옵션 + 문화재 ID
     */
    public ChatResponseDto sendMessage(String message, Long userId) {

        // --- 1. 로그인 여부 확인 ---
        if (userId == null) {
            return ChatResponseDto.builder()
                    .response("로그인 후 이용 가능합니다.")
                    .options(Collections.emptyList())
                    .heritageId(null)
                    .build();
        }

        String lowerMsg = message.toLowerCase(); // 소문자로 변환하여 대소문자 구분 제거
        String finalResponse; // 최종 반환 메시지
        List<String> options = new ArrayList<>(); // 선택 가능한 문화재 옵션
        Long heritageId = null; // 단일 문화재 ID

        // --- 2. 서울 관련 질의 처리 ---
        if (lowerMsg.contains("서울")) {
            // "서울"이 포함된 문화재 리스트 조회
            List<HeritageEncyclopedia> seoulList =
                    heritageEncyclopediaRepository.findByNameContainingIgnoreCase("서울");

            // "몇" 또는 "얼마" 관련 질문 → 문화재 개수 반환
            if (lowerMsg.contains("몇") || lowerMsg.contains("얼마")) {
                long count = seoulList.size();
                return ChatResponseDto.builder()
                        .response("서울에 등록된 문화재는 총 **" + count + "개**입니다.")
                        .options(Collections.emptyList())
                        .heritageId(null)
                        .build();
            }

            // "문화재", "리스트", "보여" 등 → 리스트 반환
            if (lowerMsg.contains("문화재") || lowerMsg.contains("리스트") || lowerMsg.contains("보여")) {
                if (seoulList.isEmpty()) {
                    return ChatResponseDto.builder()
                            .response("서울에 등록된 문화재 정보를 찾을 수 없습니다.")
                            .options(Collections.emptyList())
                            .heritageId(null)
                            .build();
                }

                // 상위 5개 문화재 이름 출력 (HTML 줄바꿈 포함)
                String listText = seoulList.stream()
                        .limit(5)
                        .map(h -> "• " + h.getName())
                        .collect(Collectors.joining("<br/>"));

                return ChatResponseDto.builder()
                        .response("서울에 있는 주요 문화재입니다:<br/><br/>")
                        .options(seoulList.stream().limit(5).map(HeritageEncyclopedia::getName).collect(Collectors.toList()))
                        .heritageId(null)
                        .build();
            }
        }

        // --- 3. DB 기반 검색: 단어별 부분 문자열 검색 ---
        String[] keywords = message.split("\\s+"); // 띄어쓰기 기준으로 키워드 분리
        Set<HeritageEncyclopedia> resultSet = new LinkedHashSet<>(); // 중복 제거

        for (String kw : keywords) {
            if (!kw.isEmpty()) {
                // 각 키워드를 포함하는 문화재 검색
                resultSet.addAll(heritageEncyclopediaRepository.findByNameContainingIgnoreCase(kw));
            }
        }
        List<HeritageEncyclopedia> heritageResults = new ArrayList<>(resultSet); // 검색 결과 리스트

        HeritageEncyclopedia suggestedHeritage = null; // AI 추천 문화재 초기화

        // --- 4. 검색 결과 처리 ---
        if (heritageResults.size() == 1) {
            // 단일 문화재 검색됨
            HeritageEncyclopedia heritage = heritageResults.get(0);

            heritageId = heritage.getId();
            String shortContent = heritage.getContent();
            if (shortContent.length() > 100) shortContent = shortContent.substring(0, 100) + "..."; // 100자 이후 생략

            finalResponse = String.format(
                    "**%s** (%s)\n\n- 소재지: %s\n- 시대: %s\n- 설명: %s\n- 더보기: [상세 페이지](/heritages/detail/%d)",
                    heritage.getName(),
                    heritage.getHeritageCode(),
                    heritage.getAddress(),
                    heritage.getPeriod(),
                    shortContent,
                    heritage.getId()
            );

        } else if (heritageResults.size() > 1) {
            // 여러 개 검색됨 → 선택 옵션 제공
            finalResponse = "검색 결과가 여러 개입니다. 어떤 문화재를 알고 싶으신가요?";
            options = heritageResults.stream()
                    .limit(5)
                    .map(HeritageEncyclopedia::getName)
                    .collect(Collectors.toList());
        } else {
            // --- 5. DB 검색 결과 없으면 AI 호출 ---
            String aiResponse = callGroqApi(message);

            // AI 응답에서 DB에 존재하는 문화재 이름 추출
            suggestedHeritage = heritageEncyclopediaRepository.findSingleHeritageByNameInString(aiResponse);

            if (suggestedHeritage != null) {
                heritageId = suggestedHeritage.getId();
                String shortContent = suggestedHeritage.getContent();
                if (shortContent.length() > 100) shortContent = shortContent.substring(0, 100) + "...";

                finalResponse = String.format(
                        "꺼비가 답변에서 언급한 문화재는 **%s**을(를) 말씀하시는 건가요?\n\n- 소재지: %s\n- 시대: %s\n- 설명: %s\n- 더보기: [상세 페이지](/heritages/detail/%d)",
                        suggestedHeritage.getName(),
                        suggestedHeritage.getAddress(),
                        suggestedHeritage.getPeriod(),
                        shortContent,
                        suggestedHeritage.getId()
                );
                options.add(suggestedHeritage.getName());
            } else {
                finalResponse = aiResponse; // AI 답변 그대로 반환
            }
        }

        // --- 6. 최종 DTO 반환 ---
        return ChatResponseDto.builder()
                .response(finalResponse)
                .options(options)
                .heritageId(heritageId)
                .build();
    }

    /**
     * 외부 AI API 호출 (Groq/OpenAI)
     * 1) requestBody 생성 (모델, 메시지, temperature, max_tokens)
     * 2) WebClient POST 호출
     * 3) 에러 처리
     * 4) AI 응답에서 메시지 추출 후 반환
     *
     * @param message 사용자 질문
     * @return AI 응답 문자열
     */
    private String callGroqApi(String message) {
        // --- 1. 요청 본문 생성 ---
        Map<String, Object> requestBody = Map.of(
                "model", model,
                "messages", List.of(
                        Map.of("role", "system", "content",
                                "너는 한국 문화재 안내 도우미 챗봇이야. 오직 문화재 관련 질문만 답변하고, 확실하지 않으면 '죄송합니다. 잘 모르겠습니다.'라고 답변해."),
                        Map.of("role", "user", "content", message)
                ),
                "temperature", 0.6, // 답변 다양성 조절
                "max_tokens", 400   // 최대 토큰 수
        );

        try {
            // --- 2. WebClient를 통한 POST 호출 ---
            Map<String, Object> response = groqWebClient.post()
                    .uri("/chat/completions") // Groq API 엔드포인트
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(requestBody)
                    .retrieve()
                    .onStatus(
                            status -> status.isError(),
                            clientResponse -> clientResponse.bodyToMono(String.class).map(body -> {
                                log.error("Groq API Error: HTTP {} - {}", clientResponse.statusCode(), body);
                                return new RuntimeException("Groq API 호출 실패");
                            })
                    )
                    .bodyToMono(Map.class) // 응답 JSON을 Map으로 변환
                    .block(); // 동기 방식 호출

            log.info("Request to Groq API: {}", requestBody);

            if (response == null || !response.containsKey("choices")) {
                return "죄송합니다. 잘 모르겠습니다.";
            }

            // --- 3. AI 응답에서 메시지 추출 ---
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
