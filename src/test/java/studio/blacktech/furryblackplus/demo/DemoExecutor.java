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

package studio.blacktech.furryblackplus.demo;

import net.mamoe.mirai.event.events.GroupMessageEvent;
import net.mamoe.mirai.event.events.UserMessageEvent;
import studio.blacktech.furryblackplus.FurryBlack;
import studio.blacktech.furryblackplus.common.Api;
import studio.blacktech.furryblackplus.core.handler.EventHandlerExecutor;
import studio.blacktech.furryblackplus.core.handler.annotation.Executor;
import studio.blacktech.furryblackplus.core.handler.common.Command;


@Api("示例执行器")


@Executor(
    value = "demo-executor",
    outline = "示例",
    description = "示例执行器",
    command = "demo",
    usage = "/demo - 示例执行器",
    privacy = "获取命令执行人"
)
public class DemoExecutor extends EventHandlerExecutor {


    @Override
    public void init() {
        FurryBlack.terminalPrintLine("加载" + this.getClass().getName());
    }

    @Override
    public void boot() {
        FurryBlack.terminalPrintLine("启动" + this.getClass().getName());
    }

    @Override
    public void shut() {
        FurryBlack.terminalPrintLine("关闭" + this.getClass().getName());
    }

    @Override
    public void handleUsersMessage(UserMessageEvent event, Command command) {
        FurryBlack.terminalPrintLine("消息" + this.getClass().getName());
    }

    @Override
    public void handleGroupMessage(GroupMessageEvent event, Command command) {
        FurryBlack.terminalPrintLine("消息" + this.getClass().getName());
    }
}
