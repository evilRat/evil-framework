package com.evil.framework.web.logger;

import com.alibaba.fastjson.JSON;
import com.evil.framework.web.autoconfigure.WebProperties;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Maps;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.NamedInheritableThreadLocal;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.CollectionUtils;
import org.springframework.util.PathMatcher;
import org.springframework.util.StopWatch;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;
import org.springframework.web.util.UrlPathHelper;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Record access log
 *
 * @author kongzheng
 * @since 1.0.0.RELEASE
 */
public class LoggerFilter extends OncePerRequestFilter {

    private static final Logger LOGGER = LoggerFactory.getLogger(LoggerFilter.class);

    private static final List<String> DEFAULT_EXCLUDE_PATTERNS = Arrays.asList("/actuator/**", "/swagger-resources/**",
            "/v2/api-docs", "/info", "/dayu/**", "/webjars/**", "/swagger-ui.html", "/swagger-ui.html/**");

    private static final NamedInheritableThreadLocal<Long> REQUEST_TIME = new NamedInheritableThreadLocal<Long>("Request Time") {
        @Override
        protected Long initialValue() {
            return 0L;
        }
    };

    private static final NamedInheritableThreadLocal<Map<String, String>> EXTRA_PARAM = new NamedInheritableThreadLocal<Map<String, String>>("Extra Param") {
        @Override
        protected Map<String, String> initialValue() {
            return new ConcurrentHashMap<>();
        }
    };

    private final ObjectMapper objectMapper;

    private final UrlPathHelper urlPathHelper = new UrlPathHelper();

    private final PathMatcher pathMatcher = new AntPathMatcher();

    private final WebProperties properties;

    public LoggerFilter(ObjectMapper objectMapper, WebProperties properties) {
        this.objectMapper = objectMapper;
        this.properties = properties;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        boolean skip = false;
        //过滤不打印日志的 url
        String uri = this.urlPathHelper.getPathWithinApplication(request);
        try {
            //过滤不打印日志的 url
            List<String> excludePatterns = new ArrayList<>();
            properties.getFilter().getExcludePatterns().stream()
                    .filter(Objects::nonNull)
                    .forEach(excludePatterns::add);
            excludePatterns.addAll(DEFAULT_EXCLUDE_PATTERNS);
            for (String excludePattern : excludePatterns) {
                if (pathMatcher.match(excludePattern, uri)) {
                    skip = true;
                }
            }
        } catch (Exception e) {
            logger.error("parse path error,skip log", e);
            skip = true;
        }
        if (skip) {
            StopWatch stopwatch = new StopWatch("requestTime");
            stopwatch.start();
            filterChain.doFilter(request, response);
            stopwatch.stop();
            long totalTimeMillis = stopwatch.getTotalTimeMillis();
            if (totalTimeMillis > 200L) {
                LOGGER.warn("url:{} invoke cost:{} ms", uri, totalTimeMillis);
            }
        } else {
            REQUEST_TIME.set(System.currentTimeMillis());
            //包装器
            ContentCachingRequestWrapper requestWrapper = new ContentCachingRequestWrapper(request);
            ContentCachingResponseWrapper responseWrapper = new ContentCachingResponseWrapper(response);
            try {
                filterChain.doFilter(requestWrapper, responseWrapper);
            } finally {
                try {
                    Map<String, String> requestMap = Maps.newHashMap();
                    Map<String, String> extraParamMap = Maps.newConcurrentMap();

                    /*
                     * request 日志处理
                     */
                    Date requestTime = new Date(REQUEST_TIME.get());
                    String bodyParam = new String(requestWrapper.getContentAsByteArray(), requestWrapper.getCharacterEncoding());
                    extraParamMap.putAll(getAllAndRemove());
                    if (!CollectionUtils.isEmpty(requestWrapper.getParameterMap())) {
                        extraParamMap.put(LoggerConst.REQUEST_KEY_FORM_PARAM, objectMapper.writeValueAsString(request.getParameterMap()));
                    }
                    Enumeration<String> entries = request.getHeaderNames();
                    while (entries.hasMoreElements()) {
                        String headerName = entries.nextElement();
                        for (String extraHeader : properties.getFilter().getExtraHeaders()) {
                            if (StringUtils.equalsIgnoreCase(extraHeader, headerName)) {
                                extraParamMap.put(headerName, requestWrapper.getHeader(headerName));
                                break;
                            }
                        }
                        requestMap.put(headerName.toLowerCase(), requestWrapper.getHeader(headerName));
                    }

                    /*
                     * 日志处理
                     */
                    requestMap.put(LoggerConst.REQUEST_KEY_REQUEST_TIME, LoggerConst.DATE_FORMAT.format(requestTime));
                    requestMap.put(LoggerConst.REQUEST_KEY_URL, request.getRequestURL().toString());
                    requestMap.put(LoggerConst.REQUEST_KEY_HTTP_METHOD, request.getMethod());
                    requestMap.put(LoggerConst.REQUEST_KEY_BODY_PARAM, StringUtils.isNotBlank(bodyParam) ? org.springframework.util.StringUtils.trimAllWhitespace(bodyParam) : LoggerConst.VALUE_DEFAULT);
                    requestMap.put(LoggerConst.REQUEST_KEY_EXTRA_PARAM, objectMapper.writeValueAsString(extraParamMap));

                    if (LOGGER.isInfoEnabled()) {
                        String requestJson = JSON.toJSONString(requestMap);
                        LOGGER.info(LoggerConst.REQUEST_IDENTITY + LoggerConst.REQUEST_PREFIX + requestJson);
                    }

                    /*
                     * response 日志处理
                     */
                    if (LOGGER.isInfoEnabled()) {
                        Map<String, String> responseMap = Maps.newHashMap();
                        Date responseDate = new Date();
                        responseMap.put(LoggerConst.RESPONSE_KEY_RESPONSE_TIME, LoggerConst.DATE_FORMAT.format(responseDate));
                        responseMap.put(LoggerConst.RESPONSE_KEY_TAKE_TIME, String.valueOf(Math.abs(responseDate.getTime() - requestTime.getTime())));
                        responseMap.put(LoggerConst.RESPONSE_KEY_HTTP_CODE, String.valueOf(responseWrapper.getStatus()));
                        responseMap.put(LoggerConst.RESPONSE_KEY_CONTENT_TYPE, responseWrapper.getContentType());
                        String responseData = IOUtils.toString(responseWrapper.getContentInputStream(), responseWrapper.getCharacterEncoding());
                        responseMap.put(LoggerConst.RESPONSE_KEY_RESPONSE_DATA, StringUtils.isNotBlank(responseData) ? responseData : "");
                        String responseJson = JSON.toJSONString(responseMap);
                        LOGGER.info(LoggerConst.RESPONSE_PREFIX + LoggerConst.RESPONSE_IDENTITY + responseJson);
                    }
                } catch (Exception e) {
                    logger.error("记录access_log异常", e);
                } finally {
                    responseWrapper.copyBodyToResponse();
                    REQUEST_TIME.remove();
                }
            }
        }
    }

    /**
     * 获取所有额外参数
     */
    private static Map<String, String> getAllAndRemove() {
        return ObjectUtils.defaultIfNull(EXTRA_PARAM.get(), new ConcurrentHashMap<>());
    }

}
