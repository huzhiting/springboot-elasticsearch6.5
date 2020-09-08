package com.qyx.elasticsearch.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * 用户查询请求参数
 *
 * @author : huzhiting
 * @date : 2020-09-04 13:39
 */
@Data
public class UserQueryReqVO implements Serializable {
    private Long id;

    private String name;
}
