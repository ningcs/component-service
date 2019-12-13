package com.ningcs.component.model;

import lombok.Data;

/**
 * @description:
 * @author: ningcs
 * @create: 2019-12-13 14:31
 **/
@Data
public class User {

    /**
     *用户主键
     */
    private Long id;

    /**
     *
     */
    private String name;


    /**
     * 手机号
     */
    private String mobile;


    /**
     * 邮箱
     */
    private String email;
}
