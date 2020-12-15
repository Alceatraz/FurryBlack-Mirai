package studio.blacktech.furryblackplus.system.module;

import studio.blacktech.furryblackplus.system.command.GroupCommand;
import studio.blacktech.furryblackplus.system.command.PrivateCommand;

public abstract class ModuleExecutor extends Handler {


    public static class ModuleExecutorInfo extends ModuleInfo {

        public final String COMMAND_NAME;
        public final String[] COMMAND_USAGE;

        public ModuleExecutorInfo(
                String MODULE_ARTIFACT_NAME,
                String MODULE_FRIENDLY_NAME,
                String MODULE_VERSION,
                String MODULE_DESCRIPTION,
                String[] MODULE_PRIVACY,
                String COMMAND_NAME,
                String[] COMMAND_USAGE
        ) {
            super(
                    MODULE_ARTIFACT_NAME,
                    MODULE_FRIENDLY_NAME,
                    MODULE_VERSION,
                    MODULE_DESCRIPTION,
                    MODULE_PRIVACY
            );
            this.COMMAND_NAME = COMMAND_NAME;
            this.COMMAND_USAGE = COMMAND_USAGE;
        }


    }


    public final ModuleExecutorInfo INFO;


    public ModuleExecutor(ModuleExecutorInfo INFO) {
        super(INFO);
        this.INFO = INFO;
    }


    public abstract void handleTempMessage(PrivateCommand message);

    public abstract void handleFriendMessage(PrivateCommand message);

    public abstract void handleGroupMessage(GroupCommand message);

}
