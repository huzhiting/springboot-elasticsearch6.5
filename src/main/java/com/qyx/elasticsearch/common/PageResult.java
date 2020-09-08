package com.qyx.elasticsearch.common;

import lombok.Data;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;

/**
 * 分页查询返回结果
 *
 * @author : huzhiting
 * @date : 2020-09-04 15:03
 */
@Data
public class PageResult implements Serializable {
    /**
     * 总条数
     */
    private Integer total;

    /**
     * 当前页
     */
    private Integer current;

    /**
     * 每页显示数量
     */
    private Integer size;

    /**
     * 总页数
     */
    private Integer pages;

    /**
     * 数据结果
     */
    private List<?> records;

    public PageResult() {
        this.records = Collections.emptyList();
        this.total = 0;
        this.size = 10;
        this.current = 1;
    }
}
