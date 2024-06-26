package top.btswork.furryblack.core.logging.backend.wrapper;

import top.btswork.furryblack.FurryBlack;
import top.btswork.furryblack.core.common.enhance.StringEnhance;
import top.btswork.furryblack.core.logging.LoggerX;

import java.util.Arrays;
import java.util.Iterator;

public abstract class PlaceholdLoggerX extends LoggerX {

  protected PlaceholdLoggerX(Class<?> clazz) {
    super(clazz);
  }

  protected PlaceholdLoggerX(String simpleName) {
    super(simpleName);
  }

  @Override protected void fatalImpl(Throwable throwable) {
    fatalImpl(StringEnhance.extractStackTrace(throwable));
  }

  @Override protected void fatalImpl(String message, Throwable throwable) {
    if (throwable == null) {
      fatalImpl(message);
    } else if (message == null) {
      fatalImpl(StringEnhance.extractStackTrace(throwable));
    } else {
      fatalImpl(message + " -> " + StringEnhance.extractStackTrace(throwable));
    }
  }

  @Override protected void fatalImpl(String messagePattern, Object... objects) {
    if (objects == null || objects.length == 0) {
      fatalImpl(messagePattern);
    } else {
      fatalImpl(placeholder(messagePattern, objects));
    }
  }

  @Override protected void errorImpl(Throwable throwable) {
    errorImpl(StringEnhance.extractStackTrace(throwable));
  }

  @Override protected void errorImpl(String message, Throwable throwable) {
    if (throwable == null) {
      errorImpl(message);
    } else if (message == null) {
      errorImpl(StringEnhance.extractStackTrace(throwable));
    } else {
      errorImpl(message + " -> " + StringEnhance.extractStackTrace(throwable));
    }
  }

  @Override protected void errorImpl(String messagePattern, Object... objects) {
    if (objects == null || objects.length == 0) {
      errorImpl(messagePattern);
    } else {
      errorImpl(placeholder(messagePattern, objects));
    }
  }

  @Override protected void warnImpl(Throwable throwable) {
    warnImpl(StringEnhance.extractStackTrace(throwable));
  }

  @Override protected void warnImpl(String message, Throwable throwable) {
    if (throwable == null) {
      warnImpl(message);
    } else if (message == null) {
      warnImpl(StringEnhance.extractStackTrace(throwable));
    } else {
      warnImpl(message + " -> " + StringEnhance.extractStackTrace(throwable));
    }
  }

  @Override protected void warnImpl(String messagePattern, Object... objects) {
    if (objects == null || objects.length == 0) {
      warnImpl(messagePattern);
    } else {
      warnImpl(placeholder(messagePattern, objects));
    }
  }

  @Override protected void hintImpl(Throwable throwable) {
    hintImpl(StringEnhance.extractStackTrace(throwable));
  }

  @Override protected void hintImpl(String message, Throwable throwable) {
    if (throwable == null) {
      hintImpl(message);
    } else if (message == null) {
      hintImpl(StringEnhance.extractStackTrace(throwable));
    } else {
      hintImpl(message + " -> " + StringEnhance.extractStackTrace(throwable));
    }
  }

  @Override protected void hintImpl(String messagePattern, Object... objects) {
    if (objects == null || objects.length == 0) {
      hintImpl(messagePattern);
    } else {
      hintImpl(placeholder(messagePattern, objects));
    }
  }

  @Override protected void seekImpl(Throwable throwable) {
    seekImpl(StringEnhance.extractStackTrace(throwable));
  }

  @Override protected void seekImpl(String message, Throwable throwable) {
    if (throwable == null) {
      seekImpl(message);
    } else if (message == null) {
      seekImpl(StringEnhance.extractStackTrace(throwable));
    } else {
      seekImpl(message + " -> " + StringEnhance.extractStackTrace(throwable));
    }
  }

