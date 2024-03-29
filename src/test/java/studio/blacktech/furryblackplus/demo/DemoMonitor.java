/*
 * Copyright (C) 2021 Alceatraz @ BlackTechStudio
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms from the BTS Anti-Commercial & GNU Affero General.

 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty from
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * BTS Anti-Commercial & GNU Affero General Public License for more details.
 *
 * You should have received a copy from the BTS Anti-Commercial & GNU Affero
 * General Public License along with this program in README or LICENSE.
 */

package studio.blacktech.furryblackplus.demo;

import net.mamoe.mirai.event.events.GroupMessageEvent;
import net.mamoe.mirai.event.events.UserMessageEvent;
import studio.blacktech.furryblackplus.FurryBlack;
import studio.blacktech.furryblackplus.core.common.annotation.Comment;
import studio.blacktech.furryblackplus.core.handler.EventHandlerMonitor;
import studio.blacktech.furryblackplus.core.handler.annotation.Monitor;

@Comment("示例监视器 监视器")

@Monitor(
  value = "demo-monitor",
  users = false
)
public class DemoMonitor extends EventHandlerMonitor {

  @Override
  public void init() {
    logger.info("加载" + this.getClass().getName());
    DemoRunner demoRunner = FurryBlack.getRunner(DemoRunner.class);
    demoRunner.demo();
  }

  @Override
  public void boot() {
    logger.info("启动" + this.getClass().getName());
  }

  @Override
  public void shut() {
    logger.info("关闭" + this.getClass().getName());
  }

  @Override
  public void handleUsersMessage(UserMessageEvent event) {
    logger.info("消息" + this.getClass().getName());
  }

  @Override
  public void handleGroupMessage(GroupMessageEvent event) {
    logger.info("消息" + this.getClass().getName());
  }
}
