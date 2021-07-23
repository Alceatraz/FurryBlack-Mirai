package studio.blacktech.furryblackplus.core.interfaces;

import net.mamoe.mirai.event.events.GroupMessageEvent;
import net.mamoe.mirai.event.events.UserMessageEvent;
import studio.blacktech.furryblackplus.common.Api;


@Api("监听器父类")
public abstract class EventHandlerMonitor extends AbstractEventHandler {


    @Api("生命周期 监听私聊消息")
    public abstract void handleUsersMessage(UserMessageEvent message);

    @Api("生命周期 监听群聊消息")
    public abstract void handleGroupMessage(GroupMessageEvent message);


    public void handleUsersMessageWrapper(UserMessageEvent message) {
        try {
            this.readWriteLock.readLock().lock();
            if (this.enable) {
                this.handleUsersMessage(message);
            }
        } finally {
            this.readWriteLock.readLock().unlock();
        }
    }

    public void handleGroupMessageWrapper(GroupMessageEvent message) {
        try {
            this.readWriteLock.readLock().lock();
            if (this.enable) {
                this.handleGroupMessage(message);
            }
        } finally {
            this.readWriteLock.readLock().unlock();
        }
    }
}
