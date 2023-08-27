package studio.blacktech.furryblackplus.core.handler;

import net.mamoe.mirai.event.events.GroupMessageEvent;
import net.mamoe.mirai.event.events.UserMessageEvent;
import studio.blacktech.furryblackplus.core.common.annotation.Comment;
import studio.blacktech.furryblackplus.core.handler.annotation.Checker;
import studio.blacktech.furryblackplus.core.handler.common.AbstractEventHandler;
import studio.blacktech.furryblackplus.core.handler.common.Command;

@Comment(value = "检查器父类", relativeClass = Checker.class)
public abstract class EventHandlerChecker extends AbstractEventHandler {

  @Comment("生命周期 检查私聊命令")
  protected abstract boolean handleUsersMessage(UserMessageEvent event, Command command);

  @Comment("生命周期 检查群聊命令")
  protected abstract boolean handleGroupMessage(GroupMessageEvent event, Command command);

  public boolean handleUsersMessageWrapper(UserMessageEvent event, Command command) {
    return handleUsersMessage(event, command);
  }

  public boolean handleGroupMessageWrapper(GroupMessageEvent event, Command command) {
    return handleGroupMessage(event, command);
  }
}
