package studio.blacktech.furryblackplus.core.common.logger.slf4j;

import org.slf4j.IMarkerFactory;
import org.slf4j.Marker;

public class Slf4jLoggerXMarkerFactory implements IMarkerFactory {

  @Override public Marker getMarker(String name) {
    return null;
  }

  @Override public boolean exists(String name) {
    return false;
  }

  @Override public boolean detachMarker(String name) {
    return false;
  }

  @Override public Marker getDetachedMarker(String name) {
    return null;
  }
}