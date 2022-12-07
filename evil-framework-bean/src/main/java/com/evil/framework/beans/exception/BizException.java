package com.evil.framework.beans.exception;


import com.evil.framework.beans.constant.RestfulCode;

public class BizException extends Exception implements BaseBizException {

    private static final long serialVersionUID = 4630227923217642600L;

    /**
     * 错误码
     */
    private Integer code;

    BizException() {
    }

    public BizException(RestfulCode rc) {
        this(rc.getCode(), rc.getMessage(), null);
    }

    public BizException(RestfulCode rc, Throwable e) {
        this(rc.getCode(), rc.getMessage(), e);
    }

    public BizException(RestfulCode rc, String message) {
        this(rc.getCode(), message, null);
    }

    public BizException(RestfulCode rc, String message, Throwable e) {
        this(rc.getCode(), message, e);
    }

    public BizException(Integer code, String message) {
        this(code, message, null);
    }

    public BizException(Integer code, String message, Throwable cause) {
        super(message, cause);
        this.code = code;
    }

    public BizException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

    public BizException(Integer code, String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
        this.code = code;
    }

    @Override
    public Integer getCode() {
        return code;
    }
}
