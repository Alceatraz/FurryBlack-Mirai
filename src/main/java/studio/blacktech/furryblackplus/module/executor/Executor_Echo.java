package studio.blacktech.furryblackplus.module.executor;

import net.mamoe.mirai.message.FriendMessageEvent;
import net.mamoe.mirai.message.GroupMessageEvent;
import net.mamoe.mirai.message.TempMessageEvent;
import net.mamoe.mirai.message.data.At;
import studio.blacktech.furryblackplus.system.annotation.ComponentHandlerExecutor;
import studio.blacktech.furryblackplus.system.command.Command;
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
    public void handleTempMessage(TempMessageEvent event, Command command) {
        event.getSender().sendMessage(command.getCommandParameterLength() == 0 ? "PONG!" : command.getCommandBody());
    }


    @Override
    public void handleFriendMessage(FriendMessageEvent event, Command command) {
        event.getSender().sendMessage(command.getCommandParameterLength() == 0 ? "PONG!" : command.getCommandBody());
    }


    @Override
    public void handleGroupMessage(GroupMessageEvent event, Command command) {
        event.getGroup().sendMessage(new At(event.getSender()).plus(command.getCommandParameterLength() == 0 ? "PONG!" : command.getCommandBody()));
    }


}
