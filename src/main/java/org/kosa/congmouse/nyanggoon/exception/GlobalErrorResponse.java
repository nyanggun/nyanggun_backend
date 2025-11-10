package org.kosa.congmouse.nyanggoon.exception;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "공통 에러 응답 구조")
public class GlobalErrorResponse {
    // 오류 발생 시간
    @Schema(description = "에러 발생 시각")
    private LocalDateTime timestamp;
    // HTTP Response 상태 코드
    @Schema(description = "HTTP 상태 코드")
    private int status;
    // 에러 코드
    @Schema(description = "에러 코드")
    private String code;
    // 사용자에게 보여줄 메세지
    @Schema(description = "에러 메세지")
    private String message;
}
