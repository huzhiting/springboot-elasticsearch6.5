package com.qyx.elasticsearch.common;

import lombok.Data;

import java.io.Serializable;

/**
 * 分页请求参数
 *
 * @author : huzhiting
 * @date : 2020-09-04 14:51
 */
@Data
public class PageReq implements Serializable {
    private Integer page;

    private Integer size;
}
