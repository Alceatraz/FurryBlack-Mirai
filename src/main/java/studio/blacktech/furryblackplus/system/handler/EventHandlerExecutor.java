package studio.blacktech.furryblackplus.system.handler;

import studio.blacktech.furryblackplus.system.command.FriendCommand;
import studio.blacktech.furryblackplus.system.command.GroupCommand;
import studio.blacktech.furryblackplus.system.command.TempCommand;

public abstract class EventHandlerExecutor extends AbstractEventHandler {


    public static class ExecutorInfo extends ModuleInfo {
        public final String COMMAND;
        public final String[] USAGE;
        public final String HELP;

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
            if (COMMAND.equals("")) throw new IllegalArgumentException("COMMAND cannot be null");
            if (USAGE == null) throw new IllegalArgumentException("USAGE cannot be null");

            this.COMMAND = COMMAND;
            this.USAGE = USAGE;
            StringBuilder builder = new StringBuilder();
            builder.append(COMMAND);
            builder.append(" ");
            builder.append(NAME);
            builder.append("\r\n");
            builder.append(DESCRIPTION);
            builder.append("\r\n命令用法: \r\n");
            for (String temp : USAGE) {
                builder.append(temp);
                builder.append("\r\n");
            }
            builder.append("隐私: \r\n");
            for (String temp : PRIVACY) {
                builder.append(temp);
                builder.append("\r\n");
            }
            HELP = builder.toString();
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
