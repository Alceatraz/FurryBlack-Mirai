package top.btswork.furryblack.core.logging;

import org.slf4j.ILoggerFactory;
import org.slf4j.IMarkerFactory;
import org.slf4j.helpers.BasicMDCAdapter;
import org.slf4j.helpers.BasicMarkerFactory;
import org.slf4j.spi.MDCAdapter;
import org.slf4j.spi.SLF4JServiceProvider;

public class Slf4jLoggerXServiceProvider implements SLF4JServiceProvider {

  private final BasicMDCAdapter basicMDCAdapter = new BasicMDCAdapter();
  private final Slf4jLoggerXFactory loggerFactory = new Slf4jLoggerXFactory();
  private final BasicMarkerFactory basicMarkerFactory = new BasicMarkerFactory();

  @Override
  public void initialize() {
    loggerFactory.initialize();
  }

  @Override
  public String getRequestedApiVersion() {
    return "2.0.0";
  }

  @Override
  public ILoggerFactory getLoggerFactory() {
    return loggerFactory;
  }

  @Override
  public IMarkerFactory getMarkerFactory() {
    return basicMarkerFactory;
  }

  @Override
  public MDCAdapter getMDCAdapter() {
    return basicMDCAdapter;
  }

}