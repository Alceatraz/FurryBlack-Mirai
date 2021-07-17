package studio.blacktech.furryblackplus.demo;

import net.mamoe.mirai.event.events.GroupMessageEvent;
import net.mamoe.mirai.event.events.UserMessageEvent;
import studio.blacktech.furryblackplus.Driver;
import studio.blacktech.furryblackplus.core.annotation.Component;
import studio.blacktech.furryblackplus.core.interfaces.EventHandlerMonitor;


@Component(
    artificial = "Monitor_Demo",
    name = "示例",
    description = "示例监视器",
    privacy = {
        "无"
    }
)
public class DemoMonitor extends EventHandlerMonitor {


    @Override
    public void init() {
        System.out.println("加载" + this.getClass().getName());
        DemoRunner demoRunner = Driver.getRunner(DemoRunner.class);
        demoRunner.demo();
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
    public void handleUsersMessage(UserMessageEvent event) {
        System.out.println("消息" + this.getClass().getName());
    }

    @Override
    public void handleGroupMessage(GroupMessageEvent event) {
        System.out.println("消息" + this.getClass().getName());
    }
}
