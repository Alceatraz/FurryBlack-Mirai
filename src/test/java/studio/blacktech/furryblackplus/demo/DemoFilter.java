package studio.blacktech.furryblackplus.demo;

import net.mamoe.mirai.event.events.GroupMessageEvent;
import net.mamoe.mirai.event.events.UserMessageEvent;
import studio.blacktech.furryblackplus.core.annotation.Filter;
import studio.blacktech.furryblackplus.core.define.moduel.EventHandlerFilter;


@Filter(
    value = "demo-filter",
    priority = 100
)
public class DemoFilter extends EventHandlerFilter {


    @Override
    public void init() {
        System.out.println("加载" + this.getClass().getName());
    }

    @Override
    public void boot() {
        System.out.println("启动" + this.getClass().getName());
    }

    @Override
    public void shut() {
        System.out.println("关闭" + this.getClass().getName());
    }

    @Override
    public boolean handleUsersMessage(UserMessageEvent event) {
        System.out.println("消息" + this.getClass().getName());
        return false;
    }

    @Override
    public boolean handleGroupMessage(GroupMessageEvent event) {
        System.out.println("消息" + this.getClass().getName());
        return false;
    }
}
