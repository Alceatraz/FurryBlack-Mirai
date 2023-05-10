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

package studio.blacktech.furryblackplus.core.exception;

import studio.blacktech.furryblackplus.core.common.annotation.Comment;

@Comment(value = "基础异常", attention = "RuntimeException")
public class CoreException extends RuntimeException {

  public CoreException() {}

  public CoreException(String message) {
    super(message);
  }

  public CoreException(String message, Throwable cause) {
    super(message, cause);
  }

  public CoreException(Throwable cause) {
    super(cause);
  }

  public static void check(String value) {
    if (value == null) return;
    throw new CoreException(value);
  }

  public static void check(String message, String value) {
    if (value == null) return;
    throw new CoreException(message + value);
  }
}
