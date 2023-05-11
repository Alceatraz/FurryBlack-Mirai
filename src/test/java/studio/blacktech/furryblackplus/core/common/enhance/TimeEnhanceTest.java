package studio.blacktech.furryblackplus.core.common.enhance;

import org.junit.jupiter.api.Test;

class TimeEnhanceTest {

  @Test
  public void test1() {
    long l = TimeEnhance.toNextDay();
    System.out.println(l);
  }

}