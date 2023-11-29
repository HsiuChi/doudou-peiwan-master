package com.xydp.prevent;

import java.lang.annotation.*;

/**
 * 自定义防重提交注解
 *
 * @author Wuxy
 * @version 1.0
 * @ClassName RepeatSubmit
 * @since 2023/6/3 17:37
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface RepeatSubmit {

    /**
     * 防重提交，支持两种，一个是方法参数，一个是令牌
     */
    enum Type {PARAM, TOKEN}

    /**
     * 默认防重提交策略，方法参数
     *
     * @return 防重提交策略
     */
    Type limitType() default Type.PARAM;

    /**
     * 加锁过期时间，默认是 5 秒
     *
     * @return 过期时间
     */
    long lockTime() default 5;
}
