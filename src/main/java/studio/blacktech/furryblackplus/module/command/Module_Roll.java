package studio.blacktech.furryblackplus.module.command;

import net.mamoe.mirai.message.data.At;
import net.mamoe.mirai.message.data.MessageChain;
import studio.blacktech.furryblackplus.system.command.BasicCommand;
import studio.blacktech.furryblackplus.system.command.GroupCommand;
import studio.blacktech.furryblackplus.system.command.PrivateCommand;
import studio.blacktech.furryblackplus.system.common.exception.BotException;
import studio.blacktech.furryblackplus.system.module.ModuleExecutor;

import java.security.SecureRandom;

public class Module_Roll extends ModuleExecutor {


    public Module_Roll() {
        super(
                new ModuleExecutorInfo(
                        "handler_command_roll",
                        "随机数",
                        "1.0.0",
                        "生成随机数",
                        new String[]{
                                "获取命令发送人"
                        },
                        "roll",
                        new String[]{
                                "/roll - 抽取真假",
                                "/roll 数字 - 从零到给定数字任选一个数字",
                                "/roll 数字 数字 - 从给定两个数字中间抽取一个"
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
        MessageChain temp = at.plus(roll(message));
        message.getGroup().sendMessage(temp);

    }


    private String roll(BasicCommand basicCommand) {

        String res = null;
        SecureRandom random = new SecureRandom();

        switch (basicCommand.getParameterSection()) {

            // ============================================================

            case 0:
                res = random.nextBoolean() ? " 1️⃣" : " 0️⃣";
                break;

            // ============================================================

            case 1:
                int range;
                try {
                    range = Integer.parseInt(basicCommand.getParameterSegment(0));
                    res = Integer.toString(random.nextInt(range));

                } catch (Exception ignored) {
                    res = basicCommand.getCommandBody() + " 是 " + (random.nextBoolean() ? " 1️⃣" : " 0️⃣");
                }
                break;

            // ============================================================

            case 2:
                int min;
                int max;
                try {
                    min = Integer.parseInt(basicCommand.getParameterSegment(0));
                    max = Integer.parseInt(basicCommand.getParameterSegment(1));
                } catch (Exception ignored) {
                    return "参数必须是罗马数字";
                }
                int temp = random.nextInt(max - min);
                res = Integer.toString(temp + min);
                break;
        }


        return res;

    }


}
