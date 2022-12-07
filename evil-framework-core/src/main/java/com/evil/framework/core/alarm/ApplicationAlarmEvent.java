package com.evil.framework.core.alarm;


import com.evil.framework.core.FrameworkApplicationEvent;

/**
 * 基于异常体系的应用告警事件
 *
 * @author  kongzheng
 * @since 1.0.0.RELEASE
 */
public class ApplicationAlarmEvent extends FrameworkApplicationEvent<Throwable> {

    private String requestUri;

    private String traceId;

    public ApplicationAlarmEvent(Object source, Throwable throwable) {
        super(source, throwable);
    }

    public String getRequestUri() {
        return requestUri;
    }

    public void setRequestUri(String requestUri) {
        this.requestUri = requestUri;
    }

    public String getTraceId() {
        return traceId;
    }

    public void setTraceId(String traceId) {
        this.traceId = traceId;
    }

    @Override
    public String toString() {
        return "ApplicationAlarmEvent{" +
                "requestUri='" + requestUri + '\'' +
                ", traceId='" + traceId + '\'' +
                '}';
    }
}
