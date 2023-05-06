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

package studio.blacktech.furryblackplus.core.handler;

import net.mamoe.mirai.event.events.GroupMessageEvent;
import net.mamoe.mirai.event.events.UserMessageEvent;
import studio.blacktech.furryblackplus.common.Comment;
import studio.blacktech.furryblackplus.core.handler.annotation.Monitor;
import studio.blacktech.furryblackplus.core.handler.common.AbstractEventHandler;

@Comment(value = "监听器父类", relativeClass = Monitor.class)
public abstract class EventHandlerMonitor extends AbstractEventHandler {

  @Comment("生命周期 监听私聊消息")
  protected abstract void handleUsersMessage(UserMessageEvent event);

  @Comment("生命周期 监听群聊消息")
  protected abstract void handleGroupMessage(GroupMessageEvent event);

  public void handleUsersMessageWrapper(UserMessageEvent event) {
    handleUsersMessage(event);
  }

  public void handleGroupMessageWrapper(GroupMessageEvent event) {
    handleGroupMessage(event);
  }
}
