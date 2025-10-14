//package org.kosa.congmouse.nyanggoon.controller;
//
//import lombok.RequiredArgsConstructor;
//import org.kosa.congmouse.nyanggoon.service.ChatbotService;
//import org.springframework.web.bind.annotation.*;
//
//import java.util.Map;
//
//@CrossOrigin(origins = "http://localhost:5173")
//@RestController
//@RequestMapping("/api/chat")
//@RequiredArgsConstructor
//public class ChatbotController {
//    private final ChatbotService chatbotService;
//
//    @PostMapping
//    public Map<String, String> chat(@RequestBody Map<String, String> body) {
//        String message = body.get("message");
//        String response = chatbotService.getChatResponse(message);
//        return Map.of("response", response);
//    }
//}
