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

import net.mamoe.mirai.event.events.GroupMessageEvent;
import net.mamoe.mirai.event.events.UserMessageEvent;
import studio.blacktech.furryblackplus.common.Api;
import studio.blacktech.furryblackplus.core.define.Command;


@Api("检查器父类")
public abstract class EventHandlerChecker extends AbstractEventHandler {


    @Api("生命周期 检查私聊命令")
    protected abstract boolean handleUsersMessage(UserMessageEvent event, Command command);

    @Api("生命周期 检查群聊命令")
    protected abstract boolean handleGroupMessage(GroupMessageEvent event, Command command);


    public boolean handleUsersMessageWrapper(UserMessageEvent event, Command command) {
        return this.handleUsersMessage(event, command);
    }

    public boolean handleGroupMessageWrapper(GroupMessageEvent event, Command command) {
        return this.handleGroupMessage(event, command);
    }
}
