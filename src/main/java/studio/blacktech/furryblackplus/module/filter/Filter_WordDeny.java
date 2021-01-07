package studio.blacktech.furryblackplus.module.filter;

import net.mamoe.mirai.message.FriendMessageEvent;
import net.mamoe.mirai.message.GroupMessageEvent;
import net.mamoe.mirai.message.TempMessageEvent;
import studio.blacktech.furryblackplus.system.annotation.ComponentHandlerFilter;
import studio.blacktech.furryblackplus.system.common.exception.BotException;
import studio.blacktech.furryblackplus.system.handler.EventHandlerFilter;

import java.io.File;
import java.util.ArrayList;
import java.util.List;


@ComponentHandlerFilter(
        name = "正则过滤",
        description = "按照正则过滤消息",
        privacy = {
                "获取消息来源"
        },
        artificial = "worddeny"
)
public class Filter_WordDeny extends EventHandlerFilter {


    public Filter_WordDeny(FilterInfo INFO) {
        super(INFO);
    }


    private List<String> REGEXES;


    @Override
    public void init() throws BotException {

        initAppFolder();
        initConfFolder();

        REGEXES = new ArrayList<>();

        File FILE_BLACKLIST = initConfFile("blacklist.txt");

        for (String item : readFile(FILE_BLACKLIST)) {
            REGEXES.add(item);
            logger.seek("添加规则 " + item);
        }

    }


    @Override
    public void boot() throws BotException {
    }

    @Override
    public void shut() throws BotException {
    }


    @Override
    public boolean handleTempMessage(TempMessageEvent message, String content) {
        return REGEXES.stream().anyMatch(content::matches);
    }


    @Override
    public boolean handleFriendMessage(FriendMessageEvent message, String content) {
        return REGEXES.stream().anyMatch(content::matches);
    }


    @Override
    public boolean handleGroupMessage(GroupMessageEvent message, String content) {
        return REGEXES.stream().anyMatch(content::matches);
    }


}
