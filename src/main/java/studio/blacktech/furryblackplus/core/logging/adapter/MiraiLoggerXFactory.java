package studio.blacktech.furryblackplus.core.logging.adapter;

import net.mamoe.mirai.utils.MiraiLogger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class MiraiLoggerXFactory implements MiraiLogger.Factory {

  @NotNull
  @Override
  public MiraiLogger create(@NotNull Class<?> clazz, @Nullable String name) {
    return new MiraiLoggerX(clazz, name);
  }
}
