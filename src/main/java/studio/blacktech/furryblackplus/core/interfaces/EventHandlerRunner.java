package studio.blacktech.furryblackplus.core.interfaces;


import studio.blacktech.furryblackplus.core.annotation.Api;


@Api("定时器父类")
public abstract class EventHandlerRunner extends AbstractEventHandler {

    public final RunnerInfo INFO;

    protected EventHandlerRunner(RunnerInfo info) {
        super(info.ARTIFICIAL);
        this.INFO = info;
    }

    public final static class RunnerInfo extends ModuleInfo {
        public RunnerInfo(String name, String artificial, String description, String[] privacy) {
            super(name, artificial, description, privacy);
        }
    }
}
