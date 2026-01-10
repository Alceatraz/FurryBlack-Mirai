package top.btswork.furryblack.core.exception.system;

import top.btswork.furryblack.core.exception.KernelException;
import top.btswork.furryblack.core.common.annotation.Comment;

@Comment("控制台发生的异常")
public class TerminalException extends KernelException {

  public TerminalException() {}

  public TerminalException(String message) {
    super(message);
  }

  public TerminalException(String message, Throwable cause) {
    super(message, cause);
  }

  public TerminalException(Throwable cause) {
    super(cause);
  }
}
