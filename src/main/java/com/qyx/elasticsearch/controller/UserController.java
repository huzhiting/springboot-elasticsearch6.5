package com.qyx.elasticsearch.controller;

import com.alibaba.fastjson.JSON;
import com.qyx.elasticsearch.common.BaseResult;
import com.qyx.elasticsearch.common.PageResult;
import com.qyx.elasticsearch.dto.UserDTO;
import com.qyx.elasticsearch.dto.UserPageDTO;
import com.qyx.elasticsearch.entity.User;
import com.qyx.elasticsearch.service.UserService;
import com.qyx.elasticsearch.vo.UserAddReqVO;
import com.qyx.elasticsearch.vo.UserQueryPageReqVO;
import com.qyx.elasticsearch.vo.UserQueryReqVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 用户 控制类
 *
 * @author : huzhiting
 * @date : 2020-09-04 11:27
 */
@RestController
@Slf4j
@RequestMapping("/user")
public class UserController {

    @Resource
    UserService userService;

    @PostMapping("/add")
    public BaseResult addUser(@RequestBody UserAddReqVO reqVO) {
        BaseResult result = new BaseResult();
        UserDTO dto = new UserDTO();
        BeanUtils.copyProperties(reqVO, dto);
        String id = userService.save(dto);
        log.info("[UserController.addUser] [end] [insert success, param is {}]", JSON.toJSONString(reqVO));
        return result.success(id);
    }

    @GetMapping("/one/{id}")
    public BaseResult queryOne(@PathVariable Long id) {
        BaseResult result = new BaseResult();
        UserDTO dto = new UserDTO();
        dto.setId(id);
        String info = userService.queryOne(dto);
        log.info("[UserController.findOne] [end] [query success, param is {}, result is {}]", id, info);
        return result.success(info);
    }

    @PostMapping("/all")
    public BaseResult queryAll(@RequestBody UserQueryReqVO reqVO) {
        BaseResult result = new BaseResult();
        UserDTO dto = new UserDTO();
        BeanUtils.copyProperties(reqVO, dto);
        Map<String, Object> info = userService.queryAll(dto);
        log.info("[UserController.queryAll] [end] [query success, param is {}, result is {}]", JSON.toJSON(reqVO), info);
        return result.success(info);
    }

    @PostMapping("/page/all")
    public BaseResult pageAll(@RequestBody UserQueryPageReqVO reqVO) {
        BaseResult result = new BaseResult();
        UserPageDTO dto = new UserPageDTO();
        BeanUtils.copyProperties(reqVO, dto);
        if (reqVO.getPage() == null || reqVO.getPage() < 0) {
            dto.setPage(0);
        } else {
            dto.setPage(reqVO.getPage() - 1);
        }
        if (reqVO.getSize() == null || reqVO.getSize() < 0) {
            dto.setSize(10);
        } else {
            dto.setSize(reqVO.getSize());
        }
        PageResult info = userService.pageAll(dto);
        log.info("[UserController.pageAll] [end] [query success, param is {}, result is {}]", JSON.toJSON(reqVO), info);
        return result.success(info);
    }

    @PostMapping("/bulk")
    public BaseResult bulkUser(@RequestBody List<UserAddReqVO> reqVO) {
        BaseResult result = new BaseResult();
        List<UserDTO> userDTOList = new ArrayList<>();
        if (!CollectionUtils.isEmpty(reqVO)) {
            reqVO.forEach(r -> {
                UserDTO dto = new UserDTO();
                BeanUtils.copyProperties(r, dto);
                userDTOList.add(dto);
            });
        }
        userService.bulkUser(userDTOList);
        log.info("[UserController.bulkUser] [end] [insert success, param is {}]", JSON.toJSONString(reqVO));
        return result.success();
    }

    /**
     * 删除指定id数据
     *
     * @param id
     * @return
     */
    @RequestMapping("/delete/{id}")
    public BaseResult deleteAll(@PathVariable Long id) {
        BaseResult result = new BaseResult();
        String info = userService.deleteAll(id);
        log.info("[UserController.deleteAll] [end] [delete success, result is {}]", info);
        return result.success();
    }

    /**
     * 判断某个id数据是否存在
     *
     * @param id
     * @return
     */
    @GetMapping("/exist/{id}")
    public BaseResult findExist(@PathVariable Long id) {
        BaseResult result = new BaseResult();
        boolean exist = userService.findExist(id);
        log.info("[UserController.findExist] [end] [find exist, result is {}]", exist);
        return result.success(exist);
    }

}
