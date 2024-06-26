package top.btswork.furryblack.core.exception.moduels;

import top.btswork.furryblack.core.common.annotation.Comment;

@Comment("启动过程发生的异常")
public class ShutException extends ModuleException {

  public ShutException() {}

  public ShutException(String message) {
    super(message);
  }

  public ShutException(String message, Throwable cause) {
    super(message, cause);
  }

  public ShutException(Throwable cause) {
    super(cause);
  }

}
