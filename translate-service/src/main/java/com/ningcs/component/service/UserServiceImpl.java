package com.ningcs.component.service;

import com.ningcs.component.dto.UserListDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

/**
 * @description: 用户服务
 * @author: ningcs
 * @create: 2019-12-13 14:29
 **/
@Service
@Slf4j
public class UserServiceImpl implements UserService {


    @Override
    public List<UserListDTO> findUserList() {

        UserListDTO userListDTO = new UserListDTO();
        userListDTO.setId(1L);
        userListDTO.setName("");
        userListDTO.setMobile("1");
        userListDTO.setEmail("1");

        UserListDTO userListDTO1 = new UserListDTO();
        userListDTO1.setId(2L);
        userListDTO1.setName("");
        userListDTO1.setMobile("2");
        userListDTO1.setEmail("2");
        UserListDTO userListDTO2 = new UserListDTO();
        userListDTO2.setId(3L);
        userListDTO2.setName("");
        userListDTO2.setMobile("3");
        userListDTO2.setEmail("3");

        return Arrays.asList(userListDTO,userListDTO1,userListDTO2);
    }
}
