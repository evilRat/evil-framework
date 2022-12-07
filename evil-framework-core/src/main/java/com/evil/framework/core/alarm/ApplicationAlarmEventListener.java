package com.evil.framework.core.alarm;

import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;

import java.util.List;


/**
 * 应用告警监听器
 * <p>
 * 目前只监听{@link ApplicationAlarmEvent}
 *
 * @author  kongzheng
 * @since 1.0.0.RELEASE
 */
public class ApplicationAlarmEventListener {

    private final List<ApplicationAlarmHandler> exceptionHandlers;

    public ApplicationAlarmEventListener(List<ApplicationAlarmHandler> exceptionHandlers) {
        this.exceptionHandlers = exceptionHandlers;
    }

    /**
     * 监听基于异常的应用告警事件
     *
     * @param event 基于异常的应用告警事件
     */
    @Async
    @EventListener(ApplicationAlarmEvent.class)
    public void onApplicationAlarmEvent(ApplicationAlarmEvent event) {
        exceptionHandlers.forEach(exceptionHandler -> exceptionHandler.handleException(event.getRequestUri(),event.getTraceId(),event.getT()));
    }

}
