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
import studio.blacktech.furryblackplus.core.handler.EventHandlerChecker;
import studio.blacktech.furryblackplus.core.handler.annotation.Checker;
import studio.blacktech.furryblackplus.core.handler.common.Command;

import java.util.concurrent.ThreadLocalRandom;

@Comment("示例检查器")

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
    logger.info("加载" + this.getClass().getName());
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
  public boolean handleUsersMessage(UserMessageEvent event, Command command) {
    logger.info("消息" + this.getClass().getName());
    return ThreadLocalRandom.current().nextInt() == 42;
  }

  @Override
  public boolean handleGroupMessage(GroupMessageEvent event, Command command) {
    logger.info("消息" + this.getClass().getName());
    return this.runner.checkPermission(event.getSender().getId(), "demo.command.demo");
  }
}
