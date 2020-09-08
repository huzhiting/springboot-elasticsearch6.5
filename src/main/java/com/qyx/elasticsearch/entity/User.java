package com.qyx.elasticsearch.entity;

import lombok.Data;

import java.io.Serializable;

/**
 * 用户实体类
 *
 * @author : huzhiting
 * @date : 2020-09-04 11:23
 */
@Data
public class User implements Serializable {

    private Long id;

    private String name;

    private Integer sex;

    private String email;

    private String phone;
}
