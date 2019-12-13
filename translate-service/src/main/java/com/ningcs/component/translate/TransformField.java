package com.ningcs.component.translate;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @description:
 * @author: ningcs
 * @create: 2019-12-13 14:18
 **/
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
public @interface TransformField {


    /**
     * 转换类型
     * @return
     */
    String type();

    /**
     * 源字段名
     * @return
     */
    String sourceFieldName();

    /**
     * 转换字段名
     * @return
     */
    String transformFieldName();


}
