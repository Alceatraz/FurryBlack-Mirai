package studio.blacktech.furryblackplus.core.annotation;


import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


/**
 * 提供API的没有被使用过的方法会被标记为Unused警告
 * 使用IDEA的 Ignore with Annotation 解决警告问题 同时写下方法的用途
 */
@Documented
@Target({
    ElementType.TYPE,
    ElementType.FIELD,
    ElementType.METHOD,
    ElementType.PARAMETER,
    ElementType.CONSTRUCTOR,
    ElementType.LOCAL_VARIABLE,
    ElementType.ANNOTATION_TYPE,
    ElementType.PACKAGE,
    ElementType.TYPE_PARAMETER,
    ElementType.TYPE_USE,
    ElementType.MODULE
})
@Retention(RetentionPolicy.RUNTIME)
public @interface Api {

    String value() default "";

}