  @Override protected void seekImpl(String messagePattern, Object... objects) {
    if (objects == null || objects.length == 0) {
      seekImpl(messagePattern);
    } else {
      seekImpl(placeholder(messagePattern, objects));
    }
  }

  @Override protected void infoImpl(Throwable throwable) {
    infoImpl(StringEnhance.extractStackTrace(throwable));
  }

  @Override protected void infoImpl(String message, Throwable throwable) {
    if (throwable == null) {
      infoImpl(message);
    } else if (message == null) {
      infoImpl(StringEnhance.extractStackTrace(throwable));
    } else {
      infoImpl(message + " -> " + StringEnhance.extractStackTrace(throwable));
    }
  }

  @Override protected void infoImpl(String messagePattern, Object... objects) {
    if (objects == null || objects.length == 0) {
      infoImpl(messagePattern);
    } else {
      infoImpl(placeholder(messagePattern, objects));
    }
  }

  @Override protected void debugImpl(Throwable throwable) {
    debugImpl(StringEnhance.extractStackTrace(throwable));
  }

  @Override protected void debugImpl(String message, Throwable throwable) {
    if (throwable == null) {
      debugImpl(message);
    } else if (message == null) {
      debugImpl(StringEnhance.extractStackTrace(throwable));
    } else {
      debugImpl(message + " -> " + StringEnhance.extractStackTrace(throwable));
    }
  }

  @Override protected void debugImpl(String messagePattern, Object... objects) {
    if (objects == null || objects.length == 0) {
      debugImpl(messagePattern);
    } else {
      debugImpl(placeholder(messagePattern, objects));
    }
  }

  @Override protected void traceImpl(Throwable throwable) {
    traceImpl(StringEnhance.extractStackTrace(throwable));
  }

  @Override protected void traceImpl(String message, Throwable throwable) {
    if (throwable == null) {
      traceImpl(message);
    } else if (message == null) {
      traceImpl(StringEnhance.extractStackTrace(throwable));
    } else {
      traceImpl(message + " -> " + StringEnhance.extractStackTrace(throwable));
    }
  }

  @Override protected void traceImpl(String messagePattern, Object... objects) {
    if (objects == null || objects.length == 0) {
      traceImpl(messagePattern);
    } else {
      traceImpl(placeholder(messagePattern, objects));
    }
  }

  //= ==================================================================================================================
  //=
  //= ==================================================================================================================

  private static String placeholder(String pattern, Object... objects) {
    StringBuilder builder = new StringBuilder();
    Iterator<Object> iterator = Arrays.stream(objects).iterator();
    boolean escape = false;
    boolean placeholder = false;
    char[] charArray = pattern.toCharArray();
    for (int i = 0; i < charArray.length; i++) {
      char chat = charArray[i];
      switch (chat) {
        case '\\' -> {
          if (escape) {
            escape = false;
            builder.append('\\');
          } else {
            escape = true;
          }
        }
        case '{' -> {
          if (escape) {
            escape = false;
            builder.append('{');
          } else {
            char next = charArray[i + 1];
            if (next == '}') {
              placeholder = true;
            } else {
              builder.append('{');
            }
          }
        }
        default -> {
          if (placeholder) {
            escape = false;
            placeholder = false;
            if (iterator.hasNext()) {
              Object object = iterator.next();
              if (object == null) {
                builder.append("{null}");
              } else if (object instanceof Throwable throwable) {
                builder.append(throwable.getClass().getName());
                builder.append(": ");
                builder.append(throwable.getMessage());
              } else {
                builder.append(object);
              }
            } else {
              builder.append("{0}");
            }
          } else {
            builder.append(chat);
          }
        }
      }
    }

    while (iterator.hasNext()) {
      Object object = iterator.next();
      builder.append(FurryBlack.LINE);
      if (object == null) {
        builder.append("{null}");
      } else if (object instanceof Throwable throwable) {
        builder.append(StringEnhance.extractStackTrace(throwable));
      } else {
        builder.append(object);
      }
    }
    return builder.toString();
  }
}
