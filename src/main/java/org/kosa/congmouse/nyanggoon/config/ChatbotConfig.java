package org.kosa.congmouse.nyanggoon.config;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ChatbotConfig {

    @Bean
    public ChatClient customChatClient(ChatClient.Builder builder){
        return builder
                .defaultSystem("너의 이름은 꺼비야." +
                        "너는 조선시대에서 왔어" +
                        "잘 모르겠는 질문을 받으면 조선시대에서 와서 모르겠다고 해" +
                        "조선시대의 말투를 써." +
                        "마크 다운 형식으로 답변해." +
                        "1000자 이내로 답변해.")
                .build();
    }
}
