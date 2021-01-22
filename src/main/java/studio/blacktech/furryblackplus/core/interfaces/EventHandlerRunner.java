package studio.blacktech.furryblackplus.core.interfaces;


public abstract class EventHandlerRunner extends AbstractEventHandler {

    public final RunnerInfo INFO;

    public EventHandlerRunner(RunnerInfo INFO) {
        super(INFO);
        this.INFO = INFO;
    }

    public static class RunnerInfo extends ModuleInfo {
        public RunnerInfo(String MODULE_NAME, String MODULE_ARTIFICIAL, String MODULE_DESCRIPTION, String[] MODULE_PRIVACY) {
            super(MODULE_NAME, MODULE_ARTIFICIAL, MODULE_DESCRIPTION, MODULE_PRIVACY);
        }
    }
}
