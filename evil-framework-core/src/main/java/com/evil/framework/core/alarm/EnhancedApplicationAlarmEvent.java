package com.evil.framework.core.alarm;

import org.apache.skywalking.apm.toolkit.trace.TraceContext;
import org.springframework.util.StringUtils;

import java.util.Optional;

/**
 * 增强{@link ApplicationAlarmEvent}
 *
 * @author kongzheng
 * @since 2021/12/28 11:23 上午
 */
public class EnhancedApplicationAlarmEvent extends ApplicationAlarmEvent {

    public EnhancedApplicationAlarmEvent(Object source, String alarmMsg) {
        super(source, StringUtils.hasText(alarmMsg) ? new RuntimeException(alarmMsg) : null);
        super.setTraceId(Optional.ofNullable(TraceContext.traceId()).orElse("N/A"));
    }

    public void setKeyword(String keyword) {
        super.setRequestUri(keyword);
    }

}
