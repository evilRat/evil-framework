package com.evil.framework.core.autoconfigure.async;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * 异步支持配置类
 *
 * @author  kongzheng
 * @since 1.0.0.RELEASE
 */
@Data
@ConfigurationProperties(prefix = "framework.async")
public class AsyncProperties {

    /**
     * 任务线程池核心大小
     */
    private Integer corePoolSize = 5;

    /**
     * 任务线程池最大大小
     */
    private Integer maxPoolSize = 10;

    /**
     * 任务存活时间
     */
    private Integer keepAliveSeconds = 60;

    /**
     * 任务队列大小
     */
    private Integer queueCapacity = 2147483647;

    /**
     * 线程前缀
     */
    private String threadNamePrefix;

}
