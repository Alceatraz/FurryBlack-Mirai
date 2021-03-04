package studio.blacktech.furryblackplus.core.interfaces;

import net.mamoe.mirai.event.events.GroupMessageEvent;
import net.mamoe.mirai.event.events.UserMessageEvent;
import studio.blacktech.furryblackplus.core.annotation.Api;
import studio.blacktech.furryblackplus.demo.DemoMonitor;


@Api(value = "具体使用方法请见示例", see = DemoMonitor.class)
public abstract class EventHandlerMonitor extends AbstractEventHandler {

    public final MonitorInfo INFO;

    public EventHandlerMonitor(MonitorInfo info) {
        super(info);
        this.INFO = info;
    }

    @Api("生命周期 监听私聊消息")
    public abstract void handleUsersMessage(UserMessageEvent message);

    @Api("生命周期 监听群聊消息")
    public abstract void handleGroupMessage(GroupMessageEvent message);

    public static class MonitorInfo extends ModuleInfo {
        public MonitorInfo(String name, String artificial, String description, String[] privacy) {
            super(name, artificial, description, privacy);
        }
    }
}
