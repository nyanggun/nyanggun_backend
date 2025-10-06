package org.kosa.congmouse.nyanggoon.dto;


import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
/**
 ApiResponseDto 입니다.
 */
@Getter
@Builder
public class ApiResponseDto<T> {
    private final boolean success;
    private final String message;
    private final T data;
    private final String code;
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
