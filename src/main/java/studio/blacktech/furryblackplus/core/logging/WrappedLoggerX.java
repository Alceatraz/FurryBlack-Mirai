package studio.blacktech.furryblackplus.core.logging;

import static studio.blacktech.furryblackplus.FurryBlack.LINE;
import static studio.blacktech.furryblackplus.core.common.enhance.StringEnhance.extractStackTrace;

public abstract class WrappedLoggerX extends LoggerX {

  protected WrappedLoggerX(Class<?> clazz) {
    super(clazz);
  }

  protected WrappedLoggerX(String simpleName) {
    super(simpleName);
  }

  @Override protected void fatalImpl(String message, Throwable throwable) {
    if (throwable == null) {
      fatalImpl(message);
    } else if (message == null) {
      fatalImpl(extractStackTrace(throwable));
    } else {
      fatalImpl(message + LINE + extractStackTrace(throwable));
    }
  }

  @Override protected void fatalImpl(String messagePattern, Object... objects) {
    if (objects == null || objects.length == 0) {
      fatalImpl(messagePattern);
    } else {
      fatalImpl(inject(messagePattern, objects));
    }
  }

  @Override protected void errorImpl(String message, Throwable throwable) {
    if (throwable == null) {
      errorImpl(message);
    } else if (message == null) {
      errorImpl(extractStackTrace(throwable));
    } else {
      errorImpl(message + LINE + extractStackTrace(throwable));
    }
  }

  @Override protected void errorImpl(String messagePattern, Object... objects) {
    if (objects == null || objects.length == 0) {
      errorImpl(messagePattern);
    } else {
      errorImpl(inject(messagePattern, objects));
    }
  }

  @Override protected void warnImpl(String message, Throwable throwable) {
    if (throwable == null) {
      warnImpl(message);
    } else if (message == null) {
      warnImpl(extractStackTrace(throwable));
    } else {
      warnImpl(message + LINE + extractStackTrace(throwable));
    }
  }

  @Override protected void warnImpl(String messagePattern, Object... objects) {
    if (objects == null || objects.length == 0) {
      warnImpl(messagePattern);
    } else {
      warnImpl(inject(messagePattern, objects));
    }
  }

  @Override protected void hintImpl(String message, Throwable throwable) {
    if (throwable == null) {
      hintImpl(message);
    } else if (message == null) {
      hintImpl(extractStackTrace(throwable));
    } else {
      hintImpl(message + LINE + extractStackTrace(throwable));
    }
  }

  @Override protected void hintImpl(String messagePattern, Object... objects) {
    if (objects == null || objects.length == 0) {
      hintImpl(messagePattern);
    } else {
      hintImpl(inject(messagePattern, objects));
    }
  }

  @Override protected void seekImpl(String message, Throwable throwable) {
    if (throwable == null) {
      seekImpl(message);
    } else if (message == null) {
      seekImpl(extractStackTrace(throwable));
    } else {
      seekImpl(message + LINE + extractStackTrace(throwable));
    }
  }

  @Override protected void seekImpl(String messagePattern, Object... objects) {
    if (objects == null || objects.length == 0) {
      seekImpl(messagePattern);
    } else {
      seekImpl(inject(messagePattern, objects));
    }
  }

  @Override protected void infoImpl(String message, Throwable throwable) {
    if (throwable == null) {
      infoImpl(message);
    } else if (message == null) {
      infoImpl(extractStackTrace(throwable));
    } else {
      infoImpl(message + LINE + extractStackTrace(throwable));
    }
  }

  @Override protected void infoImpl(String messagePattern, Object... objects) {
    if (objects == null || objects.length == 0) {
      infoImpl(messagePattern);
    } else {
      infoImpl(inject(messagePattern, objects));
    }
  }

  @Override protected void debugImpl(String message, Throwable throwable) {
    if (throwable == null) {
      debugImpl(message);
    } else if (message == null) {
      debugImpl(extractStackTrace(throwable));
    } else {
      debugImpl(message + LINE + extractStackTrace(throwable));
    }
  }

  @Override protected void debugImpl(String messagePattern, Object... objects) {
    if (objects == null || objects.length == 0) {
      debugImpl(messagePattern);
    } else {
      debugImpl(inject(messagePattern, objects));
    }
  }

  @Override protected void traceImpl(String message, Throwable throwable) {
    if (throwable == null) {
      traceImpl(message);
    } else if (message == null) {
      traceImpl(extractStackTrace(throwable));
    } else {
      traceImpl(message + LINE + extractStackTrace(throwable));
    }
  }

  @Override protected void traceImpl(String messagePattern, Object... objects) {
    if (objects == null || objects.length == 0) {
      traceImpl(messagePattern);
    } else {
      traceImpl(inject(messagePattern, objects));
    }
  }
}
