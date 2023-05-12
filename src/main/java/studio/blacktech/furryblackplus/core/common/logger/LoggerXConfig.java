package studio.blacktech.furryblackplus.core.common.logger;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Documented
@Target({
  ElementType.TYPE
})
@Retention(RetentionPolicy.RUNTIME)
public @interface LoggerXConfig {

  boolean needLoggerFile() default false;

}