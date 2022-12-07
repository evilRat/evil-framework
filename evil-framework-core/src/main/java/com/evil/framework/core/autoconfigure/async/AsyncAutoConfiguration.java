package com.evil.framework.core.autoconfigure.async;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.event.SimpleApplicationEventMulticaster;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.Optional;
import java.util.concurrent.Executor;

import static org.springframework.context.support.AbstractApplicationContext.APPLICATION_EVENT_MULTICASTER_BEAN_NAME;

/**
 * 异步配置类
 *
 * @author  kongzheng
 * @since 1.0.0.RELEASE
 */
@EnableAsync
@EnableConfigurationProperties(AsyncProperties.class)
@Configuration
@ConditionalOnProperty(value = AsyncAutoConfiguration.DEFAULT_PREFIX + "enable")
public class AsyncAutoConfiguration implements BeanFactoryAware {

    public static final String DEFAULT_PREFIX = "framework.async.";

    private BeanFactory beanFactory;

    private final AsyncProperties asyncProperties;

    public AsyncAutoConfiguration(AsyncProperties asyncProperties) {
        this.asyncProperties = asyncProperties;
    }

    /**
     * 配置异步支持{@link org.springframework.scheduling.annotation.Async}
     */
    @Primary
    @Bean("taskExecutor")
    public Executor taskAsyncPool() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        Optional.ofNullable(asyncProperties.getCorePoolSize()).ifPresent(executor::setCorePoolSize);
        Optional.ofNullable(asyncProperties.getMaxPoolSize()).ifPresent(executor::setMaxPoolSize);
        Optional.ofNullable(asyncProperties.getQueueCapacity()).ifPresent(executor::setQueueCapacity);
        Optional.ofNullable(asyncProperties.getKeepAliveSeconds()).ifPresent(executor::setKeepAliveSeconds);
        Optional.ofNullable(asyncProperties.getThreadNamePrefix()).ifPresent(executor::setThreadNamePrefix);
        executor.initialize();
        return executor;
    }

    /**
     * 配置异步发送事件，默认是同步
     */
    @Bean(APPLICATION_EVENT_MULTICASTER_BEAN_NAME)
    @ConditionalOnProperty(value = DEFAULT_PREFIX + "event-publish.enable",matchIfMissing = true)
    public SimpleApplicationEventMulticaster eventMulticaster() {
        SimpleApplicationEventMulticaster eventMulticaster = new SimpleApplicationEventMulticaster(beanFactory);
        eventMulticaster.setTaskExecutor(taskAsyncPool());
        return eventMulticaster;
    }

    @Override
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        this.beanFactory = beanFactory;
    }


}
