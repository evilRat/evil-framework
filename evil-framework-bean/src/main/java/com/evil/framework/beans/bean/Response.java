package com.evil.framework.beans.bean;

import com.evil.framework.beans.constant.RestfulCode;
import com.evil.framework.beans.exception.BizException;
import com.evil.framework.beans.exception.BizRuntimeException;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import java.io.Serializable;
import java.util.Optional;
import java.util.function.Supplier;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ApiModel("统一返回")
public class Response<T> implements Serializable {

    private static final long serialVersionUID = -6724401478874316373L;

    public static <T> Response<T> success() {
        return Response.success(RestfulCode.SUCCESS, null, null);
    }

    public static <T> Response<T> success(T data) {
        return Response.success(RestfulCode.SUCCESS, null, data);
    }

    public static <T> Response<T> success(RestfulCode rc, T data) {
        return Response.success(rc, null, data);
    }

    public static <T> Response<T> success(RestfulCode rc, String msg, T data) {
        return Response.<T>builder()
                .code(rc.getCode())
                .msg(Optional.ofNullable(msg).orElse(rc.getMessage()))
                .data(data)
                .build();
    }

    public static <T> Response<T> fail(BizException e) {
        return Response.<T>builder()
                .code(e.getCode())
                .msg(e.getMessage())
                .build();
    }

    public static <T> Response<T> fail(BizRuntimeException e) {
        return Response.<T>builder()
                .code(e.getCode())
                .msg(e.getMessage())
                .build();
    }

    public static <T> Response<T> fail(Integer code, String msg) {
        return Response.<T>builder()
                .code(code)
                .msg(msg)
                .build();
    }

    public static <T> Response<T> fail(RestfulCode rs) {
        return Response.<T>builder()
                .code(rs.getCode())
                .msg(rs.getMessage())
                .build();
    }

    public static <T> Response<T> fail(Integer code, String msg, T data) {
        return Response.<T>builder()
                .code(code)
                .msg(msg)
                .data(data)
                .build();
    }

    public static <T> Response<T> fail(RestfulCode rs, T data) {
        return Response.<T>builder()
                .code(rs.getCode())
                .msg(rs.getMessage())
                .data(data)
                .build();
    }

    public static <T> Response<T> fail(Response source) {
        return Response.<T>builder()
                .code(source.getCode())
                .msg(source.getMsg())
                .build();
    }

    /**
     * 判断返回码是否相同，与是否成功无关
     *
     * @param restfulCode
     *
     * @return
     */
    public boolean codeEquals(RestfulCode restfulCode) {
        if (this.getCode() == null || restfulCode == null) {
            return false;
        }
        return this.getCode().equals(restfulCode.getCode());
    }

    /**
     * 如果成功则返回data，失败则抛出异常
     * @return data
     * @throws BizRuntimeException 失败抛出
     */
    public T orFailThrow() throws BizRuntimeException {
        if (isSuccess()) {
            return getData();
        } else {
            throw new BizRuntimeException(getCode(), getMsg());
        }
    }

    public <X extends Throwable> T orFailThrow(Supplier<? extends X> supplier) throws X {
        if (isSuccess()) {
            return getData();
        } else {
            throw supplier.get();
        }
    }

    /**
     * 期望有data
     * @return data
     * @throws BizRuntimeException 失败或data为null 抛出异常
     */
    public T forceData(RestfulCode error) throws BizRuntimeException {
        return Optional.ofNullable(orFailThrow()).orElseThrow(() -> new BizRuntimeException(error));
    }

    public boolean isSuccess() {
        return RestfulCode.SUCCESS.isEquals(getCode());
    }

    /**
     * 状态码
     */
    @ApiModelProperty("状态码")
    private Integer code = RestfulCode.SUCCESS.getCode();

    /**
     * 描述信息
     */
    @ApiModelProperty("描述信息")
    private String msg = RestfulCode.SUCCESS.getMessage();

    /**
     * 返回数据
     */
    @ApiModelProperty("返回数据")
    private T data;
}
