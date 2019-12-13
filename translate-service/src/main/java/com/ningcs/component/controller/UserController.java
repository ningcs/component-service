package com.ningcs.component.controller;

import com.ningcs.component.dto.UserListDTO;
import com.ningcs.component.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @description:
 * @author: ningcs
 * @create: 2019-12-13 14:28
 **/
@RestController
@Slf4j
@RequestMapping("/user")
public class UserController {


    @Autowired
    private UserService userService;


    @GetMapping("/all/list")
    public List<UserListDTO> findUserListDTO(){
        log.debug("开始.......");
        return userService.findUserList();
    }
}
