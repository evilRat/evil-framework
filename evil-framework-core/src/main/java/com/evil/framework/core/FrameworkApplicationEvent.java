package com.evil.framework.core;

import org.springframework.context.ApplicationEvent;

/**
 * 框架应用事件，扩展自{@link ApplicationEvent}
 *
 * @author  kongzheng
 * @since 1.0.0.RELEASE
 */
public abstract class FrameworkApplicationEvent<T> extends ApplicationEvent {

    /**
     * 事件内容
     */
    private final T t;

    protected FrameworkApplicationEvent(Object source, T t) {
        super(source);
        this.t = t;
    }


    public T getT() {
        return t;
    }

    @Override
    public String toString() {
        return "FrameworkEvent{" +
                "t=" + t +
                ", source=" + source +
                '}';
    }

}
