package studio.blacktech.furryblackplus.core.handler;

import net.mamoe.mirai.event.events.GroupMessageEvent;
import net.mamoe.mirai.event.events.UserMessageEvent;
import studio.blacktech.furryblackplus.core.common.annotation.Comment;
import studio.blacktech.furryblackplus.core.handler.annotation.Executor;
import studio.blacktech.furryblackplus.core.handler.common.AbstractEventHandler;
import studio.blacktech.furryblackplus.core.handler.common.Command;

@Comment(value = "执行器父类", relativeClass = Executor.class)
public abstract class EventHandlerExecutor extends AbstractEventHandler {

  private String help;

  public String getHelp() {
    return help;
  }

  public void buildHelp(Executor executor) {
    if (help != null) {
      return;
    }
    StringBuilder builder = new StringBuilder();
    builder.append(executor.outline());
    builder.append(" ");
    builder.append(executor.command());
    builder.append("\r\n");
    builder.append(executor.description());
    builder.append("\r\n用法: \r\n");
    for (String temp : executor.usage()) {
      builder.append(temp);
      builder.append("\r\n");
    }
    builder.append("隐私: \r\n");
    for (String temp : executor.privacy()) {
      builder.append(temp);
      builder.append("\r\n");
    }
    this.help = builder.substring(0, builder.length() - 2);
  }

  @Comment("生命周期 处理私聊命令")
  protected abstract void handleUsersMessage(UserMessageEvent event, Command command);

  @Comment("生命周期 处理群聊命令")
  protected abstract void handleGroupMessage(GroupMessageEvent event, Command command);

  public void handleUsersMessageWrapper(UserMessageEvent event, Command command) {
    if (enable) handleUsersMessage(event, command);
  }

  public void handleGroupMessageWrapper(GroupMessageEvent event, Command command) {
    if (enable) handleGroupMessage(event, command);
  }
}
