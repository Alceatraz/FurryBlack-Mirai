package studio.blacktech.furryblackplus.core.exception.moduels;

import studio.blacktech.furryblackplus.core.common.annotation.Comment;

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
