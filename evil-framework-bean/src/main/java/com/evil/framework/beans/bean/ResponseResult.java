package com.evil.framework.beans.bean;


import com.evil.framework.beans.constant.RestfulCode;
import com.evil.framework.beans.exception.BizRuntimeException;
import lombok.Data;
import org.apache.skywalking.apm.toolkit.trace.TraceContext;

import java.util.Optional;

@Data
public class ResponseResult<T> extends Response<T> {

    private String traceId;

    public static <T> ResponseResult<T> success(T data) {
        return newInstance(RestfulCode.SUCCESS.getCode(), data, RestfulCode.SUCCESS.getMessage());
    }

    public static ResponseResult<String> fail(BizRuntimeException exception) {
        return newInstance(exception.getCode(), "", exception.getMessage());
    }

    public static <T> ResponseResult<T> of(RestfulCode code, T data) {
        return newInstance(code.getCode(), data, code.getMessage());
    }

    public static <T> ResponseResult<T> of(RestfulCode code, T data, String message) {
        return newInstance(code.getCode(), data, message);
    }

    public String obtainTraceId() {
        return Optional.ofNullable(TraceContext.traceId()).orElse("N/A");
    }

    private static <T> ResponseResult<T> newInstance(Integer code, T data, String msg) {
        ResponseResult<T> result = new ResponseResult<>();
        result.setCode(code);
        result.setMsg(msg);
        result.setData(data);
        result.setTraceId(result.obtainTraceId());
        return result;
    }


}
