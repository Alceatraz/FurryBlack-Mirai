package top.btswork.furryblack.core.exception;

import top.btswork.furryblack.core.common.annotation.Comment;

@Comment(value = "基础异常", attention = "RuntimeException")
public class KernelException extends RuntimeException {

  public KernelException() {}

  public KernelException(String message) {
    super(message);
  }

  public KernelException(String message, Throwable cause) {
    super(message, cause);
  }

  public KernelException(Throwable cause) {
    super(cause);
  }

  public static void check(String value) {
    if (value == null) return;
    throw new KernelException(value);
  }

  public static void check(String message, String value) {
    if (value == null) return;
    throw new KernelException(message + value);
  }
}
