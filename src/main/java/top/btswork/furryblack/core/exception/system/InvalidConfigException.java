package top.btswork.furryblack.core.exception.system;

import top.btswork.furryblack.core.exception.schema.SchemaException;

public class InvalidConfigException extends SchemaException {

  public InvalidConfigException() {

  }

  public InvalidConfigException(String message) {
    super(message);
  }

  public InvalidConfigException(String message, Throwable cause) {
    super(message, cause);
  }

  public InvalidConfigException(Throwable cause) {
    super(cause);
  }

  public static String require(String value, String... name) {
    if (value != null) return value;
    throw new InvalidConfigException("Required field " + String.join(".", name) + " not set");
  }

}
