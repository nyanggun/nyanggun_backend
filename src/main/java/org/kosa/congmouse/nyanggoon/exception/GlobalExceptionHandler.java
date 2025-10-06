package org.kosa.congmouse.nyanggoon.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.LocalDateTime;

@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {

    // 404 not found
    // 사용자가 요청한 리소스를 서버에서 찾을 수 없음
    @ExceptionHandler
    public ResponseEntity<GlobalErrorResponse> handleRuntimeException(RuntimeException e){
        log.error("사용자가 요청한 리소스를 서버에서 찾을 수 없음{}", e.getMessage(), e);
        GlobalErrorResponse error = GlobalErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(404)
                .code("NOT_FOUND")
                .message(e.getMessage())
                .build();
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    /**
     * 그외 예상하지 못한 시스템 오류
     * -500 Internal Server Error 응답
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<GlobalErrorResponse> handleException(Exception e){
        log.error("예상하지 못한 오류 발생{}", e.getMessage(), e);
        GlobalErrorResponse error = GlobalErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(500)
                .message("서버 내부 오류가 발생했습니다")
                .build();
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }
}
