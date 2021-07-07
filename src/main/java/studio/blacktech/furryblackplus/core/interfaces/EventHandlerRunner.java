package studio.blacktech.furryblackplus.core.interfaces;


import studio.blacktech.furryblackplus.common.Api;
import studio.blacktech.furryblackplus.core.annotation.Component;


@Api("定时器父类")
public abstract class EventHandlerRunner extends AbstractEventHandler {

    public final RunnerInfo INFO;

    protected EventHandlerRunner(RunnerInfo info) {
        super(info.ARTIFICIAL);
        this.INFO = info;
    }

    public static final class RunnerInfo extends ModuleInfo {

        public RunnerInfo(Component annotation) {
            this(annotation.name(), annotation.artificial(), annotation.description(), annotation.privacy());
        }

        public RunnerInfo(String name, String artificial, String description, String[] privacy) {
            super(name, artificial, description, privacy);
        }
    }
}
