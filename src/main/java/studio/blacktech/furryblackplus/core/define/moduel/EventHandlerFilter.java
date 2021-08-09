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


@Api("过滤器父类")
public abstract class EventHandlerFilter extends AbstractEventHandler {


    @Api("生命周期 过滤私聊消息")
    protected abstract boolean handleUsersMessage(UserMessageEvent event);

    @Api("生命周期 过滤群聊消息")
    protected abstract boolean handleGroupMessage(GroupMessageEvent event);


    public boolean handleUsersMessageWrapper(UserMessageEvent event) {
        return this.handleUsersMessage(event);
    }

    public boolean handleGroupMessageWrapper(GroupMessageEvent event) {
        return this.handleGroupMessage(event);
    }
}
