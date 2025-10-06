package org.kosa.congmouse.nyanggoon.exception;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GlobalErrorResponse {
    // 오류 발생 시간
    private LocalDateTime timestamp;
    // HTTP Response 상태 코드
    private int status;
    // 에러 코드
    private String code;
    // 사용자에게 보여줄 메세지
    private String message;
}
