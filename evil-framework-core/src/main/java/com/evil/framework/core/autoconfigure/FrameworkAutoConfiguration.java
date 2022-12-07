package com.evil.framework.core.autoconfigure;

import com.evil.framework.core.autoconfigure.alarm.AlarmAutoConfiguration;
import com.evil.framework.core.autoconfigure.alarm.AlarmProperties;
import com.evil.framework.core.autoconfigure.async.AsyncAutoConfiguration;
import com.evil.framework.core.autoconfigure.async.AsyncProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.validation.annotation.Validated;

/**
 * Framework core 自动装配类
 *
 * @author  kongzheng
 * @since 1.0.0.RELEASE
 */
@Validated
@Configuration
@Import({AsyncAutoConfiguration.class, AlarmAutoConfiguration.class})
@EnableConfigurationProperties(value = {AsyncProperties.class, AlarmProperties.class})
public class FrameworkAutoConfiguration {

}
