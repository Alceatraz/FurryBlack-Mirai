package studio.blacktech.furryblackplus.core.interfaces;

import net.mamoe.mirai.event.events.GroupMessageEvent;
import net.mamoe.mirai.event.events.UserMessageEvent;

public abstract class EventHandlerFilter extends AbstractEventHandler {

    public final FilterInfo INFO;

    public EventHandlerFilter(FilterInfo INFO) {
        super(INFO);
        this.INFO = INFO;
    }

    public abstract boolean handleUsersMessage(UserMessageEvent message);

    public abstract boolean handleGroupMessage(GroupMessageEvent message);

    public static class FilterInfo extends ModuleInfo {
        public FilterInfo(String MODULE_NAME, String MODULE_ARTIFICIAL, String MODULE_DESCRIPTION, String[] MODULE_PRIVACY) {
            super(MODULE_NAME, MODULE_ARTIFICIAL, MODULE_DESCRIPTION, MODULE_PRIVACY);
        }
    }
}
