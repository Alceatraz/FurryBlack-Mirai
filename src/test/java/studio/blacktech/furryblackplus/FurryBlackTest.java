package studio.blacktech.furryblackplus;

import net.mamoe.mirai.utils.MiraiLogger;
import org.junit.jupiter.api.Test;
import org.slf4j.spi.SLF4JServiceProvider;

import java.util.ServiceLoader;

public class FurryBlackTest {

  @Test
  public void test1() {
    System.err.println("org.slf4j.spi.SLF4JServiceProvider");
    ServiceLoader<SLF4JServiceProvider> providers = ServiceLoader.load(SLF4JServiceProvider.class);
    providers.stream().map(it -> it.get().getClass().getName()).forEach(System.out::println);
  }

  @Test
  public void test2() {
    System.err.println("net.mamoe.mirai.utils.MiraiLogger$Factory");
    ServiceLoader<MiraiLogger.Factory> providers = ServiceLoader.load(MiraiLogger.Factory.class);
    providers.stream().map(it -> it.get().getClass().getName()).forEach(System.out::println);
  }
}