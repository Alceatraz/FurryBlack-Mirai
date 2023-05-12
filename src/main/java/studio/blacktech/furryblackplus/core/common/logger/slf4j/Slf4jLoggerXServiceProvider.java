package studio.blacktech.furryblackplus.core.common.logger.slf4j;

import org.slf4j.ILoggerFactory;
import org.slf4j.IMarkerFactory;
import org.slf4j.spi.MDCAdapter;
import org.slf4j.spi.SLF4JServiceProvider;

public class Slf4jLoggerXServiceProvider implements SLF4JServiceProvider {

  private final Slf4jLoggerXFactory loggerFactory = new Slf4jLoggerXFactory();
  private final Slf4jLoggerXMarkerFactory markerFactory = new Slf4jLoggerXMarkerFactory();

  @Override public ILoggerFactory getLoggerFactory() {
    return loggerFactory;
  }

  @Override public IMarkerFactory getMarkerFactory() {
    return markerFactory;
  }

  @Override public MDCAdapter getMDCAdapter() {
    return null;
  }

  @Override public String getRequestedApiVersion() {
    return "1.0.0";
  }

  @Override public void initialize() {

  }
}