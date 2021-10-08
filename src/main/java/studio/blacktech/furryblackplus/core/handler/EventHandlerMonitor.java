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


package studio.blacktech.furryblackplus.core.handler;


import net.mamoe.mirai.event.events.GroupMessageEvent;
import net.mamoe.mirai.event.events.UserMessageEvent;
import studio.blacktech.furryblackplus.common.Api;
import studio.blacktech.furryblackplus.core.handler.common.AbstractEventHandler;


@Api("监听器父类")
public abstract class EventHandlerMonitor extends AbstractEventHandler {


    @Api("生命周期 监听私聊消息")
    protected abstract void handleUsersMessage(UserMessageEvent event);

    @Api("生命周期 监听群聊消息")
    protected abstract void handleGroupMessage(GroupMessageEvent event);


    public void handleUsersMessageWrapper(UserMessageEvent event) {
        this.handleUsersMessage(event);
    }

    public void handleGroupMessageWrapper(GroupMessageEvent event) {
        this.handleGroupMessage(event);
    }
}
