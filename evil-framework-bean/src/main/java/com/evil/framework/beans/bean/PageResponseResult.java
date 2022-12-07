package com.evil.framework.beans.bean;


import com.evil.framework.beans.constant.RestfulCode;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
public class PageResponseResult<T> extends ResponseResult<List<T>> {

    /**
     * 当前页码
     */
    @ApiModelProperty("当前页码")
    private Integer pageNo = 0;

    /**
     * 每页大小
     */
    @ApiModelProperty("每页大小")
    private Integer pageSize = 0;

    /**
     * 总页数
     */
    @ApiModelProperty("总页数")
    private Long pageCount = 0L;

    /**
     * 总记录数
     */
    @ApiModelProperty("总记录数")
    private Long totalCount = 0L;

    public static <T> PageResponseResult<T> success(List<T> data){
        return newInstance(RestfulCode.SUCCESS.getCode(), data,RestfulCode.SUCCESS.getMessage());
    }

    public void populatePageInfo(Integer pageNO,
                                 Integer pageSize,
                                 Long pageCount,
                                 Long totalCount){
        this.setPageNo(pageNO);
        this.setPageSize(pageSize);
        this.setPageCount(pageCount);
        this.setTotalCount(totalCount);
    }

    private static <T> PageResponseResult<T> newInstance(Integer code, List<T> data, String msg){
        PageResponseResult<T> pageResult = new PageResponseResult<>();
        pageResult.setCode(code);
        pageResult.setMsg(msg);
        pageResult.setData(data);
        pageResult.setTraceId(pageResult.getTraceId());
        return pageResult;
    }

}
