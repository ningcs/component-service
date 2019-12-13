package com.ningcs.component.util;

import com.alibaba.fastjson.JSON;
import com.ningcs.component.translate.TransformBean;
import com.ningcs.component.translate.TransformField;
import com.ningcs.component.translate.TransformResolver;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.util.*;
import java.util.concurrent.CountDownLatch;

/**
 * @description:
 * @author: ningcs
 * @create: 2019-12-13 15:21
 **/
@Component
@Slf4j
public class LeadingBeanTransformResolver {
    private Integer MAX_LEVEL = 3;


    public void execute(Object obj) {
        Map<String, Set<Long>> transformResolverMap = new HashMap<>();
        this.findSourceFieldValues(obj, transformResolverMap, 1);
        log.debug("Method:execute transformResolverMap:{}", JSON.toJSONString(transformResolverMap));
        if (transformResolverMap.isEmpty()) {
            return;
        }
        CountDownLatch countDownLatch = new CountDownLatch(transformResolverMap.size());
        for (String type : transformResolverMap.keySet()) {
            this.findDataAndTransformField(obj, transformResolverMap, type, countDownLatch);
        }
        try {
            countDownLatch.await();
        } catch (InterruptedException e) {
            log.error("Method:execute countDownLatch.await error:{}", e);
        }
    }

    /**
     * 获取sourceField源字段值
     *
     * @param obj
     *            对象
     * @param transformResolverMap
     *            存储不同转换类型的源字段值的集合 key为LeadingTransformField 注解上type的值 ； value为LeadingTransformField注解上sourceFieldName
     *            对应的值的集合
     * @param level
     *            层级
     */
    private void findSourceFieldValues(Object obj, Map<String, Set<Long>> transformResolverMap, Integer level) {
        if (obj == null) {
            return;
        }
        if (obj instanceof List && level < this.MAX_LEVEL) {
            Iterator iterator = ((List)obj).iterator();
            while (iterator.hasNext()) {
                this.findSourceFieldValues(iterator.next(), transformResolverMap, level);
            }
            return;
        }
        Class<?> objClass = obj.getClass();
        Field[] fields = objClass.getDeclaredFields();
        for (Field field : fields) {
            field.setAccessible(true);
            Optional.ofNullable(field.getAnnotation(TransformField.class))
                    .ifPresent(leadingTransformAnnotation -> {
                        String type = leadingTransformAnnotation.type();
                        Set<Long> transformIds = transformResolverMap.get(type);
                        if (transformIds == null) {
                            transformIds = new HashSet<>();
                            transformResolverMap.put(type, transformIds);
                        }
                        String sourceFieldName = leadingTransformAnnotation.sourceFieldName();
                        try {
                            Field sourceField = objClass.getDeclaredField(sourceFieldName);
                            sourceField.setAccessible(true);
                            transformIds.add((Long)sourceField.get(obj));
                        } catch (NoSuchFieldException e) {
                            log.error("Method:findSourceFieldValues 1 getDeclaredField sourceFieldName:{} error:{}",
                                    sourceFieldName, e.getMessage());
                        } catch (IllegalAccessException e) {
                            log.error("Method:findSourceFieldValues 2 反射{}值异常 error:{}", sourceFieldName, e.getMessage());
                        } catch (Exception e) {
                            log.error("Method:findSourceFieldValues 3 sourceFieldName:{} error:{}", sourceFieldName,
                                    e.getMessage());
                        }
                    });

            Optional.ofNullable(field.getAnnotation(TransformBean.class)).ifPresent(leadingTransformBean -> {
                try {
                    this.findSourceFieldValues(field.get(obj), transformResolverMap, level + 1);
                } catch (Exception e) {
                    log.error("Method:findSourceFieldValues  field name:{} error:{}", field.getName(), e.getMessage());
                }
            });
        }
    }

