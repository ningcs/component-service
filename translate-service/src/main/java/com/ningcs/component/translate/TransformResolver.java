package com.ningcs.component.translate;

import java.util.Map;
import java.util.Set;

/**
 * @description:转换解析器
 * @author: ningcs
 * @create: 2019-12-13 14:18
 **/
public interface TransformResolver {

    Map<Long, Object> execute(Set<Long> ids);
}
