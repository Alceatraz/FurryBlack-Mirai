package studio.blacktech.furryblackplus.core.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


@Documented
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Component {

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
     * @return 注册顺序 数字越大 越先启动/后关闭 对Executor无效
     */
    int priority() default 100;


    /**
     * @return 对私聊启用 默认启用 对Runner无效
     */
    boolean users() default true;

    /**
     * @return 对群聊启用 默认启用 对Runner无效
     */
    boolean group() default true;


    /**
     * @return 插件的命令 只有Executor有效
     */
    String command() default "";

    /**
     * @return 插件的用法 只有Executor有效
     */
    String[] usage() default {};
}