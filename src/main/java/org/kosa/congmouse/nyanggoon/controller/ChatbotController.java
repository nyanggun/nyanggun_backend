package org.kosa.congmouse.nyanggoon.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.kosa.congmouse.nyanggoon.dto.ChatResponseDto;
import org.kosa.congmouse.nyanggoon.security.user.CustomMemberDetails;
import org.kosa.congmouse.nyanggoon.service.ChatbotService;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@Slf4j
@CrossOrigin(origins = "http://localhost:5173")
@RestController
@RequestMapping("/api/chat")
@RequiredArgsConstructor
public class ChatbotController {

    // ChatbotService 의존성 주입 (생성자 주입)
    // @RequiredArgsConstructor 때문에 final 필드를 자동으로 생성자에 주입해줌
    private final ChatbotService chatbotService;

    /**
     * 메시지 전송 (DB 저장 없이 매번 새 대화)
     *
     * - 엔드포인트: POST /api/chat/messages
     * - 요청 바디: 단순 문자열(message) — 이 컨트롤러는 @RequestBody로 raw String을 받음
     * - 인증: Spring Security에서 인증된 사용자 정보를 @AuthenticationPrincipal로 주입 받음
     * - 반환: ChatResponseDto (서비스에서 생성된 챗봇 응답 DTO)
     *
     * 유효성 검사:
     *  - 인증정보가 없으면 401 UNAUTHORIZED 반환
     *  - message가 비어있거나 null이면 400 BAD_REQUEST 반환
     *
     * 예외 처리 방식:
     *  - 잘못된 요청/인증은 ResponseStatusException으로 처리 -> Spring이 적절한 HTTP 상태로 응답함
     */
    @PostMapping("/messages")
    public ChatResponseDto sendMessage(
            @RequestBody String message, // 요청 본문(예: "안녕 챗봇!") — raw text로 받음
            @AuthenticationPrincipal CustomMemberDetails customMemberDetails) { // 인증된 사용자 정보 주입

        // 1) 로그인(인증) 체크
        // customMemberDetails는 Spring Security가 인증된 Principal을 바인딩해서 넣어준 객체
        // 인증이 되지 않았거나 내부 member 정보가 없으면(세션/토큰 만료 등) 접근 금지.
        if (customMemberDetails == null || customMemberDetails.getMember() == null) {
            // ResponseStatusException을 던지면 컨트롤러 레벨에서 HTTP 401로 응답됩니다.
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "로그인 후 이용해주세요.");
        }

        // 2) 요청 데이터 검증
        // message가 null이거나 공백 문자열이면 400 Bad Request
        if (message == null || message.trim().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "message는 필수입니다.");
        }

        // 3) 사용자 정보와 메시지 로깅
        // 로그에 남겨서 디버깅/추적에 도움되게 함 (로그레벨은 info)
        Long userId = customMemberDetails.getMember().getId(); // 회원 ID 가져오기
        log.info("사용자({}) 메시지: {}", customMemberDetails.getUsername(), message.trim());

        // 4) 서비스 호출
        // 실제 챗봇 처리(예: 외부 API 호출, 모델 호출 등)는 Service 계층에서 담당
        // 사용자 ID를 함께 전달하여 유저별 컨텍스트/요금/사용량 트래킹에 활용할 수 있음
        return chatbotService.sendMessage(message.trim(), userId);
    }
}
