package studio.blacktech.furryblackplus.core.exception.system;

import studio.blacktech.furryblackplus.core.common.annotation.Comment;
import studio.blacktech.furryblackplus.core.exception.schema.SchemaException;

@Comment("初次启动,缺少必要的配置项目")
public class FirstBootException extends SchemaException {

  public FirstBootException() {}

  public FirstBootException(String message) {
    super(message);
  }

  public FirstBootException(String message, Throwable cause) {
    super(message, cause);
  }

  public FirstBootException(Throwable cause) {
    super(cause);
  }

  public static String require(String value, String... name) {
    if (value != null) return value;
    throw new FirstBootException("Required field " + String.join(".", name) + " not set");
  }
}
