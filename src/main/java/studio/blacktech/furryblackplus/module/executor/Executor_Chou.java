package studio.blacktech.furryblackplus.module.executor;

import net.mamoe.mirai.Bot;
import net.mamoe.mirai.contact.Member;
import net.mamoe.mirai.message.data.At;
import net.mamoe.mirai.message.data.MessageChain;
import net.mamoe.mirai.message.data.PlainText;
import studio.blacktech.furryblackplus.system.annotation.ComponentHandlerExecutor;
import studio.blacktech.furryblackplus.system.command.FriendCommand;
import studio.blacktech.furryblackplus.system.command.GroupCommand;
import studio.blacktech.furryblackplus.system.command.TempCommand;
import studio.blacktech.furryblackplus.system.common.exception.BotException;
import studio.blacktech.furryblackplus.system.common.utilties.RandomTool;
import studio.blacktech.furryblackplus.system.handler.EventHandlerExecutor;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@ComponentHandlerExecutor(
        name = "随机抽人",
        description = "从当前的群随机抽一个人",
        privacy = {
                "获取命令发送人",
                "获取群成员列表"
        },
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

        File FILE_EXCLUDE = Paths.get(FOLDER_CONF.getAbsolutePath(), "exclude.txt").toFile();

        if (!FILE_EXCLUDE.exists()) {
            try {
                FILE_EXCLUDE.createNewFile();
            } catch (IOException exception) {
                throw new BotException("创建文件失败 -> " + FILE_EXCLUDE.getAbsolutePath(), exception);
            }
        }

        if (!FILE_EXCLUDE.canRead()) throw new BotException("文件无权读取 -> " + FILE_EXCLUDE.getAbsolutePath());


        long gropid;
        long userid;

        String line;
        String[] temp;


        try (
                FileInputStream fileInputStream = new FileInputStream(FILE_EXCLUDE);
                InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream, StandardCharsets.UTF_8);
                BufferedReader reader = new BufferedReader(inputStreamReader)
        ) {

            while ((line = reader.readLine()) != null) {

                if (line.startsWith("#")) continue;
                if (!line.contains(":")) continue;
                if (line.contains("#")) line = line.substring(0, line.indexOf("#")).trim();

                temp = line.split(":");

                if (temp.length != 2) {
                    logger.warning("配置无效 " + line);
                    continue;
                }

                gropid = Long.parseLong(temp[0]);
                userid = Long.parseLong(temp[1]);

                List<Long> tempList;

                if (EXCLUDE.containsKey(gropid)) {
                    tempList = EXCLUDE.get(gropid);
                } else {
                    EXCLUDE.put(gropid, tempList = new ArrayList<>());
                }

                tempList.add(userid);

                logger.seek("排除成员 " + gropid + " - " + userid);
            }

        } catch (IOException exception) {
            throw new BotException(exception);
        }
    }

    @Override
    public void boot() throws BotException {

    }

    @Override
    public void shut() throws BotException {

    }


    @Override
    public void handleTempMessage(TempCommand message) {

    }

    @Override
    public void handleFriendMessage(FriendCommand message) {

    }


    @Override
    public void handleGroupMessage(GroupCommand message) {

        List<Member> collect = message.getGroup().getMembers().parallelStream()
                                       .filter(item -> item.getId() != message.getSender().getId())
                                       .filter(item -> item.getId() != Bot.getBotInstances().get(0).getId())
                                       .filter(item -> !EXCLUDE.get(message.getGroup().getId()).contains(item.getId()))
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
