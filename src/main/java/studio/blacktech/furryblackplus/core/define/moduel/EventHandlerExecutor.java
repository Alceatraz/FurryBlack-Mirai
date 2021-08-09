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
import studio.blacktech.furryblackplus.core.define.annotation.Executor;
import studio.blacktech.furryblackplus.core.define.Command;


@Api("执行器父类")
public abstract class EventHandlerExecutor extends AbstractEventHandler {


    private String help;


    public String getHelp() {
        return this.help;
    }

    public void buildHelp(Executor executor) {
        if (this.help != null) {
            return;
        }
        StringBuilder builder = new StringBuilder();
        builder.append(executor.outline());
        builder.append(" ");
        builder.append(executor.command());
        builder.append("\r\n");
        builder.append(executor.description());
        builder.append("\r\n用法: \r\n");
        for (String temp : executor.usage()) {
            builder.append(temp);
            builder.append("\r\n");
        }
        builder.append("隐私: \r\n");
        for (String temp : executor.privacy()) {
            builder.append(temp);
            builder.append("\r\n");
        }
        this.help = builder.substring(0, builder.length() - 2);
    }

    @Api("生命周期 处理私聊命令")
    protected abstract void handleUsersMessage(UserMessageEvent event, Command command);

    @Api("生命周期 处理群聊命令")
    protected abstract void handleGroupMessage(GroupMessageEvent event, Command command);


    public void handleUsersMessageWrapper(UserMessageEvent event, Command command) {
        if (this.enable) this.handleUsersMessage(event, command);
    }

    public void handleGroupMessageWrapper(GroupMessageEvent event, Command command) {
        if (this.enable) this.handleGroupMessage(event, command);
    }
}
