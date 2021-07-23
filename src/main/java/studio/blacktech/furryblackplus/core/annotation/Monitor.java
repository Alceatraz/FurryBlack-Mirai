package studio.blacktech.furryblackplus.core.annotation;

import studio.blacktech.furryblackplus.common.Api;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


@Documented
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Monitor {

    @Api("模块名")
    String value();

    @Api("模块权重")
    int priority() default Integer.MAX_VALUE;

    @Api("对私聊启用 默认启用")
    boolean users() default true;

    @Api("对群聊启用 默认启用")
    boolean group() default true;
}
