package com.my.agent.common;

import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(IllegalArgumentException.class)
    public Map<String, Object> handleIllegalArgument(IllegalArgumentException e) {
        return Map.of(
                "code", -1,
                "msg", e.getMessage()
        );
    }

    @ExceptionHandler(IllegalStateException.class)
    public Map<String, Object> handleIllegalState(IllegalStateException e) {
        return Map.of(
                "code", -1,
                "msg", e.getMessage()
        );
    }

    @ExceptionHandler(Exception.class)
    public Map<String, Object> handleException(Exception e) {
        return Map.of(
                "code", -1,
                "msg", e.getMessage()
        );
    }
}