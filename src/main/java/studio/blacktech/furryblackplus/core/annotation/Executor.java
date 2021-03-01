package studio.blacktech.furryblackplus.core.annotation;


import studio.blacktech.furryblackplus.demo.DemoExecutor;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


@Documented
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Api(value = "具体使用方法请见示例", see = DemoExecutor.class)
public @interface Executor {


    /**
     * @return 插件的名字 用于控制台
     */
    String artificial() default "";


    /**
     * @return 插件的名字
     */
    String name() default "";


    /**
     * @return 插件的简介
     */
    String description() default "";


    /**
     * @return 插件的隐私
     */
    String[] privacy() default {};


    /**
     * @return 插件的命令
     */
    String command() default "";


    /**
     * @return 插件的用法
     */
    String[] usage() default {};


    /**
     * @return 对私聊启用 默认启用
     */
    boolean users() default true;


    /**
     * @return 对群聊启用 默认启用
     */
    boolean group() default true;

}
