package top.btswork.furryblack.core.common.enhance;

import top.btswork.furryblack.FurryBlack;
import top.btswork.furryblack.core.common.annotation.Comment;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Locale;

public class StringEnhance {

  @Comment("打印完整调用栈")
  public static String extractStackTrace(Throwable throwable) {
    if (throwable == null) return "";
    var stringWriter = new StringWriter();
    var printWriter = new PrintWriter(stringWriter);
    throwable.printStackTrace(printWriter);
    printWriter.close();
    return stringWriter.toString().trim();
  }

  @Comment("将hash转换为人类有好格式")
  public static String toHumanHashCode(Object object) {
    return Integer.toHexString(object.hashCode()).toUpperCase(Locale.ROOT);
  }

  @Comment("将二进制转换为人类友好 -h")
  public static String toHumanBytes(long value) {
    // @formatter:off
    if (value > 1024 * 1024 * 1024) return value / (1024 * 1024 * 1024F) + "GB";
    else if (value > 1024 * 1024)        return value / (1024 * 1024F) + "MB";
    else if (value > 1024)               return value / 1024F + "kB";
    else                                 return value + "B";
    // @formatter:on
  }

  public static class LineBuilder {

    private final StringBuilder builder = new StringBuilder();

    public LineBuilder append(Object object) {
      builder.append(object).append(FurryBlack.LINE);
      return this;
    }

    public LineBuilder append(Object... object) {
      for (Object o : object) {
        builder.append(o);
      }
      builder.append(FurryBlack.LINE);
      return this;
    }

    public LineBuilder format(String format, Object... object) {
      builder.append(String.format(format, object)).append(FurryBlack.LINE);
      return this;
    }

    @Override public String toString() {
      return builder.toString();
    }
  }

}
