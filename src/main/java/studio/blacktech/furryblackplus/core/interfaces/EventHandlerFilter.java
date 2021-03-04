package studio.blacktech.furryblackplus.core.interfaces;

import net.mamoe.mirai.event.events.GroupMessageEvent;
import net.mamoe.mirai.event.events.UserMessageEvent;
import studio.blacktech.furryblackplus.core.annotation.Api;
import studio.blacktech.furryblackplus.demo.DemoFilter;


@Api(value = "具体使用方法请见示例", see = DemoFilter.class)
public abstract class EventHandlerFilter extends AbstractEventHandler {

    public final FilterInfo INFO;

    public EventHandlerFilter(FilterInfo info) {
        super(info);
        this.INFO = info;
    }

    @Api("生命周期 过滤私聊消息")
    public abstract boolean handleUsersMessage(UserMessageEvent message);

    @Api("生命周期 过滤群聊消息")
    public abstract boolean handleGroupMessage(GroupMessageEvent message);

    public static class FilterInfo extends ModuleInfo {
        public FilterInfo(String name, String artificial, String description, String[] privacy) {
            super(name, artificial, description, privacy);
        }
    }
}
