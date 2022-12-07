package com.evil.framework.core.autoconfigure.alarm;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 告警配置类
 *
 * @author  kongzheng
 * @since 1.0.0.RELEASE
 */
@Data
@ConfigurationProperties(prefix = "framework.alarm")
public class AlarmProperties {

    /**
     * 告警url
     */
    private String url;

}
