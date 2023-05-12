package studio.blacktech.furryblackplus.core.common.logger.slf4j;

import org.slf4j.ILoggerFactory;
import org.slf4j.Logger;

public class Slf4jLoggerXFactory implements ILoggerFactory {

  @Override
  public Logger getLogger(String name) {
    return new Slf4jLoggerX(name);
  }

}