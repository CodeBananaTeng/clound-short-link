package com.yulin.annotation;

import java.lang.annotation.*;

/**
 * 自定义防重提交
 * @Auther:LinuxTYL
 * @Date:2022/2/8
 * @Description:
 */

@Documented
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface RepeatSubmit {

    /**
     * 防重提交支持两种的形式1，方法参数2，令牌
     */
    enum Type{PARAM,TOKEN}

    /**
     * 默认使用方法参数做一个防重提交
     * @return
     */
    Type limitType() default Type.PARAM;

    /**
     * 加锁过期时间默认为5s
     * @return
     */
    long lockTime() default 5;

}
