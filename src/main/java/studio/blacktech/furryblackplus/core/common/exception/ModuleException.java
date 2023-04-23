/*
 * Copyright (C) 2021 Alceatraz @ BlackTechStudio
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the BTS Anti-Commercial & GNU Affero General.

 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * BTS Anti-Commercial & GNU Affero General Public License for more details.
 *
 * You should have received a copy of the BTS Anti-Commercial & GNU Affero
 * General Public License along with this program in README or LICENSE.
 */

package studio.blacktech.furryblackplus.core.common.exception;

import studio.blacktech.furryblackplus.common.Api;

@SuppressWarnings("unused")

@Api("模块相关的异常")
public class ModuleException extends BotException {

  public ModuleException() {

  }

  public ModuleException(String message) {
    super(message);
  }

  public ModuleException(String message, Throwable cause) {
    super(message, cause);
  }

  public ModuleException(Throwable cause) {
    super(cause);
  }

}
