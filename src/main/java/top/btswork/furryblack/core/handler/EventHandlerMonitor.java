package top.btswork.furryblack.core.handler;

import net.mamoe.mirai.event.events.GroupMessageEvent;
import net.mamoe.mirai.event.events.UserMessageEvent;
import top.btswork.furryblack.core.handler.annotation.Monitor;
import top.btswork.furryblack.core.common.annotation.Comment;
import top.btswork.furryblack.core.handler.common.AbstractEventHandler;

@Comment(value = "监听器父类", relativeClass = Monitor.class)
public abstract class EventHandlerMonitor extends AbstractEventHandler {

  @Comment("生命周期 监听私聊消息")
  protected abstract void handleUsersMessage(UserMessageEvent event);

  @Comment("生命周期 监听群聊消息")
  protected abstract void handleGroupMessage(GroupMessageEvent event);

  public void handleUsersMessageWrapper(UserMessageEvent event) {
    if (isEnable() && isReady()) handleUsersMessage(event);
  }

  public void handleGroupMessageWrapper(GroupMessageEvent event) {
    if (isEnable() && isReady()) handleGroupMessage(event);
  }
}
