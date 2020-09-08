package com.qyx.elasticsearch.vo;

import com.qyx.elasticsearch.common.PageReq;
import lombok.Data;

import java.io.Serializable;

/**
 * 分页查询用户请求参数
 *
 * @author : huzhiting
 * @date : 2020-09-04 14:52
 */
@Data
public class UserQueryPageReqVO extends PageReq implements Serializable {
    private String name;
}
