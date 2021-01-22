package studio.blacktech.furryblackplus.core.interfaces;

import net.mamoe.mirai.event.events.GroupMessageEvent;
import net.mamoe.mirai.event.events.UserMessageEvent;

public abstract class EventHandlerMonitor extends AbstractEventHandler {

    public final MonitorInfo INFO;

    public EventHandlerMonitor(MonitorInfo INFO) {
        super(INFO);
        this.INFO = INFO;
    }

    public abstract void handleUsersMessage(UserMessageEvent message);

    public abstract void handleGroupMessage(GroupMessageEvent message);

    public static class MonitorInfo extends ModuleInfo {
        public MonitorInfo(String MODULE_NAME, String MODULE_ARTIFICIAL, String MODULE_DESCRIPTION, String[] MODULE_PRIVACY) {
            super(MODULE_NAME, MODULE_ARTIFICIAL, MODULE_DESCRIPTION, MODULE_PRIVACY);
        }
    }
}
