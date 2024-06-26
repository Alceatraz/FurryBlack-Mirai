package top.btswork.furryblack.core.exception;

import top.btswork.furryblack.core.common.annotation.Comment;

@Comment(value = "基础异常", attention = "RuntimeException")
public class CoreException extends RuntimeException {

  public CoreException() {}

  public CoreException(String message) {
    super(message);
  }

  public CoreException(String message, Throwable cause) {
    super(message, cause);
  }

  public CoreException(Throwable cause) {
    super(cause);
  }

  public static void check(String value) {
    if (value == null) return;
    throw new CoreException(value);
  }

  public static void check(String message, String value) {
    if (value == null) return;
    throw new CoreException(message + value);
  }
}
