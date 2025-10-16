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

    private final ChatbotService chatbotService;

    /**
     * 메시지 전송 (DB 저장 없이 매번 새 대화)
     */
    @PostMapping("/messages")
    public ChatResponseDto sendMessage(
            @RequestBody String message,
            @AuthenticationPrincipal CustomMemberDetails customMemberDetails) {

        // 로그인 체크
        if (customMemberDetails == null || customMemberDetails.getMember() == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "로그인 후 이용해주세요.");
        }

        if (message == null || message.trim().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "message는 필수입니다.");
        }

        Long userId = customMemberDetails.getMember().getId(); // 회원 ID 가져오기
        log.info("사용자({}) 메시지: {}", customMemberDetails.getUsername(), message.trim());

        // 메시지 전송 (userId 전달)
        return chatbotService.sendMessage(message.trim(), userId);
    }
}
