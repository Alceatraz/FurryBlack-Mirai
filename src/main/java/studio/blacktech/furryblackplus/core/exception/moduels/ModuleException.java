package studio.blacktech.furryblackplus.core.exception.moduels;

import studio.blacktech.furryblackplus.core.common.annotation.Comment;

@Comment("模块子系统相关的异常")
public class ModuleException extends Exception {

  public ModuleException() {}

  public ModuleException(String message) {
    super(message);
  }

  public ModuleException(String message, Throwable cause) {
    super(message, cause);
  }

  public ModuleException(Throwable cause) {
    super(cause);
  }

}
