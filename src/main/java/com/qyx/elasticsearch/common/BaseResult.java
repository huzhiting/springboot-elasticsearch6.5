package com.qyx.elasticsearch.common;

import com.fasterxml.jackson.databind.ser.Serializers;
import lombok.Data;

import java.io.Serializable;

/**
 * 接口返回实体
 *
 * @author : huzhiting
 * @date : 2020-09-04 11:36
 */
@Data
public class BaseResult<T> implements Serializable {
    /**
     * 返回码
     */
    private String code;
    /**
     * 返回描述
     */
    private String msg;
    /**
     * 返回内容
     */
    private T data;

    /**
     * 成功
     */
    public BaseResult success() {
        this.code = "200";
        this.msg = "成功";
        return this;
    }

    public <T> BaseResult success(T data) {
        BaseResult<T> result = new BaseResult<>();
        result.success();
        result.setData(data);
        return result;
    }

    /**
     * 失败
     */
    public void fail() {
        this.code = "500";
        this.msg = "失败";
    }
}
