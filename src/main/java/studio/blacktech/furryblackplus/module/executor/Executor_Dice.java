package studio.blacktech.furryblackplus.module.executor;

import net.mamoe.mirai.message.data.At;
import net.mamoe.mirai.message.data.MessageChain;
import studio.blacktech.furryblackplus.system.annotation.ComponentHandlerExecutor;
import studio.blacktech.furryblackplus.system.command.FriendCommand;
import studio.blacktech.furryblackplus.system.command.GroupCommand;
import studio.blacktech.furryblackplus.system.command.TempCommand;
import studio.blacktech.furryblackplus.system.common.exception.BotException;
import studio.blacktech.furryblackplus.system.common.utilties.RandomTool;
import studio.blacktech.furryblackplus.system.handler.EventHandlerExecutor;


@ComponentHandlerExecutor(
        name = "骰子",
        description = "七面骰子",
        privacy = {
                "获取命令发送人"
        },
        command = "dice",
        usage = {
                "/dice - 投掷一枚骰子"
        }
)
public class Executor_Dice extends EventHandlerExecutor {


    public Executor_Dice(ExecutorInfo INFO) {
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
        message.getSender().sendMessage(dice());
    }

    @Override
    public void handleFriendMessage(FriendCommand message) {
        message.getSender().sendMessage(dice());
    }

    @Override
    public void handleGroupMessage(GroupCommand message) {
        At at = new At(message.getSender());
        MessageChain temp = at.plus(dice());
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
