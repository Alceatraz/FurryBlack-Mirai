package studio.blacktech.furryblackplus.core.interfaces;

import net.mamoe.mirai.event.events.GroupMessageEvent;
import net.mamoe.mirai.event.events.UserMessageEvent;
import studio.blacktech.furryblackplus.core.annotation.Api;


@Api("监听器父类")
public abstract class EventHandlerMonitor extends AbstractEventHandler {

    public final MonitorInfo INFO;

    protected EventHandlerMonitor(MonitorInfo info) {
        super(info.ARTIFICIAL);
        this.INFO = info;
    }

    @Api("生命周期 监听私聊消息")
    public abstract void handleUsersMessage(UserMessageEvent message);

    @Api("生命周期 监听群聊消息")
    public abstract void handleGroupMessage(GroupMessageEvent message);

    public final static class MonitorInfo extends ModuleInfo {
        public MonitorInfo(String name, String artificial, String description, String[] privacy) {
            super(name, artificial, description, privacy);
        }
    }
}
