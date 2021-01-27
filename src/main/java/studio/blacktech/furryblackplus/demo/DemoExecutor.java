package studio.blacktech.furryblackplus.demo;

import net.mamoe.mirai.event.events.GroupMessageEvent;
import net.mamoe.mirai.event.events.UserMessageEvent;
import studio.blacktech.furryblackplus.core.annotation.Executor;
import studio.blacktech.furryblackplus.core.interfaces.EventHandlerExecutor;
import studio.blacktech.furryblackplus.core.utilties.Command;


@Executor(
    artificial = "Executor_Demo",
    name = "示例",
    description = "示例执行器",
    privacy = {
        "无"
    },
    command = "demo",
    usage = {
        "/demo - 示例执行器"
    }
)
public class DemoExecutor extends EventHandlerExecutor {


    public DemoExecutor(ExecutorInfo INFO) {
        super(INFO);
    }


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
