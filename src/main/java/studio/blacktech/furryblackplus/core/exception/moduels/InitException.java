package studio.blacktech.furryblackplus.core.exception.moduels;

import studio.blacktech.furryblackplus.core.common.annotation.Comment;

@Comment("启动过程发生的异常")
public class InitException extends ModuleException {

  public InitException() {}

  public InitException(String message) {
    super(message);
  }

  public InitException(String message, Throwable cause) {
    super(message, cause);
  }

  public InitException(Throwable cause) {
    super(cause);
  }

}
