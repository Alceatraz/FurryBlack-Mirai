package studio.blacktech.furryblackplus.module;

import net.mamoe.mirai.message.data.At;
import net.mamoe.mirai.message.data.MessageChain;
import net.mamoe.mirai.message.data.PlainText;
import studio.blacktech.furryblackplus.system.command.GroupCommand;
import studio.blacktech.furryblackplus.system.command.PrivateCommand;
import studio.blacktech.furryblackplus.system.common.exception.BotException;
import studio.blacktech.furryblackplus.system.module.ModuleExecutor;

public class Module_null extends ModuleExecutor {


    public Module_null() {
        super(
                new ModuleExecutorInfo(
                        "handler_command_",
                        "",
                        "1.0.0",
                        "",
                        new String[]{
                                ""
                        },
                        "",
                        new String[]{
                                ""
                        })
        );
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
    public void handleTempMessage(PrivateCommand message) {

    }

    @Override
    public void handleFriendMessage(PrivateCommand message) {

    }

    @Override
    public void handleGroupMessage(GroupCommand message) {

        At at = new At(message.getSender());
        MessageChain temp = at.plus(new PlainText(""));
        message.getGroup().sendMessage(temp);

    }


}
