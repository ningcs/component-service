package com.ningcs.component.dto;

import com.ningcs.component.translate.TransformField;
import lombok.Data;

/**
 * @description:
 * @author: ningcs
 * @create: 2019-12-13 14:31
 **/
@Data
public class UserListDTO {

    /**
     *用户主键
     */
    private Long id;

    /**
     *
     */
    @TransformField(type="tranFormUser",sourceFieldName="id",transformFieldName="name")
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
