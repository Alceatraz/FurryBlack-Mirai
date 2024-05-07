package top.btswork.furryblack.core.handler;

import net.mamoe.mirai.event.events.GroupMessageEvent;
import net.mamoe.mirai.event.events.UserMessageEvent;
import top.btswork.furryblack.core.handler.annotation.Checker;
import top.btswork.furryblack.core.common.annotation.Comment;
import top.btswork.furryblack.core.handler.common.AbstractEventHandler;
import top.btswork.furryblack.core.handler.common.Command;

@Comment(value = "检查器父类", relativeClass = Checker.class)
public abstract class EventHandlerChecker extends AbstractEventHandler {

  @Comment("生命周期 检查私聊命令")
  protected abstract boolean handleUsersMessage(UserMessageEvent event, Command command);

  @Comment("生命周期 检查群聊命令")
  protected abstract boolean handleGroupMessage(GroupMessageEvent event, Command command);

  public boolean handleUsersMessageWrapper(UserMessageEvent event, Command command) {
    if (isEnable() && isReady()) {
      return handleUsersMessage(event, command);
    } else {
      return true;
    }
  }

  public boolean handleGroupMessageWrapper(GroupMessageEvent event, Command command) {
    if (isEnable() && isReady()) {
      return handleGroupMessage(event, command);
    } else {
      return true;
    }
  }
}
