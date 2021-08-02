package studio.blacktech.furryblackplus.demo;

import net.mamoe.mirai.event.events.GroupMessageEvent;
import net.mamoe.mirai.event.events.UserMessageEvent;
import studio.blacktech.furryblackplus.core.define.annotation.Executor;
import studio.blacktech.furryblackplus.core.define.Command;
import studio.blacktech.furryblackplus.core.define.moduel.EventHandlerExecutor;


@Executor(value = "demo-executor", outline = "示例", description = "示例执行器", command = "demo", usage = "/demo - 示例执行器", privacy = "获取命令执行人")
public class DemoExecutor extends EventHandlerExecutor {


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
    public void handleUsersMessage(UserMessageEvent event, Command command) {
        System.out.println("消息" + this.getClass().getName());
    }

    @Override
    public void handleGroupMessage(GroupMessageEvent event, Command command) {
        System.out.println("消息" + this.getClass().getName());
    }
}
