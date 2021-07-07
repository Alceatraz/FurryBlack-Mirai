package studio.blacktech.furryblackplus.core.interfaces;

import net.mamoe.mirai.event.events.GroupMessageEvent;
import net.mamoe.mirai.event.events.UserMessageEvent;
import studio.blacktech.furryblackplus.common.Api;
import studio.blacktech.furryblackplus.core.annotation.Component;
import studio.blacktech.furryblackplus.core.utilties.Command;


@Api("执行器父类")
public abstract class EventHandlerExecutor extends AbstractEventHandler {

    public final ExecutorInfo INFO;

    protected EventHandlerExecutor(ExecutorInfo info) {
        super(info.ARTIFICIAL);
        this.INFO = info;
    }

    @Api("生命周期 处理私聊命令")
    public abstract void handleUsersMessage(UserMessageEvent event, Command command);

    @Api("生命周期 处理群聊命令")
    public abstract void handleGroupMessage(GroupMessageEvent event, Command command);

    public static final class ExecutorInfo extends ModuleInfo {

        public final String COMMAND;
        public final String[] USAGE;
        public final String HELP;

        public ExecutorInfo(Component annotation) {
            this(annotation.name(), annotation.artificial(), annotation.description(), annotation.privacy(), annotation.command(), annotation.usage());
        }

        public ExecutorInfo(String name, String artificial, String description, String[] privacy, String command, String[] usage) {
            super(name, artificial, description, privacy);
            if (command.isBlank()) throw new IllegalArgumentException("无效的模块命令`command`");
            this.COMMAND = command;
            this.USAGE = usage;
            StringBuilder builder = new StringBuilder();
            builder.append(this.COMMAND);
            builder.append(" ");
            builder.append(this.NAME);
            builder.append("\r\n");
            builder.append(this.DESCRIPTION);
            builder.append("\r\n命令用法: \r\n");
            for (String temp : this.USAGE) {
                builder.append(temp);
                builder.append("\r\n");
            }
            builder.append("隐私: \r\n");
            for (String temp : this.PRIVACY) {
                builder.append(temp);
                builder.append("\r\n");
            }
            this.HELP = builder.toString();
        }
    }
}
