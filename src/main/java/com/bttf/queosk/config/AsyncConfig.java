package com.bttf.queosk.config;

import com.bttf.queosk.exception.AsyncUncaughtExceptionHandlerCustom;
import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.AsyncConfigurerSupport;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

@EnableAsync
@Configuration
public class AsyncConfig extends AsyncConfigurerSupport {

    public Executor getAsyncExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(2); // 디폴트로 실행 대기 중인 Thread 수
        executor.setMaxPoolSize(10); // 동시에 동작하는 최대 Thread 수
        executor.setQueueCapacity(20); // CorePool이 초과될때 Queue에 저장했다가 꺼내서 실행. (20개까지 저장)
        executor.setThreadNamePrefix("async-"); // Spring에서 생성하는 Thread 접두사
        executor.initialize();
        return executor;
    }

    public AsyncUncaughtExceptionHandler getAsyncUncaughtExceptionHandler() {
        return new AsyncUncaughtExceptionHandlerCustom();
    }

}
