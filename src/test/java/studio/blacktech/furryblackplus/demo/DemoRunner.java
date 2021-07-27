package studio.blacktech.furryblackplus.demo;


import studio.blacktech.furryblackplus.core.annotation.Runner;
import studio.blacktech.furryblackplus.core.define.moduel.EventHandlerRunner;

@Runner(
    value = "demo-runner"
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

    public void demo() {
        this.logger.info("DemoRunner working!");
    }
}
