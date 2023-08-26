package studio.blacktech.furryblackplus.core.logging.adapter;

import kotlin.reflect.KClass;
import net.mamoe.mirai.utils.MiraiLogger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class MiraiLoggerXFactory implements MiraiLogger.Factory {

  public MiraiLoggerXFactory() {
    System.out.println("[FurryBlack][LOGS] MiraiLoggerXFactory loaded");
  }

  @NotNull
  @Override
  public MiraiLogger create(@NotNull Class<?> clazz, @Nullable String identity) {
    return new MiraiLoggerX(clazz, identity);
  }

  @NotNull
  @Override
  public MiraiLogger create(@NotNull Class<?> clazz) {
    return new MiraiLoggerX(clazz, null);
  }

  @NotNull
  @Override
  public MiraiLogger create(@NotNull KClass<?> clazz) {
    return new MiraiLoggerX(clazz.getClass(), null);
  }

  @NotNull
  @Override
  public MiraiLogger create(@NotNull KClass<?> clazz, @Nullable String identity) {
    return new MiraiLoggerX(clazz.getClass(), identity);
  }
}
