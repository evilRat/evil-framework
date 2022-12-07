package com.evil.framework.core.autoconfigure.alarm;

import com.evil.framework.core.alarm.ApplicationAlarmEventListener;
import com.evil.framework.core.alarm.ApplicationAlarmHandler;
import com.evil.framework.core.alarm.DefaultAlarmHandler;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import java.util.List;

/**
 * 告警自动装配类
 *
 * @author  kongzheng
 * @since 1.0.0.RELEASE
 */
@EnableConfigurationProperties(AlarmProperties.class)
@Configuration
public class AlarmAutoConfiguration {

    @Bean
    public ApplicationAlarmEventListener alarmEventListener(List<ApplicationAlarmHandler> exceptionHandlers) {
        return new ApplicationAlarmEventListener(exceptionHandlers);
    }

    @Bean
    public ApplicationAlarmHandler defaultExceptionHandler(Environment environment,
                                                           AlarmProperties alarmProperties) {
        return new DefaultAlarmHandler(environment, alarmProperties);
    }

}
