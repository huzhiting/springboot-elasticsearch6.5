package com.qyx.elasticsearch.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * 分页查询用户
 *
 * @author : huzhiting
 * @date : 2020-09-04 18:42
 */
@Data
public class UserPageDTO implements Serializable {
    private Integer page;

    private Integer size;

    private String name;
}