    /**
     * 查询并转换字段
     *
     * @param obj
     *            待转换对象
     * @param transformResolverMap
     *            存储不同转换类型的源字段值的集合 key为LeadingTransformField 注解上type的值 ； value为LeadingTransformField注解上sourceFieldName
     *            对应的值的集合
     * @param type
     *            待转换类型
     * @param countDownLatch
     */
    private void findDataAndTransformField(Object obj, Map<String, Set<Long>> transformResolverMap, String type,
                                           CountDownLatch countDownLatch) {
        Integer level = 1;
            try {
                Optional.ofNullable(transformResolverMap.get(type)).ifPresent(ids -> {
                    TransformResolver transformResolver = (TransformResolver)SpringApplicationHolder.getBean(type);
                    Map<Long, Object> resultMap = transformResolver.execute(ids);
                    log.debug("Method:findDataAndTransformField type:{} ids:{} resultMap:{}", type, ids,
                            JSON.toJSONString(resultMap));
                    if (resultMap == null || resultMap.isEmpty()) {
                        return;
                    }
                    transformBean(obj, type, resultMap, level);
                });
            } catch (Exception e) {
                log.error("Method:findDataAndTransformField type:{}  error:{}",type, e);
            } finally {
                countDownLatch.countDown();
            }

    }

    /**
     * 转换Bean
     *
     * @param obj
     *            待转换对象
     * @param type
     *            转换类型
     * @param resultMap
     *            结果集
     * @param level
     *            级别
     */
    private void transformBean(Object obj, String type, Map<Long, Object> resultMap, Integer level) {
        if (obj == null) {
            return;
        }
        if (obj instanceof List && level < this.MAX_LEVEL) {
            Iterator iterator = ((List)obj).iterator();
            while (iterator.hasNext()) {
                this.transformBean(iterator.next(), type, resultMap, level);
            }
            return;
        }
        Class<?> beanClass = obj.getClass();
        Field[] fields = beanClass.getDeclaredFields();
        for (Field field : fields) {
            field.setAccessible(true);
            Optional.ofNullable(field.getAnnotation(TransformField.class))
                    .ifPresent(leadingTransformAnnotation -> {
                        String sourceFieldName = leadingTransformAnnotation.sourceFieldName();
                        String transformFieldName = leadingTransformAnnotation.transformFieldName();
                        try {
                            // 待转换字段不为null时，跳过
                            if (!type.equals(leadingTransformAnnotation.type()) || field.get(obj) != null) {
                                return;
                            }
                            Field sourceField = beanClass.getDeclaredField(sourceFieldName);
                            sourceField.setAccessible(true);
                            Long id = (Long)sourceField.get(obj);
                            if (id == null) {
                                return;
                            }
                            Object result = resultMap.get(id);
                            if (result == null) {
                                return;
                            }
                            if (StringUtils.isBlank(transformFieldName)) {
                                field.set(obj, result);
                            } else {
                                Field transformField = result.getClass().getDeclaredField(transformFieldName);
                                transformField.setAccessible(true);
                                field.set(obj, transformField.get(result));
                            }

                        } catch (NoSuchFieldException e) {
                            log.error("Method:transformBean 1 getDeclaredField sourceFieldName:{} error:{}",
                                    sourceFieldName,
                                    e.getMessage());
                        } catch (IllegalAccessException e) {
                            log.error("Method:transformBean 2 反射{}值异常 error:{}", sourceFieldName,
                                    e.getMessage());
                        } catch (Exception e) {
                            log.error("Method:transformBean 3 sourceFieldName:{} error:{}", sourceFieldName,
                                    e.getMessage());
                        }
                    });
            Optional.ofNullable(field.getAnnotation(TransformBean.class)).ifPresent(leadingTransformBean -> {
                try {
                    this.transformBean(field.get(obj), type, resultMap, level + 1);
                } catch (IllegalAccessException e) {
                    log.error("Method:transformBean  field name:{} error:{}", field.getName(), e.getMessage());
                }
            });
        }
    }
}
