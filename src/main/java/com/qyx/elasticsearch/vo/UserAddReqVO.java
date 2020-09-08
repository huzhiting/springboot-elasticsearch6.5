package com.qyx.elasticsearch.vo;

import com.qyx.elasticsearch.entity.User;
import lombok.Data;

import java.io.Serializable;

/**
 * 添加用户请求参数
 *
 * @author : huzhiting
 * @date : 2020-09-04 11:29
 */
@Data
public class UserAddReqVO extends User implements Serializable {
}
