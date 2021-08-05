/*
 * Copyright (C) 2021 Alceatraz @ BlackTechStudio
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 *
 */

package studio.blacktech.furryblackplus.core.exception.moduels.load;


import studio.blacktech.furryblackplus.common.Api;


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
