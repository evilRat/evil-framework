package com.evil.framework.web.autoconfigure;

import com.ctrip.framework.apollo.model.ConfigChange;
import com.ctrip.framework.apollo.model.ConfigChangeEvent;
import com.ctrip.framework.apollo.spring.annotation.ApolloConfigChangeListener;
import com.evil.framework.web.logger.LoggerFilter;
import com.evil.framework.web.mapping.MyRequestMappingHandlerMethodMapping;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.web.servlet.WebMvcRegistrations;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.logging.LogLevel;
import org.springframework.boot.logging.LoggingSystem;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.cloud.context.environment.EnvironmentChangeEvent;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

/**
 * Web自动装配类
 *
 * @author kongzheng
 * @since 1.0.0.RELEASE
 */
@Slf4j
@EnableConfigurationProperties(value = {WebProperties.class})
@Configuration
@ComponentScan("com.evil.framework.web")
public class WebAutoConfiguration implements ApplicationContextAware {

    /**
     * 日志配置常量
     */
    private static final String LOGGER_TAG = "logging.level.";

    private ApplicationContext applicationContext;

    @Autowired
    private LoggingSystem loggingSystem;

    private final WebProperties properties;

    public WebAutoConfiguration(WebProperties properties) {
        this.properties = properties;
    }

    @ConditionalOnProperty(prefix = "framework.web", value = "myRequestMapping.enable")
    @Bean
    public WebMvcRegistrations webMvcRegistrations(Environment environment) {
        return new WebMvcRegistrations() {
            @Override
            public RequestMappingHandlerMapping getRequestMappingHandlerMapping() {
                String applicationPath = StringUtils.isNotBlank(properties.getRequestUrlPrefix())
                        ? properties.getRequestUrlPrefix() : "/" + environment.getProperty("spring.application.name", "");
                return new MyRequestMappingHandlerMethodMapping(applicationPath);
            }
        };
    }

    @ConditionalOnMissingBean(LoggerFilter.class)
    @Bean
    public FilterRegistrationBean<LoggerFilter> lcpLoggerFilter(ObjectMapper objectMapper) {
        LoggerFilter loggerFilter = new LoggerFilter(objectMapper, properties);
        FilterRegistrationBean<LoggerFilter> registrationBean = new FilterRegistrationBean<>();
        registrationBean.setFilter(loggerFilter);
        registrationBean.addUrlPatterns("/*");
        registrationBean.setName("lcpLoggerFilter");
        return registrationBean;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    @ApolloConfigChangeListener
    private void onChange(ConfigChangeEvent changeEvent) {
        log.info("【Apollo-config-change】>> start");
        for (String key : changeEvent.changedKeys()) {
            ConfigChange change = changeEvent.getChange(key);
            log.info("【Apollo-config-change】>> key={} , propertyName={} , oldValue={} , newValue={} ",
                    key, change.getPropertyName(), change.getOldValue(), change.getNewValue());
            //是否为日志配置
            if (StringUtils.containsIgnoreCase(key, LOGGER_TAG)) {
                //日志配置刷新
                changeLoggingLevel(key, change);
                continue;
            }
            // 更新相应的bean的属性值，主要是存在@ConfigurationProperties注解的bean
            this.applicationContext.publishEvent(new EnvironmentChangeEvent(changeEvent.changedKeys()));
        }
        log.info("【Apollo-config-change】>> end");
    }

    /**
     * 刷新日志级别
     */
    private void changeLoggingLevel(String key, ConfigChange change) {
        if (null == loggingSystem) {
            return;
        }
        String newLevel = change.getNewValue();
        LogLevel level = LogLevel.valueOf(newLevel.toUpperCase());
        loggingSystem.setLogLevel(key.replace(LOGGER_TAG, ""), level);
        log.info("【Apollo-logger-config-change】>> {} -> {}", key, newLevel);
    }
}
