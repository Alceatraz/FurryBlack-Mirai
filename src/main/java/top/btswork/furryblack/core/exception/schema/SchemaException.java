package top.btswork.furryblack.core.exception.schema;

import top.btswork.furryblack.core.common.annotation.Comment;

@Comment("模块子系统相关的异常")
public class SchemaException extends RuntimeException {

  public SchemaException() {}

  public SchemaException(String message) {
    super(message);
  }

  public SchemaException(String message, Throwable cause) {
    super(message, cause);
  }

  public SchemaException(Throwable cause) {
    super(cause);
  }

}
