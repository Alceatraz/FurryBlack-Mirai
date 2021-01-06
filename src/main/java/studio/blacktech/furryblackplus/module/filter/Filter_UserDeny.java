package studio.blacktech.furryblackplus.module.filter;

import net.mamoe.mirai.message.FriendMessageEvent;
import net.mamoe.mirai.message.GroupMessageEvent;
import net.mamoe.mirai.message.TempMessageEvent;
import studio.blacktech.furryblackplus.system.annotation.ComponentHandlerFilter;
import studio.blacktech.furryblackplus.system.common.exception.BotException;
import studio.blacktech.furryblackplus.system.handler.EventHandlerFilter;

import java.io.File;
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

        File FILE_BLACKLIST = initConfFile("blacklist.txt");

        for (String line : readFile(FILE_BLACKLIST)) {

            if (!line.matches("^(?:\\*|[0-9]{5,12}):(?:\\*|[0-9]{5,12})$")) {
                logger.warning("配置无效 " + line);
                continue;
            }

            String[] temp = line.split(":");

            long group;
            long member;

            if (temp[0].equals("*")) { // Global Deny User
                if (temp[1].equals("*")) { // Invalidate
                    logger.warning("配置无效 " + line);
                    continue;
                }
                member = Long.parseLong(temp[1]);
                USER_IGNORE.add(member);
                logger.seek("拉黑用户 " + member);
            } else if (temp[1].equals("*")) { // Deny Group
                group = Long.parseLong(temp[0]);
                GROUP_IGNORE.add(group);
                logger.seek("拉黑群组 " + group);
            } else { // Deny Member
                group = Long.parseLong(temp[0]);
                member = Long.parseLong(temp[1]);
                Set<Long> tempSet;
                if (MEMBER_IGNORE.containsKey(group)) {
                    tempSet = MEMBER_IGNORE.get(group);
                } else {
                    MEMBER_IGNORE.put(group, tempSet = new HashSet<>());
                }
                tempSet.add(member);
                logger.seek("拉黑成员 " + group + " - " + member);
            }

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
