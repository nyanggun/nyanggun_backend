package org.kosa.congmouse.nyanggoon.dto;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
/**
 ApiResponseDto 입니다.
 */
@Getter
@Builder
@Schema(description = "공통 API 응답 DTO")
public class ApiResponseDto<T> {

    @Schema(description = "요청 성공 여부")
    private final boolean success;
    @Schema(description = "요청 성공 메세지")
    private final String message;
    @Schema(description = "응답 데이터(성공 시만 존재)", example = "게시글 조회가 성공했습니다.")
    private final T data;
    @Schema(description = "응답 코드(HTTP 상태 코드와 별개)", example = "200")
    private final String code;
    @Schema(description = "성공/실패 발생 시간")
    private final LocalDateTime timestamp;

    public static <T> ApiResponseDto<T> success(T data, String message) {
        return ApiResponseDto.<T>builder()
                .success(true)
                .message(message)
                .data(data)
                .timestamp(LocalDateTime.now())
                .build();
    }

    public static <T> ApiResponseDto<T> error(String code, String message) {
        return ApiResponseDto.<T>builder()
                .success(false)
                .code(code)
                .message(message)
                .timestamp(LocalDateTime.now())
                .build();
    }
}
