package studio.blacktech.furryblackplus.core.logging;

import org.slf4j.ILoggerFactory;
import org.slf4j.IMarkerFactory;
import org.slf4j.spi.MDCAdapter;
import org.slf4j.spi.SLF4JServiceProvider;

public class Slf4jLoggerXServiceProvider implements SLF4JServiceProvider {

  private final Slf4jLoggerXFactory loggerFactory = new Slf4jLoggerXFactory();

  @Override
  public void initialize() {

  }

  @Override
  public String getRequestedApiVersion() {
    return "1.0.0";
  }

  @Override
  public ILoggerFactory getLoggerFactory() {
    return loggerFactory;
  }

  @Override
  public IMarkerFactory getMarkerFactory() {
    return null;
  }

  @Override
  public MDCAdapter getMDCAdapter() {
    return null;
  }

}