package studio.blacktech.furryblackplus.demo;

import studio.blacktech.furryblackplus.core.annotation.Component;
import studio.blacktech.furryblackplus.core.interfaces.EventHandlerRunner;


@Component(
    artificial = "Runner_Demo",
    name = "示例",
    description = "示例执行器",
    privacy = {
        "无"
    }
)
public class DemoRunner extends EventHandlerRunner {


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

    @SuppressWarnings("EmptyMethod")
    public void demo() {
        // Do what ever you need to
    }
}
