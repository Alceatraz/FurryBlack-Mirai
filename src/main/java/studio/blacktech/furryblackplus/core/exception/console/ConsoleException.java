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

package studio.blacktech.furryblackplus.core.exception.console;

import studio.blacktech.furryblackplus.common.Api;
import studio.blacktech.furryblackplus.core.exception.BotException;


@SuppressWarnings("unused")


@Api("控制台发生的异常")
public class ConsoleException extends BotException {


    public ConsoleException() {

    }

    public ConsoleException(String message) {
        super(message);
    }

    public ConsoleException(String message, Throwable cause) {
        super(message, cause);
    }

    public ConsoleException(Throwable cause) {
        super(cause);
    }
}
