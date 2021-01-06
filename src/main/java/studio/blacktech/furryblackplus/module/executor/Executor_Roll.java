package studio.blacktech.furryblackplus.module.executor;

import net.mamoe.mirai.message.data.At;
import net.mamoe.mirai.message.data.MessageChain;
import studio.blacktech.furryblackplus.system.annotation.ComponentHandlerExecutor;
import studio.blacktech.furryblackplus.system.command.BasicCommand;
import studio.blacktech.furryblackplus.system.command.FriendCommand;
import studio.blacktech.furryblackplus.system.command.GroupCommand;
import studio.blacktech.furryblackplus.system.command.TempCommand;
import studio.blacktech.furryblackplus.system.common.exception.BotException;
import studio.blacktech.furryblackplus.system.handler.EventHandlerExecutor;

import java.security.SecureRandom;


@ComponentHandlerExecutor(
        name = "随机数",
        description = "生成随机数并窥探本质",
        privacy = {
                "获取命令发送人"
        },
        command = "roll",
        usage = {
                "/roll - 抽取真假",
                "/roll 数字 - 从零到给定数字任选一个数字[0,x)",
                "/roll 数字 数字 - 从给定两个数字中间抽取一个[x,y)"
        }
)
public class Executor_Roll extends EventHandlerExecutor {


    public Executor_Roll(ExecutorInfo INFO) {
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
        message.getSender().sendMessage(roll(message));
    }

    @Override
    public void handleFriendMessage(FriendCommand message) {
        message.getSender().sendMessage(roll(message));
    }

    @Override
    public void handleGroupMessage(GroupCommand message) {
        At at = new At(message.getSender());
        MessageChain temp = at.plus(roll(message));
        message.getGroup().sendMessage(temp);
    }


    private String roll(BasicCommand command) {

        String res;
        SecureRandom random = new SecureRandom();

        switch (command.getParameterLength()) {

            // ============================================================

            case 0:
                res = random.nextBoolean() ? " 1️⃣" : " 0️⃣";
                break;

            // ============================================================

            case 1:
                int range;
                try {
                    range = Integer.parseInt(command.getParameterSegment(0));
                    res = Integer.toString(random.nextInt(range));
                } catch (Exception ignored) {
                    res = command.getCommandBody(200) + " 是 " + (random.nextBoolean() ? " 1️⃣" : " 0️⃣");
                }
                break;

            // ============================================================

            case 2:
                int min;
                int max;
                try {
                    min = Integer.parseInt(command.getParameterSegment(0));
                    max = Integer.parseInt(command.getParameterSegment(1));
                } catch (Exception ignored) {
                    return "参数必须是罗马数字";
                }
                int temp = random.nextInt(max - min);
                res = Integer.toString(temp + min);
                break;

            default:
                res = command.getCommandBody(200) + " 是 " + (random.nextBoolean() ? " 1️⃣" : " 0️⃣");
        }


        return res;

    }


}
