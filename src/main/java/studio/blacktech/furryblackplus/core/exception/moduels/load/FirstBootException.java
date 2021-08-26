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

package studio.blacktech.furryblackplus.core.exception.moduels.load;


import studio.blacktech.furryblackplus.common.Api;


@SuppressWarnings("unused")


@Api("初次启动 不能以默认值运行时 打断启动过程")
public class FirstBootException extends LoadException {

    public FirstBootException() {

    }

    public FirstBootException(String message) {
        super(message);
    }

    public FirstBootException(String message, Throwable cause) {
        super(message, cause);
    }

    public FirstBootException(Throwable cause) {
        super(cause);
    }

}
