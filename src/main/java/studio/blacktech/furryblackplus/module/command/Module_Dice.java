package studio.blacktech.furryblackplus.module.command;

import net.mamoe.mirai.message.data.At;
import net.mamoe.mirai.message.data.MessageChain;
import net.mamoe.mirai.message.data.PlainText;
import studio.blacktech.furryblackplus.system.command.GroupCommand;
import studio.blacktech.furryblackplus.system.command.PrivateCommand;
import studio.blacktech.furryblackplus.system.common.exception.BotException;
import studio.blacktech.furryblackplus.system.common.utilties.RandomTool;
import studio.blacktech.furryblackplus.system.module.ModuleExecutor;

public class Module_Dice extends ModuleExecutor {


    public Module_Dice() {
        super(
                new ModuleExecutorInfo(
                        "handler_command_dice",
                        "骰子",
                        "1.0.0",
                        "扔一个骰子",
                        null,
                        "dice",
                        new String[]{
                                "/dice - 投掷一枚骰子"
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
        MessageChain temp = at.plus(new PlainText(dice()));
        message.getGroup().sendMessage(temp);

    }


    private final static String[] DICE = {
            "0️⃣", "1️⃣", "2️⃣", "3️⃣", "4️⃣", "5️⃣", "6️⃣"
    };


    private String dice() {
        int i = RandomTool.nextInt(61);
        if (i == 0) {
            return DICE[0];
        } else {
            return DICE[i / 10 + 1];
        }
    }


    private String diceNormal() {
        return DICE[RandomTool.nextInt(5) + 1];
    }


}
