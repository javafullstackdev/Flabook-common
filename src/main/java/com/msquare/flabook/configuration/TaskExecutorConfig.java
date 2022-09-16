package com.msquare.flabook.configuration;

import com.msquare.flabook.common.controllers.CommonResponseCode;
import com.msquare.flabook.exception.CommonException;
import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

/*
 * Spring Async annotation를 이용한 비동기 처리
 * @async 를 선언한 메소드를 호출한 호출자는 즉시 리턴
 * 실제 실행은 Spring TaskExecutor에 의해 실행.*/
@Configuration
@EnableAsync
public class TaskExecutorConfig implements AsyncConfigurer {
	private static final int TASK_CORE_POOL_SIZE = 2;
	private static final int TASK_MAX_POOL_SIZE = 4;
	private static final int TASK_QUEUE_CAPACITY = 10;
	private static final String BEAN_NAME = "executorSample";

	@Bean(name = "executorSample")
	@Override
	public Executor getAsyncExecutor() {
		ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
		executor.setCorePoolSize(TASK_CORE_POOL_SIZE);
		executor.setMaxPoolSize(TASK_MAX_POOL_SIZE);
		executor.setQueueCapacity(TASK_QUEUE_CAPACITY);
		executor.setBeanName(BEAN_NAME);
		executor.initialize();
		return executor;
	}

	@Override
	public AsyncUncaughtExceptionHandler getAsyncUncaughtExceptionHandler() {
		return (ex, method, params) -> {
			throw new CommonException(CommonResponseCode.FAIL, ex.getMessage());
		};
	}
}
