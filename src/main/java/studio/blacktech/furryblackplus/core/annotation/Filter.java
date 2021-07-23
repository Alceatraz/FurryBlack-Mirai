package studio.blacktech.furryblackplus.core.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


@Documented
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Filter {

    /**
     * @return 插件的名字 用于控制台和唯一标记
     */
    String value();

    /**
     * 模块权重 数字越小越:
     *
     * 优先 实例化/加载/启动/执行
     * 最后 关闭
     *
     * @return 注册顺序
     */
    int priority() default Integer.MAX_VALUE;


    /**
     * @return 对私聊启用 默认启用
     */
    boolean users() default true;

    /**
     * @return 对群聊启用 默认启用
     */
    boolean group() default true;
}
