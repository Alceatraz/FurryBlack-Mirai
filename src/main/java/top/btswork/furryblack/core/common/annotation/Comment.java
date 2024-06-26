package top.btswork.furryblack.core.common.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

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
public @interface Comment {

  String value();

  String[] usage() default {};

  String[] attention() default {};

  Class<?>[] relativeClass() default {};

}
