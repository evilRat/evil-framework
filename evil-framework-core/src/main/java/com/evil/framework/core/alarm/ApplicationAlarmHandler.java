package com.evil.framework.core.alarm;

/**
 * 应用非预期异常处理器
 *
 * @author kongzheng
 * @since 1.0.0.RELEASE
 */
public interface ApplicationAlarmHandler {

    /**
     * 处理非预期异
     *
     * @param throwable 应用非预期异常
     */
    default void handleException(String requestUri, String traceId, Throwable throwable){
        // do nothing
    }

}
