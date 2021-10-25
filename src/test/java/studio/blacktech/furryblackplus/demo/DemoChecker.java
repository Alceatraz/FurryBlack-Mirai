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
import studio.blacktech.furryblackplus.core.handler.common.Command;
import studio.blacktech.furryblackplus.core.handler.annotation.Checker;
import studio.blacktech.furryblackplus.core.handler.EventHandlerChecker;


@Api("示例检查器")


@Checker(
    value = "demo-checker",
    command = "demo"
)
public class DemoChecker extends EventHandlerChecker {


    private DemoRunner runner;


    @Override
    public void init() {
        // 使用getRunner从IoC容器获取定时器实例
        this.runner = FurryBlack.getRunner(DemoRunner.class);
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
    public boolean handleUsersMessage(UserMessageEvent event, Command command) {
        FurryBlack.terminalPrintLine("消息" + this.getClass().getName());
        return false;
    }

    @Override
    public boolean handleGroupMessage(GroupMessageEvent event, Command command) {
        FurryBlack.terminalPrintLine("消息" + this.getClass().getName());
        return this.runner.checkPermission(event.getSender().getId(), "demo.command.demo");

    }
}
