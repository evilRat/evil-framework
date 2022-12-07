package com.evil.framework.beans.exception;


import com.evil.framework.beans.constant.RestfulCode;

/**
 * @author SongJian
 * Created by SongJian at 2018/4/16.
 */
public class BizRuntimeException extends RuntimeException implements BaseBizException {

    private static final long serialVersionUID = 5315085822828857512L;

    /**
     * 错误码
     */
    private Integer code;

    BizRuntimeException() {
    }

    public BizRuntimeException(RestfulCode rc) {
        this(rc.getCode(), rc.getMessage(), null);
    }

    public BizRuntimeException(RestfulCode rc, Throwable e) {
        this(rc.getCode(), rc.getMessage(), e);
    }

    public BizRuntimeException(RestfulCode rc, String message) {
        this(rc.getCode(), message, null);
    }

    public BizRuntimeException(RestfulCode rc, String message, Throwable e) {
        this(rc.getCode(), message, e);
    }

    public BizRuntimeException(Integer code, String message) {
        this(code, message, null);
    }

    public BizRuntimeException(Integer code, String message, Throwable cause) {
        super(message, cause);
        this.code = code;
    }

    public BizRuntimeException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

    public BizRuntimeException(Integer code, String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
        this.code = code;
    }

    @Override
    public Integer getCode() {
        return code;
    }
}
