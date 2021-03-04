package studio.blacktech.furryblackplus.core.interfaces;

import net.mamoe.mirai.event.events.GroupMessageEvent;
import net.mamoe.mirai.event.events.UserMessageEvent;
import studio.blacktech.furryblackplus.core.annotation.Api;
import studio.blacktech.furryblackplus.core.utilties.Command;
import studio.blacktech.furryblackplus.demo.DemoExecutor;


@Api(value = "具体使用方法请见示例", see = DemoExecutor.class)
public abstract class EventHandlerExecutor extends AbstractEventHandler {

    public final ExecutorInfo INFO;

    public EventHandlerExecutor(ExecutorInfo info) {
        super(info);
        this.INFO = info;
    }

    @Api("生命周期 处理私聊命令")
    public abstract void handleUsersMessage(UserMessageEvent event, Command command);

    @Api("生命周期 处理群聊命令")
    public abstract void handleGroupMessage(GroupMessageEvent event, Command command);

    public static class ExecutorInfo extends ModuleInfo {

        public final String COMMAND;
        public final String[] USAGE;
        public final String HELP;

        public ExecutorInfo(String name, String artificial, String description, String[] privacy, String command, String[] usage) {
            super(name, artificial, description, privacy);
            if (command.equals("")) throw new IllegalArgumentException("无效的模块命令`command`");
            this.COMMAND = command;
            this.USAGE = usage;
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
