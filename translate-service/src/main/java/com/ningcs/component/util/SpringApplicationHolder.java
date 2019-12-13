package com.ningcs.component.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Optional;

@Component
@Slf4j
public class SpringApplicationHolder implements ApplicationContextAware {

    private static ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        SpringApplicationHolder.applicationContext = applicationContext;

    }

    public static ApplicationContext getApplicationContext() {
        return applicationContext;
    }

    /**
     * 获取对象
     *
     * @param name
     * @return
     * @throws BeansException
     */
    public static Object getBean(String name) throws BeansException {
        return applicationContext.getBean(name);
    }

    public static Object getBean(Class c) throws BeansException {
        return applicationContext.getBean(c);
    }

    public static void inject(Object object) {
        Field[] declaredFields = object.getClass().getDeclaredFields();
        Arrays.stream(declaredFields)
                .filter(field -> field.getAnnotation(Autowired.class) != null
                        || field.getAnnotation(Resource.class) != null)
                .forEach(field -> {
                    field.setAccessible(true);
                    try {
                        field.set(object, getFieldBean(field));
                    } catch (IllegalAccessException e) {
                        log.error(e.getMessage(),e);
                    }
                });
    }

    private static Object getFieldBean(Field field) {
        if (field.isAnnotationPresent(Resource.class)) {
            Resource resource = field.getAnnotation(Resource.class);
            if (resource.name() != null && !resource.name().equals("")) {
                return Optional.of(applicationContext.getBean(resource.name(), field.getType())).get();
            }
        }
        return Optional.of(applicationContext.getBean(field.getType())).get();
    }
}
