package com.evil.framework.beans.exception;

public interface BaseBizException {

    /**
     * 获取错误码
     *
     * @return
     */
    Integer getCode();

    /**
     * 获取错误描述
     *
     * @return
     */
    String getMessage();
}
