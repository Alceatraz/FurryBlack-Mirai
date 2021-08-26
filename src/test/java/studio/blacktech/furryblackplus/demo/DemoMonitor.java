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
import studio.blacktech.furryblackplus.Driver;
import studio.blacktech.furryblackplus.common.Api;
import studio.blacktech.furryblackplus.core.define.annotation.Monitor;
import studio.blacktech.furryblackplus.core.define.moduel.EventHandlerMonitor;


@Api("示例监视器 监视器")


@Monitor(
    value = "demo-monitor",
    users = false
)
public class DemoMonitor extends EventHandlerMonitor {


    @Override
    public void init() {
        System.out.println("加载" + this.getClass().getName());
        DemoRunner demoRunner = Driver.getRunner(DemoRunner.class);
        demoRunner.demo();
    }

    @Override
    public void boot() {
        System.out.println("启动" + this.getClass().getName());
    }

    @Override
    public void shut() {
        System.out.println("关闭" + this.getClass().getName());
    }

    @Override
    public void handleUsersMessage(UserMessageEvent event) {
        System.out.println("消息" + this.getClass().getName());
    }

    @Override
    public void handleGroupMessage(GroupMessageEvent event) {
        System.out.println("消息" + this.getClass().getName());
    }
}
