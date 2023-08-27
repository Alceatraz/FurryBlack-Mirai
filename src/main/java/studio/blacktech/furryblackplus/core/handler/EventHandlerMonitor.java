package studio.blacktech.furryblackplus.core.handler;

import net.mamoe.mirai.event.events.GroupMessageEvent;
import net.mamoe.mirai.event.events.UserMessageEvent;
import studio.blacktech.furryblackplus.core.common.annotation.Comment;
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
