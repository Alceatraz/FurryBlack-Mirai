package top.btswork.furryblack.core.common.enhance;

import top.btswork.furryblack.core.common.annotation.Comment;

import java.util.function.Supplier;

@Comment("基础工具")
public final class Enhance {

  public static <T> T takeIfNull(T value, T defaultValue) {
    return value == null ? defaultValue : value;
  }

  public static <T> T takeIfNull(T value, Supplier<T> defaultValue) {
    return value == null ? defaultValue.get() : value;
  }
}
