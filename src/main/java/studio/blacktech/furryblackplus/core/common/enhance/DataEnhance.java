package studio.blacktech.furryblackplus.core.common.enhance;

import org.jetbrains.annotations.Nullable;
import studio.blacktech.furryblackplus.core.common.annotation.Comment;

import java.util.Locale;
import java.util.function.Supplier;

@Comment("数值工具")
public class DataEnhance {

  //= ==================================================================================================================
  //= Byte

  @Comment("数值转换")
  public static byte parseByte(String value, byte defaultValue) {
    if (value == null) return defaultValue;
    try {
      return Byte.parseByte(value);
    } catch (NumberFormatException exception) {
      return defaultValue;
    }
  }

  @Comment("数值转换")
  public static <T extends Throwable> byte parseByte(String value, Supplier<T> throwableSupplier) throws T {
    if (value == null) {
      throw throwableSupplier.get();
    }
    try {
      return Byte.parseByte(value);
    } catch (NumberFormatException exception) {
      T throwable = throwableSupplier.get();
      throwable.addSuppressed(exception);
      throw throwable;
    }
  }

  @Comment("数值转换")
  @Nullable
  public static Byte parseByteOrNull(String value) {
    if (value == null) return null;
    try {
      return Byte.parseByte(value);
    } catch (NumberFormatException exception) {
      return null;
    }
  }

  @Comment("数值转换")
  @Nullable
  public static <T extends Throwable> Byte parseByteOrNull(String value, Supplier<T> throwableSupplier) throws T {
    if (value == null) return null;
    try {
      return Byte.parseByte(value);
    } catch (NumberFormatException exception) {
      T throwable = throwableSupplier.get();
      throwable.addSuppressed(exception);
      throw throwable;
    }
  }

  //= ==================================================================================================================
  //= Byte

  @Comment("数值转换")
  public static short parseShort(String value, short defaultValue) {
    if (value == null) return defaultValue;
    try {
      return Short.parseShort(value);
    } catch (NumberFormatException exception) {
      return defaultValue;
    }
  }

  @Comment("数值转换")
  public static <T extends Throwable> short parseShort(String value, Supplier<T> throwableSupplier) throws T {
    if (value == null) {
      throw throwableSupplier.get();
    }
    try {
      return Short.parseShort(value);
    } catch (NumberFormatException exception) {
      T throwable = throwableSupplier.get();
      throwable.addSuppressed(exception);
      throw throwable;
    }
  }

  @Comment("数值转换")
  @Nullable
  public static Short parseShortOrNull(String value) {
    if (value == null) return null;
    try {
      return Short.parseShort(value);
    } catch (NumberFormatException exception) {
      return null;
    }
  }

  @Comment("数值转换")
  @Nullable
  public static <T extends Throwable> Short parseShortOrNull(String value, Supplier<T> throwableSupplier) throws T {
    if (value == null) return null;
    try {
      return Short.parseShort(value);
    } catch (NumberFormatException exception) {
      T throwable = throwableSupplier.get();
      throwable.addSuppressed(exception);
      throw throwable;
    }
  }

  //= ==================================================================================================================
  //= Int

  @Comment("数值转换")
  public static int parseInt(String value, int defaultValue) {
    if (value == null) return defaultValue;
    try {
      return Integer.parseInt(value);
    } catch (NumberFormatException exception) {
      return defaultValue;
    }
  }

  @Comment("数值转换")
  public static <T extends Throwable> int parseInt(String value, Supplier<T> throwableSupplier) throws T {
    if (value == null) {
      throw throwableSupplier.get();
    }
    try {
      return Integer.parseInt(value);
    } catch (NumberFormatException exception) {
      T throwable = throwableSupplier.get();
      throwable.addSuppressed(exception);
      throw throwable;
    }
  }

  @Comment("数值转换")
  @Nullable
  public static Integer parseIntOrNull(String value) {
    if (value == null) return null;
    try {
      return Integer.parseInt(value);
    } catch (NumberFormatException exception) {
      return null;
    }
  }

  @Comment("数值转换")
  @Nullable
  public static <T extends Throwable> Integer parseIntOrNull(String value, Supplier<T> throwableSupplier) throws T {
    if (value == null) return null;
    try {
      return Integer.parseInt(value);
    } catch (NumberFormatException exception) {
      T throwable = throwableSupplier.get();
      throwable.addSuppressed(exception);
      throw throwable;
    }
  }

  //= ====================================================================================================================
  //= Long

  @Comment("数值转换")
  public static long parseLong(String value, long defaultValue) {
    if (value == null) return defaultValue;
    try {
      return Long.parseLong(value);
    } catch (NumberFormatException exception) {
      return defaultValue;
    }
  }

  @Comment("数值转换")
  public static <T extends Throwable> long parseLong(String value, Supplier<T> throwableSupplier) throws T {
    if (value == null) {
      throw throwableSupplier.get();
    }
    try {
      return Long.parseLong(value);
    } catch (NumberFormatException exception) {
      T throwable = throwableSupplier.get();
      throwable.addSuppressed(exception);
      throw throwable;
    }
  }

  @Comment("数值转换")
  @Nullable
  public static Long parseLongOrNull(String value) {
    if (value == null) return null;
    try {
      return Long.parseLong(value);
    } catch (NumberFormatException exception) {
      return null;
    }
  }

  @Comment("数值转换")
  @Nullable
  public static <T extends Throwable> Long parseLongOrNull(String value, Supplier<T> throwableSupplier) throws T {
    if (value == null) return null;
    try {
      return Long.parseLong(value);
    } catch (NumberFormatException exception) {
      T throwable = throwableSupplier.get();
      throwable.addSuppressed(exception);
      throw throwable;
    }
  }

  //= ====================================================================================================================
  //= Boolean

  @Comment("数值转换")
  public static Boolean parseBoolean(String value, boolean defaultValue) {
    return switch (value.toUpperCase(Locale.ROOT)) {
      case "T", "TRUE" -> true;
      case "F", "FALSE" -> false;
      default -> defaultValue;
    };
  }

  @Comment("数值转换")
  @Nullable
  public static Boolean parseBooleanOrNull(String value) {
    return switch (value.toUpperCase(Locale.ROOT)) {
      case "T", "TRUE" -> true;
      case "F", "FALSE" -> false;
      default -> null;
    };
  }

}
