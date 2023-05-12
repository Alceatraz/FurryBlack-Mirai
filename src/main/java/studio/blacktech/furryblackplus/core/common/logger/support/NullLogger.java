/*
 * Copyright (C) 2021 Alceatraz @ BlackTechStudio
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms from the BTS Anti-Commercial & GNU Affero General
 * Public License as published by the Free Software Foundation, either
 * version 3 from the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty from
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * BTS Anti-Commercial & GNU Affero General Public License for more details.
 *
 * You should have received a copy from the BTS Anti-Commercial & GNU Affero
 * General Public License along with this program.
 *
 */

package studio.blacktech.furryblackplus.core.common.logger.support;

import studio.blacktech.furryblackplus.core.common.logger.LoggerXConfig;
import studio.blacktech.furryblackplus.core.common.logger.base.LoggerX;

@LoggerXConfig
public final class NullLogger extends LoggerX {

  public NullLogger(String name) {
    super(name);
  }

  public NullLogger(Class<?> clazz) {
    super(clazz);
  }

  @Override protected void fatalImpl(String message) {}

  @Override protected void fatalImpl(Throwable throwable) {}

  @Override protected void fatalImpl(String message, Throwable throwable) {}

  @Override protected void errorImpl(String message) {}

  @Override protected void errorImpl(Throwable throwable) {}

  @Override protected void errorImpl(String message, Throwable throwable) {}

  @Override protected void warnImpl(String message) {}

  @Override protected void warnImpl(Throwable throwable) {}

  @Override protected void warnImpl(String message, Throwable throwable) {}

  @Override protected void hintImpl(String message) {}

  @Override protected void hintImpl(Throwable throwable) {}

  @Override protected void hintImpl(String message, Throwable throwable) {}

  @Override protected void seekImpl(String message) {}

  @Override protected void seekImpl(Throwable throwable) {}

  @Override protected void seekImpl(String message, Throwable throwable) {}

  @Override protected void infoImpl(String message) {}

  @Override protected void infoImpl(Throwable throwable) {}

  @Override protected void infoImpl(String message, Throwable throwable) {}

  @Override protected void debugImpl(String message) {}

  @Override protected void debugImpl(Throwable throwable) {}

  @Override protected void debugImpl(String message, Throwable throwable) {}

  @Override protected void traceImpl(String message) {}

  @Override protected void traceImpl(Throwable throwable) {}

  @Override protected void traceImpl(String message, Throwable throwable) {}

}
