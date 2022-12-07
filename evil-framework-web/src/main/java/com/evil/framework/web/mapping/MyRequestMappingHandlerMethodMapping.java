package com.evil.framework.web.mapping;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.lang.Nullable;
import org.springframework.util.StringUtils;
import org.springframework.util.StringValueResolver;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.condition.RequestCondition;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Method;

/**
 * 自定义{@link RequestMappingHandlerMapping}
 *
 * @author kongzheng
 * @since 2021/11/24 11:16 上午
 */
@Slf4j
public class MyRequestMappingHandlerMethodMapping extends RequestMappingHandlerMapping {

    private RequestMappingInfo.BuilderConfiguration config = new RequestMappingInfo.BuilderConfiguration();

    private final String applicationPath;

    @Nullable
    private StringValueResolver valueResolver;

    public MyRequestMappingHandlerMethodMapping(String applicationPath) {
        this.applicationPath = applicationPath;
    }

    @Override
    public boolean isHandler(Class<?> beanType) {
        // 排除 Feign 注解
        return super.isHandler(beanType)
                && !AnnotatedElementUtils.hasAnnotation(beanType, FeignClient.class);
    }

    @Override
    public RequestMappingInfo getMappingForMethod(Method method, Class<?> handlerType) {
        RequestMappingInfo info = createRequestMappingInfo(method, false);
        if (info != null) {
            RequestMappingInfo typeInfo = createRequestMappingInfo(handlerType, true);
            if (typeInfo != null) {
                info = typeInfo.combine(info);
            }
        }
        return info;
    }

    @Override
    public void setEmbeddedValueResolver(StringValueResolver resolver) {
        this.valueResolver = resolver;
        super.setEmbeddedValueResolver(resolver);
    }

    @Override
    public void afterPropertiesSet() {
        this.config = new RequestMappingInfo.BuilderConfiguration();
        this.config.setUrlPathHelper(getUrlPathHelper());
        this.config.setPathMatcher(getPathMatcher());
        this.config.setSuffixPatternMatch(useSuffixPatternMatch());
        this.config.setTrailingSlashMatch(useTrailingSlashMatch());
        this.config.setRegisteredSuffixPatternMatch(useRegisteredSuffixPatternMatch());
        this.config.setContentNegotiationManager(getContentNegotiationManager());
        super.afterPropertiesSet();
    }

    private RequestMappingInfo createRequestMappingInfo(AnnotatedElement element, boolean classRequestMapping) {
        RequestMapping requestMapping = AnnotatedElementUtils.findMergedAnnotation(element, RequestMapping.class);
        RequestCondition<?> condition = (element instanceof Class ?
                getCustomTypeCondition((Class<?>) element) : getCustomMethodCondition((Method) element));
        return (requestMapping != null ? createRequestMappingInfo(requestMapping, condition, classRequestMapping) : null);
    }

    private RequestMappingInfo createRequestMappingInfo(
            RequestMapping requestMapping, @Nullable RequestCondition<?> customCondition, boolean classRequestMapping) {

        RequestMappingInfo.Builder builder = RequestMappingInfo
                .paths(resolveEmbeddedValuesInPatterns(classRequestMapping, requestMapping.path()))
                .methods(requestMapping.method())
                .params(requestMapping.params())
                .headers(requestMapping.headers())
                .consumes(requestMapping.consumes())
                .produces(requestMapping.produces())
                .mappingName(requestMapping.name());
        if (customCondition != null) {
            builder.customCondition(customCondition);
        }
        return builder.options(this.config).build();
    }

    private String[] resolveEmbeddedValuesInPatterns(boolean classRequestMapping, String[] patterns) {
        if (this.valueResolver == null) {
            return patterns;
        } else {
            String[] resolvedPatterns = new String[patterns.length];
            for (int i = 0; i < patterns.length; i++) {
                String resolvedPattern = this.valueResolver.resolveStringValue(patterns[i]);
                if (classRequestMapping && !StringUtils.startsWithIgnoreCase(resolvedPattern, "/swagger")
                        && !StringUtils.startsWithIgnoreCase(resolvedPattern, "/dayu")) {
                    resolvedPatterns[i] = applicationPath + resolvedPattern;
                } else {
                    resolvedPatterns[i] = resolvedPattern;
                }
            }
            return resolvedPatterns;
        }
    }

}
