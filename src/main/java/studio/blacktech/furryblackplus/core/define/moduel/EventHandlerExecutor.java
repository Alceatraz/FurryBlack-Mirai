package studio.blacktech.furryblackplus.core.define.moduel;

import net.mamoe.mirai.event.events.GroupMessageEvent;
import net.mamoe.mirai.event.events.UserMessageEvent;
import studio.blacktech.furryblackplus.common.Api;
import studio.blacktech.furryblackplus.core.utilties.Command;


@Api("执行器父类")
public abstract class EventHandlerExecutor extends AbstractEventHandler {


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
}
