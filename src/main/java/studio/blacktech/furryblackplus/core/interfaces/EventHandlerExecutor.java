package studio.blacktech.furryblackplus.core.interfaces;

import net.mamoe.mirai.event.events.GroupMessageEvent;
import net.mamoe.mirai.event.events.UserMessageEvent;
import studio.blacktech.furryblackplus.common.Api;
import studio.blacktech.furryblackplus.core.utilties.Command;


@Api("执行器父类")
public abstract class EventHandlerExecutor extends AbstractEventHandler {

    private final String HELP;

    public EventHandlerExecutor() {
        super();
        if (this.annotation.command().isBlank()) throw new IllegalArgumentException("无效的模块命令`command`");
        StringBuilder builder = new StringBuilder();
        builder.append(this.annotation.command());
        builder.append(" ");
        builder.append(this.annotation.name());
        builder.append("\r\n");
        builder.append(this.annotation.description());
        builder.append("\r\n命令用法: \r\n");
        for (String temp : this.annotation.usage()) {
            builder.append(temp);
            builder.append("\r\n");
        }
        builder.append("隐私: \r\n");
        for (String temp : this.annotation.privacy()) {
            builder.append(temp);
            builder.append("\r\n");
        }
        this.HELP = builder.toString();
    }

    @Api("生命周期 处理私聊命令")
    public abstract void handleUsersMessage(UserMessageEvent event, Command command);

    @Api("生命周期 处理群聊命令")
    public abstract void handleGroupMessage(GroupMessageEvent event, Command command);


    public void handleUsersMessageWrapper(UserMessageEvent event, Command command) {
        if (this.enable) this.handleUsersMessage(event, command);
    }

    public void handleGroupMessageWrapper(GroupMessageEvent event, Command command) {
        if (this.enable) this.handleGroupMessage(event, command);
    }

    public String getHelpMessage() {
        return this.HELP;
    }
}
