package studio.blacktech.furryblackplus.core.annotation;


import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


@Documented
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Runner {

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
     * @return 注册顺序 数字越小 越优先启动/最后关闭
     */
    int priority() default 100;
}
