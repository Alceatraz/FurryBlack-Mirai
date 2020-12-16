package studio.blacktech.furryblackplus.system.handler;

import lombok.Getter;
import studio.blacktech.furryblackplus.system.command.FriendCommand;
import studio.blacktech.furryblackplus.system.command.GroupCommand;
import studio.blacktech.furryblackplus.system.command.TempCommand;

public abstract class EventHandlerExecutor extends AbstractEventHandler {


    @Getter
    public static class ExecutorInfo extends ModuleInfo {
        public final String COMMAND;
        public final String[] USAGE;

        public ExecutorInfo(
                String NAME,
                String DESCRIPTION,
                String[] PRIVACY,
                String COMMAND,
                String[] USAGE
        ) {
            super(
                    NAME,
                    DESCRIPTION,
                    PRIVACY
            );
            this.COMMAND = COMMAND;
            this.USAGE = USAGE;
        }
    }


    public final ExecutorInfo INFO;


    public EventHandlerExecutor(ExecutorInfo INFO) {
        super(INFO);
        this.INFO = INFO;
    }


    public abstract void handleTempMessage(TempCommand message);

    public abstract void handleFriendMessage(FriendCommand message);

    public abstract void handleGroupMessage(GroupCommand message);

}
