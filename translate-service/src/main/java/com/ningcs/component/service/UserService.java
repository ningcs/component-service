package com.ningcs.component.service;

import com.ningcs.component.dto.UserListDTO;

import java.util.List;

/**
 * @description: 用户服务
 * @author: ningcs
 * @create: 2019-12-13 14:29
 **/
public interface UserService {

    /**
     * 获取用户列表
     * @return
     */
    public List<UserListDTO> findUserList();
}
