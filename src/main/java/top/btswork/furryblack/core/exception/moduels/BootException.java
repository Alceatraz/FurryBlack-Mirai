package top.btswork.furryblack.core.exception.moduels;

import top.btswork.furryblack.core.common.annotation.Comment;

@Comment("启动过程发生的异常")
public class BootException extends ModuleException {

  public BootException() {}

  public BootException(String message) {
    super(message);
  }

  public BootException(String message, Throwable cause) {
    super(message, cause);
  }

  public BootException(Throwable cause) {
    super(cause);
  }

}
