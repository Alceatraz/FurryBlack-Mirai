package studio.blacktech.furryblackplus.module.filter;

import net.mamoe.mirai.message.FriendMessageEvent;
import net.mamoe.mirai.message.GroupMessageEvent;
import net.mamoe.mirai.message.TempMessageEvent;
import studio.blacktech.furryblackplus.system.annotation.ComponentHandlerFilter;
import studio.blacktech.furryblackplus.system.common.exception.BotException;
import studio.blacktech.furryblackplus.system.handler.EventHandlerFilter;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;


@ComponentHandlerFilter(
        name = "用户过滤",
        description = "按照ID过滤用户和群",
        privacy = {
                "获取消息来源"
        },
        artificial = "userdeny"
)
public class Filter_UserDeny extends EventHandlerFilter {


    public Filter_UserDeny(FilterInfo INFO) {
        super(INFO);
    }


    private Set<Long> USER_IGNORE;
    private Set<Long> GROUP_IGNORE;
    private Map<Long, Set<Long>> MEMBER_IGNORE;


    @Override
    public void init() throws BotException {

        initAppFolder();
        initConfFolder();

        USER_IGNORE = new HashSet<>();
        GROUP_IGNORE = new HashSet<>();
        MEMBER_IGNORE = new TreeMap<>();

        File FILE_BLACKLIST = Paths.get(FOLDER_CONF.getAbsolutePath(), "blacklist.txt").toFile();

        if (!FILE_BLACKLIST.exists()) {
            try {
                FILE_BLACKLIST.createNewFile();
                logger.hint("创建新的配置文件 -> " + FILE_BLACKLIST.getAbsolutePath());
            } catch (IOException exception) {
                throw new BotException("创建文件失败 -> " + FILE_BLACKLIST.getAbsolutePath(), exception);
            }
        }

        if (!FILE_BLACKLIST.canRead()) throw new BotException("文件无权读取 -> " + FILE_BLACKLIST.getAbsolutePath());


        long gropid;
        long userid;

        String line;
        String[] temp;

        try (
                FileInputStream fileInputStream = new FileInputStream(FILE_BLACKLIST);
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

                if (temp[0].equals("*")) { // Global Deny User
                    userid = Long.parseLong(temp[1]);
                    USER_IGNORE.add(userid);
                    logger.seek("拉黑用户 " + userid);

                } else if (temp[1].equals("*")) { // Deny Group
                    gropid = Long.parseLong(temp[0]);
                    GROUP_IGNORE.add(gropid);
                    logger.seek("拉黑群组 " + gropid);

                } else { // Deny Member
                    gropid = Long.parseLong(temp[0]);
                    userid = Long.parseLong(temp[1]);
                    Set<Long> tempSet;
                    if (MEMBER_IGNORE.containsKey(gropid)) {
                        tempSet = MEMBER_IGNORE.get(gropid);
                    } else {
                        MEMBER_IGNORE.put(gropid, tempSet = new HashSet<>());
                    }
                    tempSet.add(userid);
                    logger.seek("拉黑成员 " + gropid + " - " + userid);
                }
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
    public boolean handleTempMessage(TempMessageEvent message) {
        return USER_IGNORE.contains(message.getSender().getId());
    }

    @Override
    public boolean handleFriendMessage(FriendMessageEvent message) {
        return USER_IGNORE.contains(message.getSender().getId());
    }

    @Override
    public boolean handleGroupMessage(GroupMessageEvent message) {
        if (GROUP_IGNORE.contains(message.getGroup().getId())) return true;
        if (MEMBER_IGNORE.containsKey(message.getGroup().getId())) return MEMBER_IGNORE.get(message.getGroup().getId()).contains(message.getSender().getId());
        return false;
    }
}
