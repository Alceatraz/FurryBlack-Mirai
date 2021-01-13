package studio.blacktech.furryblackplus.system.handler;


import net.mamoe.mirai.event.events.FriendMessageEvent;
import net.mamoe.mirai.event.events.GroupMessageEvent;
import net.mamoe.mirai.event.events.GroupTempMessageEvent;

public abstract class EventHandlerFilter extends AbstractEventHandler {


    public static class FilterInfo extends ModuleInfo {
        public FilterInfo(
                String MODULE_NAME,
                String MODULE_DESCRIPTION,
                String[] MODULE_PRIVACY
        ) {
            super(
                    MODULE_NAME,
                    MODULE_DESCRIPTION,
                    MODULE_PRIVACY
            );
        }
    }


    public final FilterInfo INFO;


    public EventHandlerFilter(FilterInfo INFO) {
        super(INFO);
        this.INFO = INFO;
    }


    public abstract boolean handleTempMessage(GroupTempMessageEvent message, String content);

    public abstract boolean handleFriendMessage(FriendMessageEvent message, String content);

    public abstract boolean handleGroupMessage(GroupMessageEvent message, String content);

}
