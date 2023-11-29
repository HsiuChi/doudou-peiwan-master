package com.ddpw.prevent;

import org.aspectj.lang.JoinPoint;

import java.lang.annotation.*;

/**
 * 接口防刷注解（控制相同 ip 和用户在规定时间内的最大访问次数。具体实现见{@link PreventAop#joinPoint(JoinPoint)}）
 *
 * @author Wuxy
 * @version 1.0
 * @ClassName Prevent
 * @see PreventAop
 * @since 2023/1/13 16:30
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Prevent {

    /**
     * 限制的时间间隔（秒）
     */
    long value() default 5;

    /**
     * 最大访问次数
     */
    int count() default 5;

    /**
     * 提示信息
     */
    String message() default "";

}
