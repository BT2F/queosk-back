package com.bttf.queosk.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;

import java.lang.reflect.Method;
import java.util.Arrays;

@Slf4j
public class AsyncUncaughtExceptionHandlerCustom implements AsyncUncaughtExceptionHandler {
    @Override
    public void handleUncaughtException(Throwable ex, Method method, Object... params) {
        log.error("Async Exception - " + ex.getMessage());
        log.error("Async Method - " + method.getName());
        Arrays.stream(params).forEach(param -> log.error("Parameter value - " + param));
        throw new RuntimeException(ex.getMessage());
    }
}
