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
public @interface Executor {

    @Api("模块名 全局唯一")
    String value();

    @Api("模块介绍")
    String description();

    @Api("模块注册的命令")
    String command();

    @Api("模块命令的用法")
    String[] usage();

    @Api("插件的隐私权限")
    String[] privacy() default {"不需要任何权限"};

    @Api("模块是否私聊可用")
    boolean users() default true;

    @Api("模块是否群聊可用")
    boolean group() default true;

}
