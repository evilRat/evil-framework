package com.evil.framework.web.error;

import com.alibaba.csp.sentinel.Tracer;
import com.alibaba.csp.sentinel.slots.block.BlockException;
import com.alibaba.csp.sentinel.slots.block.authority.AuthorityException;
import com.alibaba.csp.sentinel.slots.block.flow.FlowException;
import com.evil.framework.beans.bean.ResponseResult;
import com.evil.framework.beans.exception.BizRuntimeException;
import com.evil.framework.core.alarm.ApplicationAlarmEvent;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import lombok.extern.slf4j.Slf4j;
import org.apache.skywalking.apm.toolkit.trace.TraceContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.ApplicationEventMulticaster;
import org.springframework.http.HttpStatus;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindException;
import org.springframework.validation.ObjectError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.servlet.http.HttpServletRequest;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.util.Optional;

/**
 * 全局异常处理类
 *
 * @author kongzheng
 * @since 1.0.0.RELEASE
 */
@Slf4j
@RestControllerAdvice
public class DefaultGlobalExceptionHandler {

    @Autowired
    private ApplicationEventMulticaster eventMulticaster;

    @Autowired
    private MeterRegistry meterRegistry;

    @ExceptionHandler(Throwable.class)
    public ResponseResult<String> handleException(HttpServletRequest request, Throwable exception) {
        log.error("invoke web url:{} error", request.getRequestURI(), exception);
        ResponseResult<String> result = new ResponseResult<>();
        result.setTraceId(result.obtainTraceId());

        Counter.Builder counterBuilder = Counter.builder("http.error.requests");
        counterBuilder.tag("exception", exception.getClass().getSimpleName());

        if (exception instanceof BizRuntimeException) {
            BizRuntimeException bizRuntimeException = (BizRuntimeException) exception;
            result.setCode(bizRuntimeException.getCode());
            result.setMsg(bizRuntimeException.getMessage());
            counterBuilder.tag("code", bizRuntimeException.getCode() + "");
            counterBuilder.register(meterRegistry).increment();
            return result;
        } else if (exception instanceof BindException) {
            BindException bindException = (BindException) exception;
            StringBuilder errorMessage = new StringBuilder();
            for (ObjectError error : bindException.getAllErrors()) {
                errorMessage.append(error.getDefaultMessage());
                break;
            }
            result.setMsg(errorMessage.toString());
            result.setCode(400);
            counterBuilder.tag("code", "4xx");
            counterBuilder.register(meterRegistry).increment();
            return result;

        } else if (exception instanceof MethodArgumentNotValidException) {
            MethodArgumentNotValidException notValidException = (MethodArgumentNotValidException) exception;
            StringBuilder errorMessage = new StringBuilder();
            for (ObjectError error : notValidException.getBindingResult().getAllErrors()) {
                errorMessage.append(error.getDefaultMessage());
                break;
            }
            result.setMsg(errorMessage.toString());
            result.setCode(400);
            counterBuilder.tag("code", "4xx");
            counterBuilder.register(meterRegistry).increment();
            return result;
        } else if (exception instanceof ConstraintViolationException) {
            ConstraintViolationException constraintViolationException = (ConstraintViolationException) exception;
            StringBuilder errorMessage = new StringBuilder();
            for (ConstraintViolation constraintViolation : constraintViolationException.getConstraintViolations()) {
                errorMessage.append(constraintViolation.getMessage());
                break;
            }
            result.setMsg(errorMessage.toString());
            result.setCode(400);
            counterBuilder.tag("code", "4xx");
            counterBuilder.register(meterRegistry).increment();
            return result;
        } else if (exception instanceof IllegalStateException) {
            result.setMsg(exception.getMessage());
            result.setCode(400);
            counterBuilder.tag("code", "4xx");
            counterBuilder.register(meterRegistry).increment();
            return result;
        } else if (exception instanceof HttpRequestMethodNotSupportedException) {
            result.setMsg(exception.getMessage());
            result.setCode(415);
            counterBuilder.tag("code", "4xx");
            counterBuilder.register(meterRegistry).increment();
            return result;
        } else if (exception instanceof MissingServletRequestParameterException) {
            result.setMsg(exception.getMessage());
            result.setCode(415);
            counterBuilder.tag("code", "4xx");
            counterBuilder.register(meterRegistry).increment();
            return result;
        } else if (exception instanceof FlowException) {
            result.setCode(HttpStatus.TOO_MANY_REQUESTS.value());
            result.setMsg(HttpStatus.TOO_MANY_REQUESTS.getReasonPhrase());
            counterBuilder.tag("code", "4xx");
            counterBuilder.register(meterRegistry).increment();
            return result;
        } else if (exception instanceof AuthorityException) {
            result.setCode(HttpStatus.UNAUTHORIZED.value());
            result.setMsg(HttpStatus.UNAUTHORIZED.getReasonPhrase());
            counterBuilder.tag("code", "4xx");
            counterBuilder.register(meterRegistry).increment();
            return result;
        } else if (exception instanceof BlockException) {
            result.setCode(430);
            result.setMsg("请求过于频繁，请稍后再试");
            counterBuilder.tag("code", "4xx");
            counterBuilder.register(meterRegistry).increment();
            return result;
        } else if (exception instanceof IllegalArgumentException) {
            result.setCode(HttpStatus.BAD_REQUEST.value());
            result.setMsg(StringUtils.hasText(exception.getMessage()) ? exception.getMessage() : "参数错误");
            counterBuilder.tag("code", "4xx");
            counterBuilder.register(meterRegistry).increment();
            return result;
        }

        // sentinel 降级埋点
        // 处理异常
        Tracer.trace(exception);

        ApplicationAlarmEvent event = new ApplicationAlarmEvent(this, exception);
        event.setRequestUri(request.getRequestURI());
        event.setTraceId(Optional.ofNullable(TraceContext.traceId()).orElse("N/A"));
        eventMulticaster.multicastEvent(event);

        // 非预期异常
        String msg = StringUtils.hasText(exception.getMessage()) ? exception.getMessage() : getStatus(request).getReasonPhrase();
        result.setMsg(msg);
        int code = getStatus(request).value();
        result.setCode(code);
        counterBuilder.tag("code", String.valueOf(code).charAt(0) + "xx");
        counterBuilder.register(meterRegistry).increment();
        return result;

    }

    private HttpStatus getStatus(HttpServletRequest request) {

        Integer statusCode = (Integer) request
                .getAttribute("javax.servlet.error.status_code");
        if (statusCode == null) {
            return HttpStatus.INTERNAL_SERVER_ERROR;
        }
        try {
            return HttpStatus.valueOf(statusCode);
        } catch (Exception ex) {
            return HttpStatus.INTERNAL_SERVER_ERROR;
        }

    }

}
