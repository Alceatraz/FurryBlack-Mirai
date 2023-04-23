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

package studio.blacktech.furryblackplus.core.common.exception.moduels.load;

import studio.blacktech.furryblackplus.common.Api;
import studio.blacktech.furryblackplus.core.common.exception.ModuleException;

@SuppressWarnings("unused")

@Api("启动过程发生的异常")
public class LoadException extends ModuleException {

  public LoadException() {

  }

  public LoadException(String message) {
    super(message);
  }

  public LoadException(String message, Throwable cause) {
    super(message, cause);
  }

  public LoadException(Throwable cause) {
    super(cause);
  }

}
