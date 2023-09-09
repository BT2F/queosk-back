package com.bttf.queosk.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.ArrayList;
import java.util.List;

import static com.bttf.queosk.exception.ErrorCode.UNDEFINED_EXCEPTION;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<List<String>> handleValidException(
            MethodArgumentNotValidException exception) {

        List<String> errors = new ArrayList<>();
        exception.getBindingResult().getAllErrors()
                .forEach(error -> errors.add(error.getDefaultMessage()));

        return ResponseEntity.status(400).body(errors);
    }

    @ExceptionHandler(CustomException.class)
    public ResponseEntity<ErrorResponse> handleCustomException(CustomException exception) {
        ErrorResponse errorResponse = ErrorResponse.builder()
                .errorCode(exception.getErrorCode())
                .message(exception.getMessage())
                .build();

        log.error(exception.getMessage(), exception);

        return ResponseEntity
                .status(errorResponse.getErrorCode().getStatus())
                .body(errorResponse);
    }

    //별도로 정의되지 않았으나 발생한 예외 처리
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> Exception(Exception exception) {
        ErrorResponse errorResponse = ErrorResponse.builder()
                .message(exception.getMessage())
                .errorCode(UNDEFINED_EXCEPTION)
                .build();

        log.error(exception.getMessage(), exception);

        return ResponseEntity
                .status(errorResponse.getErrorCode().getStatus())
                .body(errorResponse);
    }
}
