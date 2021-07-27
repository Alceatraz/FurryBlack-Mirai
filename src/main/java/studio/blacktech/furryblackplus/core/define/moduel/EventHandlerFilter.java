package studio.blacktech.furryblackplus.core.define.moduel;

import net.mamoe.mirai.event.events.GroupMessageEvent;
import net.mamoe.mirai.event.events.UserMessageEvent;
import studio.blacktech.furryblackplus.common.Api;


@Api("过滤器父类")
public abstract class EventHandlerFilter extends AbstractEventHandler {


    @Api("生命周期 过滤私聊消息")
    public abstract boolean handleUsersMessage(UserMessageEvent message);

    @Api("生命周期 过滤群聊消息")
    public abstract boolean handleGroupMessage(GroupMessageEvent message);


    public boolean handleUsersMessageWrapper(UserMessageEvent message) {
        return this.handleUsersMessage(message);

    }

    public boolean handleGroupMessageWrapper(GroupMessageEvent message) {
        return this.handleGroupMessage(message);
    }
}
