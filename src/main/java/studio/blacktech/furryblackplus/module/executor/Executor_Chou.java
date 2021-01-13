package studio.blacktech.furryblackplus.module.executor;

import net.mamoe.mirai.contact.ContactList;
import net.mamoe.mirai.contact.Group;
import net.mamoe.mirai.contact.Member;
import net.mamoe.mirai.contact.NormalMember;
import net.mamoe.mirai.event.events.FriendMessageEvent;
import net.mamoe.mirai.event.events.GroupMessageEvent;
import net.mamoe.mirai.event.events.GroupTempMessageEvent;
import net.mamoe.mirai.message.data.At;
import net.mamoe.mirai.message.data.MessageChain;
import net.mamoe.mirai.message.data.MessageChainBuilder;
import studio.blacktech.furryblackplus.Driver;
import studio.blacktech.furryblackplus.system.annotation.ComponentHandlerExecutor;
import studio.blacktech.furryblackplus.system.command.Command;
import studio.blacktech.furryblackplus.system.exception.BotException;
import studio.blacktech.furryblackplus.system.handler.EventHandlerExecutor;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;
import java.util.stream.Stream;


@ComponentHandlerExecutor(
        name = "随机抽人",
        description = "从当前的群随机抽一个人",
        privacy = {
                "获取命令发送人",
                "获取群成员列表"
        },
        users = false,
        command = "chou",
        usage = {
                "/chou - 抽一个人",
                "/chou XXX - 以某事抽一个人"
        }
)
public class Executor_Chou extends EventHandlerExecutor {


    public Executor_Chou(ExecutorInfo INFO) {
        super(INFO);
    }


    private Map<Long, List<Long>> EXCLUDE;


    @Override
    public void init() throws BotException {

        initAppFolder();
        initConfFolder();

        EXCLUDE = new HashMap<>();

        File FILE_EXCLUDE = initConfFile("exclude.txt");

        for (String line : readFile(FILE_EXCLUDE)) {

            if (!line.matches("^[0-9]{5,12}:[0-9]{5,12}$")) {
                logger.warning("配置无效 " + line);
                continue;
            }

            String[] temp = line.split(":");

            long group = Long.parseLong(temp[0].trim());
            long member = Long.parseLong(temp[1].trim());

            List<Long> tempList;

            if (EXCLUDE.containsKey(group)) {
                tempList = EXCLUDE.get(group);
            } else {
                EXCLUDE.put(group, tempList = new ArrayList<>());
            }

            tempList.add(member);

            logger.seek("排除成员 " + group + " - " + member);
        }
    }

    @Override
    public void boot() throws BotException {

    }

    @Override
    public void shut() throws BotException {

    }


    @Override
    public void handleTempMessage(GroupTempMessageEvent event, Command command) {

    }


    @Override
    public void handleFriendMessage(FriendMessageEvent event, Command command) {

    }


    @Override
    public void handleGroupMessage(GroupMessageEvent event, Command command) {


        Group group = event.getGroup();
        Member sender = event.getSender();
        ContactList<NormalMember> members = group.getMembers();

        if (members.size() < 4) {

            MessageChain messages = new MessageChainBuilder()
                                            .append(new At(sender.getId()))
                                            .append("可用成员人数不足，无法使用此功能。")
                                            .build();
            group.sendMessage(messages);
            return;

        }

        long botID = Driver.getBotID();
        long userID = sender.getId();
        long groupID = group.getId();

        Stream<Long> range = members.stream().map(Member::getId)
                                     .filter(item -> item != botID)
                                     .filter(item -> item != userID);

        if (EXCLUDE.containsKey(groupID)) {
            List<Long> list = EXCLUDE.get(groupID);
            if (!list.isEmpty()) range = range.filter(item -> !list.contains(item));
        }

        List<Long> list = range.collect(Collectors.toUnmodifiableList());

        int size = list.size();

        if (size < 2) {
            MessageChain messages = new MessageChainBuilder()
                                            .append(new At(sender.getId()))
                                            .append("可用成员人数不足，无法使用此功能。")
                                            .build();
            group.sendMessage(messages);
            return;
        }

        Long memberID = list.get(ThreadLocalRandom.current().nextInt(size));

        Member member = Driver.getGroupMember(groupID, memberID);

        StringBuilder builder = new StringBuilder();

        if (command.getCommandParameterLength() > 0) {
            builder.append("因为: ");
            builder.append(command.getCommandBody(200));
            builder.append("\r\n");
        }

        builder.append("抽中了: ");
        builder.append(member.getNick());
        builder.append("(");
        builder.append(member.getId());
        builder.append(")");

        MessageChain messages = new MessageChainBuilder()
                                        .append(new At(sender.getId()))
                                        .append(builder.toString())
                                        .build();
        group.sendMessage(messages);


    }


}
