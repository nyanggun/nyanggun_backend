package org.kosa.congmouse.nyanggoon.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.kosa.congmouse.nyanggoon.dto.ChatResponseDto;
import org.kosa.congmouse.nyanggoon.security.user.CustomMemberDetails;
import org.kosa.congmouse.nyanggoon.service.ChatbotService;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/chat")
@RequiredArgsConstructor
public class ChatbotController {

    // ChatbotService 의존성 주입 (생성자 주입)
    // @RequiredArgsConstructor 때문에 final 필드를 자동으로 생성자에 주입해줌
    private final ChatbotService chatbotService;

    private final ChatClient chatClient;
    @PostMapping("/messages")
    public ChatResponseDto PostMessage(
            @RequestBody String message,
            @AuthenticationPrincipal CustomMemberDetails customMemberDetails) {
        return chatbotService.getResult(message, customMemberDetails);
    }
}
