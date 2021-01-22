package studio.blacktech.furryblackplus.core.interfaces;

import net.mamoe.mirai.event.events.GroupMessageEvent;
import net.mamoe.mirai.event.events.UserMessageEvent;
import studio.blacktech.furryblackplus.core.utilties.Command;

public abstract class EventHandlerExecutor extends AbstractEventHandler {

    public final ExecutorInfo INFO;

    public EventHandlerExecutor(ExecutorInfo INFO) {
        super(INFO);
        this.INFO = INFO;
    }

    public abstract void handleUsersMessage(UserMessageEvent event, Command command);

    public abstract void handleGroupMessage(GroupMessageEvent event, Command command);

    public static class ExecutorInfo extends ModuleInfo {

        public final String COMMAND;
        public final String[] USAGE;
        public final String HELP;

        public ExecutorInfo(String MODULE_NAME, String MODULE_ARTIFICIAL, String MODULE_DESCRIPTION, String[] MODULE_PRIVACY, String COMMAND, String[] USAGE) {
            super(MODULE_NAME, MODULE_ARTIFICIAL, MODULE_DESCRIPTION, MODULE_PRIVACY);
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
}
