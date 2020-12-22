package studio.blacktech.furryblackplus.module.executor;

import studio.blacktech.furryblackplus.system.annotation.ComponentHandlerExecutor;
import studio.blacktech.furryblackplus.system.command.FriendCommand;
import studio.blacktech.furryblackplus.system.command.GroupCommand;
import studio.blacktech.furryblackplus.system.command.TempCommand;
import studio.blacktech.furryblackplus.system.common.exception.BotException;
import studio.blacktech.furryblackplus.system.handler.EventHandlerExecutor;


@ComponentHandlerExecutor(
        name = "回显",
        description = "查看机器人是否在线",
        privacy = {
                "获取命令发送人"
        },
        command = "echo",
        usage = {
                "/echo - Ping!Pong!",
                "/echo XXX - 原样返回"
        }
)
public class Executor_Echo extends EventHandlerExecutor {


    public Executor_Echo(ExecutorInfo INFO) {
        super(INFO);
    }


    @Override
    public void init() throws BotException {
    }

    @Override
    public void boot() throws BotException {
    }

    @Override
    public void shut() throws BotException {
    }


    @Override
    public void handleTempMessage(TempCommand message) {
        message.getSender().sendMessage(message.getParameterLength() == 0 ? "PONG!" : message.getCommandBody());
    }

    @Override
    public void handleFriendMessage(FriendCommand message) {
        message.getSender().sendMessage(message.getParameterLength() == 0 ? "PONG!" : message.getCommandBody());
    }

    @Override
    public void handleGroupMessage(GroupCommand message) {
        message.getGroup().sendMessage(message.getParameterLength() == 0 ? "PONG!" : message.getCommandBody());
    }


}
