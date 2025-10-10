package org.kosa.congmouse.nyanggoon.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ChatRequestDto {
    private String message;  // 사용자가 보낸 질문
}