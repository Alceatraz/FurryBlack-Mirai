package studio.blacktech.furryblackplus.core.logging.backend;

import studio.blacktech.furryblackplus.core.logging.annotation.LoggerXConfig;
import studio.blacktech.furryblackplus.core.logging.backend.wrapper.WrappedLoggerX;

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

  @Override protected void fatalImpl(Throwable throwable) {

  }

  @Override protected void errorImpl(String message) {

  }

  @Override protected void errorImpl(Throwable throwable) {

  }

  @Override protected void warnImpl(String message) {

  }

  @Override protected void warnImpl(Throwable throwable) {

  }

  @Override protected void hintImpl(String message) {

  }

  @Override protected void hintImpl(Throwable throwable) {

  }

  @Override protected void infoImpl(String message) {

  }

  @Override protected void infoImpl(Throwable throwable) {

  }

  @Override protected void seekImpl(String message) {

  }

  @Override protected void seekImpl(Throwable throwable) {

  }

  @Override protected void debugImpl(String message) {

  }

  @Override protected void debugImpl(Throwable throwable) {  }

  @Override protected void traceImpl(String message) {  }

  @Override protected void traceImpl(Throwable throwable) {  }
}
