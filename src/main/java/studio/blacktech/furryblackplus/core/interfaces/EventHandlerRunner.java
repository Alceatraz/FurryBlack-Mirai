package studio.blacktech.furryblackplus.core.interfaces;


import studio.blacktech.furryblackplus.core.annotation.Api;
import studio.blacktech.furryblackplus.demo.DemoRunner;


@Api(value = "具体使用方法请见示例", see = DemoRunner.class)
public abstract class EventHandlerRunner extends AbstractEventHandler {

    public final RunnerInfo INFO;

    public EventHandlerRunner(RunnerInfo info) {
        super(info);
        this.INFO = info;
    }

    public static class RunnerInfo extends ModuleInfo {
        public RunnerInfo(String name, String artificial, String description, String[] privacy) {
            super(name, artificial, description, privacy);
        }
    }
}
