package studio.blacktech.furryblackplus.system.annotation;


import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


@Documented
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface ComponentHandlerExecutor {


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
