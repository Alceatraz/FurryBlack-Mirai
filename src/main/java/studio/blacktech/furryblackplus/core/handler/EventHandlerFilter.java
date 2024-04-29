package studio.blacktech.furryblackplus.core.handler;

import net.mamoe.mirai.event.events.GroupMessageEvent;
import net.mamoe.mirai.event.events.UserMessageEvent;
import studio.blacktech.furryblackplus.core.common.annotation.Comment;
import studio.blacktech.furryblackplus.core.handler.annotation.Filter;
import studio.blacktech.furryblackplus.core.handler.common.AbstractEventHandler;

@Comment(value = "过滤器父类", relativeClass = Filter.class)
public abstract class EventHandlerFilter extends AbstractEventHandler {

  @Comment("生命周期 过滤私聊消息")
  protected abstract boolean handleUsersMessage(UserMessageEvent event);

  @Comment("生命周期 过滤群聊消息")
  protected abstract boolean handleGroupMessage(GroupMessageEvent event);

  public boolean handleUsersMessageWrapper(UserMessageEvent event) {
    if (isEnable() && isReady()) {
      return handleUsersMessage(event);
    } else {
      return true;
    }
  }

  public boolean handleGroupMessageWrapper(GroupMessageEvent event) {
    if (isEnable() && isReady()) {
      return handleGroupMessage(event);
    } else {
      return true;
    }
  }
}
