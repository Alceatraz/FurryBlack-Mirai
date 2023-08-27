package studio.blacktech.furryblackplus.core.exception.system;

import studio.blacktech.furryblackplus.core.common.annotation.Comment;
import studio.blacktech.furryblackplus.core.exception.CoreException;

@Comment("控制台发生的异常")
public class TerminalException extends CoreException {

  public TerminalException() {

  }

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
