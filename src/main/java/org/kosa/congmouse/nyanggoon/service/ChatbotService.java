package org.kosa.congmouse.nyanggoon.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j; // 로그 출력용
import org.kosa.congmouse.nyanggoon.dto.ChatResponseDto; // 챗봇 응답 DTO
import org.kosa.congmouse.nyanggoon.entity.HeritageEncyclopedia; // 문화재 엔티티
import org.kosa.congmouse.nyanggoon.repository.HeritageEncyclopediaRepository; // 문화재 Repository
import org.kosa.congmouse.nyanggoon.security.user.CustomMemberDetails;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient; // 외부 API 호출용
import org.springframework.web.server.ResponseStatusException;

import java.util.*;
import java.util.stream.Collectors;

@Service // Spring Bean으로 등록, 서비스 계층에서 비즈니스 로직 담당
@RequiredArgsConstructor // final 필드 생성자 자동 주입
@Slf4j // 로그 출력 가능
public class ChatbotService {
    private final ChatClient chatClient;

    public ChatResponseDto getResult(String message, CustomMemberDetails customMemberDetails){

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

        return ChatResponseDto
                .builder()
                .response(this.chatClient.prompt().user(message).call().content())
                .build();
    }
}
