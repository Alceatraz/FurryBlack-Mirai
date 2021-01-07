package studio.blacktech.furryblackplus.module.executor;

import net.mamoe.mirai.message.FriendMessageEvent;
import net.mamoe.mirai.message.GroupMessageEvent;
import net.mamoe.mirai.message.TempMessageEvent;
import net.mamoe.mirai.message.data.At;
import net.mamoe.mirai.message.data.MessageChain;
import studio.blacktech.furryblackplus.system.annotation.ComponentHandlerExecutor;
import studio.blacktech.furryblackplus.system.command.Command;
import studio.blacktech.furryblackplus.system.common.exception.BotException;
import studio.blacktech.furryblackplus.system.handler.EventHandlerExecutor;

import java.util.concurrent.ThreadLocalRandom;


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
    public void handleTempMessage(TempMessageEvent event, Command command) {
        event.getSender().sendMessage(dice());
    }


    @Override
    public void handleFriendMessage(FriendMessageEvent event, Command command) {
        event.getSender().sendMessage(dice());
    }


    @Override
    public void handleGroupMessage(GroupMessageEvent event, Command command) {
        At at = new At(event.getSender());
        MessageChain temp = at.plus(dice());
        event.getGroup().sendMessage(temp);
    }


    private final static String[] DICE = {
            "0️⃣", "1️⃣", "2️⃣", "3️⃣", "4️⃣", "5️⃣", "6️⃣"
    };


    private String dice() {
        int i = ThreadLocalRandom.current().nextInt(61);
        if (i == 0) {
            return DICE[0];
        } else {
            return DICE[i / 10 + 1];
        }
    }


    private String diceNormal() {
        return DICE[ThreadLocalRandom.current().nextInt(5) + 1];
    }


}
