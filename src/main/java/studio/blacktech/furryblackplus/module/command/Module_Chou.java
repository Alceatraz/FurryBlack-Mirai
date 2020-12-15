package studio.blacktech.furryblackplus.module.command;

import net.mamoe.mirai.Bot;
import net.mamoe.mirai.contact.Member;
import net.mamoe.mirai.message.data.At;
import net.mamoe.mirai.message.data.MessageChain;
import net.mamoe.mirai.message.data.PlainText;
import studio.blacktech.furryblackplus.system.command.GroupCommand;
import studio.blacktech.furryblackplus.system.command.PrivateCommand;
import studio.blacktech.furryblackplus.system.common.exception.BotException;
import studio.blacktech.furryblackplus.system.common.utilties.RandomTool;
import studio.blacktech.furryblackplus.system.module.ModuleExecutor;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class Module_Chou extends ModuleExecutor {


    public Module_Chou() {
        super(
                new ModuleExecutorInfo(
                        "handler_command_chou",
                        "抽人",
                        "1.0.0",
                        "从当前群里随机抽一个人",
                        new String[]{
                                "获取命令发送人",
                                "获取群成员列表"
                        },
                        "chou",
                        new String[]{
                                "/chou - 抽一个人",
                                "/chou XXX - 以某事抽一个人"
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


    private static final List<Long> BLACKLIST = Arrays.asList(
            2410587830L,
            412815735L,
            2424155565L,
            312774993L,
            447856602L,
            2944059874L,
            1163602318L
    );

    @Override
    public void handleGroupMessage(GroupCommand message) {

        List<Member> collect = message.getGroup().getMembers().parallelStream()
                .filter(item -> item.getId() != message.getSender().getId())
                .filter(item -> item.getId() != Bot.getBotInstances().get(0).getId())
                .filter(item -> !BLACKLIST.contains(item.getId()))
                .collect(Collectors.toList());


        At at = new At(message.getSender());

        MessageChain temp;

        if (collect.size() < 2) {
            temp = at.plus(new PlainText("无法抽人"));
        } else {
            Member member = collect.get(RandomTool.nextInt(collect.size()));
            StringBuilder builder = new StringBuilder();

            if (message.getParameterSection() > 0) {
                builder.append("因为: ");
                builder.append(message.getCommandBody());
                builder.append("\r\n");
            }

            builder.append("抽中了: ");
            builder.append(member.getNameCard());
            builder.append("(");
            builder.append(member.getId());
            builder.append(")");
            temp = at.plus(builder.toString());
        }
        message.getGroup().sendMessage(temp);
    }


}
