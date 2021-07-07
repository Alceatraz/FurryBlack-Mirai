package studio.blacktech.furryblackplus.core.interfaces;

import net.mamoe.mirai.event.events.GroupMessageEvent;
import net.mamoe.mirai.event.events.UserMessageEvent;
import studio.blacktech.furryblackplus.common.Api;
import studio.blacktech.furryblackplus.core.annotation.Component;


@Api("过滤器父类")
public abstract class EventHandlerFilter extends AbstractEventHandler {

    public final FilterInfo INFO;

    protected EventHandlerFilter(FilterInfo info) {
        super(info.ARTIFICIAL);
        this.INFO = info;
    }

    @Api("生命周期 过滤私聊消息")
    public abstract boolean handleUsersMessage(UserMessageEvent message);

    @Api("生命周期 过滤群聊消息")
    public abstract boolean handleGroupMessage(GroupMessageEvent message);

    public static final class FilterInfo extends ModuleInfo {

        public FilterInfo(Component annotation) {
            this(annotation.name(), annotation.artificial(), annotation.description(), annotation.privacy());
        }

        public FilterInfo(String name, String artificial, String description, String[] privacy) {
            super(name, artificial, description, privacy);
        }
    }
}
