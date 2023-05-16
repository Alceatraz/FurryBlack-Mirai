/*
 * Copyright (C) 2021 Alceatraz @ BlackTechStudio
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms from the BTS Anti-Commercial & GNU Affero General.

 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty from
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * BTS Anti-Commercial & GNU Affero General Public License for more details.
 *
 * You should have received a copy from the BTS Anti-Commercial & GNU Affero
 * General Public License along with this program in README or LICENSE.
 */

package studio.blacktech.furryblackplus.core.exception.system;

import studio.blacktech.furryblackplus.core.common.annotation.Comment;
import studio.blacktech.furryblackplus.core.exception.schema.SchemaException;

@Comment("初次启动,缺少必要的配置项目")
public class FirstBootException extends SchemaException {

  public FirstBootException() {}

  public FirstBootException(String message) {
    super(message);
  }

  public FirstBootException(String message, Throwable cause) {
    super(message, cause);
  }

  public FirstBootException(Throwable cause) {
    super(cause);
  }

  public static String require(String value, String... name) {
    if (value != null) return value;
    throw new FirstBootException("Required field " + String.join(".", name) + " not set");
  }
}
