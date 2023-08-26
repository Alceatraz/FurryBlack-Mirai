package studio.blacktech.furryblackplus.core.logging.backend;

import studio.blacktech.furryblackplus.core.logging.WrappedLoggerX;
import studio.blacktech.furryblackplus.core.logging.annotation.LoggerXConfig;

@LoggerXConfig
public class NullLogger extends WrappedLoggerX {

  protected NullLogger(Class<?> clazz) {
    super(clazz);
  }

  protected NullLogger(String simpleName) {
    super(simpleName);
  }


  @Override protected void fatalImpl(String message) {

  }

  @Override protected void errorImpl(String message) {

  }

  @Override protected void warnImpl(String message) {

  }

  @Override protected void hintImpl(String message) {

  }

  @Override protected void infoImpl(String message) {

  }

  @Override protected void seekImpl(String message) {

  }

  @Override protected void debugImpl(String message) {

  }

  @Override protected void traceImpl(String message) {

  }
}
