package studio.blacktech.furryblackplus.core.annotation;


import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


@SuppressWarnings("unused")

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

    String[] usage() default {};

    String[] attention() default {};

    Class<?>[] relativeClass() default {};

}
