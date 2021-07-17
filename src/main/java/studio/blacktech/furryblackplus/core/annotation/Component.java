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
     * @return 插件的名字 用于控制台和唯一标记
     */
    String artificial();

    /**
     * @return 插件的名字 用于人类友好显示
     */
    String name() default "无名称";

    /**
     * @return 插件的简介
     */
    String description() default "无介绍";

    /**
     * @return 插件的隐私
     */
    String[] privacy() default {"无介绍"};


    /**
     * 模块权重 数字越小越:
     *
     * 优先 实例化/加载/启动/执行
     * 最后 关闭
     *
     * @return 注册顺序
     */
    int priority() default 0;


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
    String[] usage() default {"无介绍"};


}
