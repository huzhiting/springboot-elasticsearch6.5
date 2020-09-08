package com.qyx.elasticsearch.service;

import com.qyx.elasticsearch.common.PageResult;
import com.qyx.elasticsearch.dto.UserDTO;
import com.qyx.elasticsearch.dto.UserPageDTO;

import java.util.List;
import java.util.Map;

/**
 * 用户Service接口类
 *
 * @author : huzhiting
 * @date : 2020-09-04 11:25
 */
public interface UserService {
    /**
     * 保存用户
     *
     * @param dto
     * @return
     */
    String save(UserDTO dto);

    /**
     * 查询指定用户
     *
     * @param dto
     * @return
     */
    String queryOne(UserDTO dto);

    /**
     * 条件查询用户
     *
     * @param dto
     * @return
     */
    Map<String, Object> queryAll(UserDTO dto);

    /**
     * 条件查询用户（分页）
     *
     * @param dto
     * @return
     */
    PageResult pageAll(UserPageDTO dto);

    /**
     * 批量插入用户
     *
     * @param dto
     */
    void bulkUser(List<UserDTO> dto);

    /**
     * 删除指定id数据
     * @param id
     * @return
     */
    String deleteAll(Long id);

    /**
     * 判断某个id数据是否存在
     * @param id
     * @return
     */
    boolean findExist(Long id);
}
