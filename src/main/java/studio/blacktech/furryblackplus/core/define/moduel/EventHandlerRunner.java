/*
 * Copyright (C) 2021 Alceatraz @ BlackTechStudio
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the BTS Anti Commercial & GNU Affero General.

 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * BTS Anti Commercial & GNU Affero General Public License for more details.
 *
 * You should have received a copy of the BTS Anti Commercial & GNU Affero
 * General Public License along with this program in README or LICENSE.
 */

package studio.blacktech.furryblackplus.core.define.moduel;


import studio.blacktech.furryblackplus.common.Api;
import studio.blacktech.furryblackplus.core.exception.BotException;


@Api("定时器父类")
public abstract class EventHandlerRunner extends AbstractEventHandler {

    private volatile boolean lock;

    @SuppressWarnings("deprecation")
    @Deprecated
    @Override
    public void internalInit(String name) {
        if (this.lock) {
            throw new BotException("Illegal access due to try invoke internalInit twice");
        }
        this.lock = true;
        super.internalInit(name);
    }
}
