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
import studio.blacktech.furryblackplus.common.Comment;
import studio.blacktech.furryblackplus.core.handler.EventHandlerFilter;
import studio.blacktech.furryblackplus.core.handler.annotation.Filter;

@Comment("示例过滤器")

@Filter(
  value = "demo-filter",
  priority = 100
)
public class DemoFilter extends EventHandlerFilter {

  @Override
  public void init() {
    FurryBlack.println("加载" + this.getClass().getName());
  }

  @Override
  public void boot() {
    FurryBlack.println("启动" + this.getClass().getName());
  }

  @Override
  public void shut() {
    FurryBlack.println("关闭" + this.getClass().getName());
  }

  @Override
  public boolean handleUsersMessage(UserMessageEvent event) {
    FurryBlack.println("消息" + this.getClass().getName());
    return false;
  }

  @Override
  public boolean handleGroupMessage(GroupMessageEvent event) {
    FurryBlack.println("消息" + this.getClass().getName());
    return false;
  }
}
