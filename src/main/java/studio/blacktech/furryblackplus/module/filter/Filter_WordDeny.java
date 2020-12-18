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
import java.util.ArrayList;
import java.util.List;


@ComponentHandlerFilter(
        name = "正则过滤",
        description = "按照正则过滤消息",
        privacy = {
                "获取消息来源"
        }
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


        String line;

        try (
                FileInputStream fileInputStream = new FileInputStream(FILE_BLACKLIST);
                InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream, StandardCharsets.UTF_8);
                BufferedReader reader = new BufferedReader(inputStreamReader)
        ) {

            while ((line = reader.readLine()) != null) {

                if (line.startsWith("#")) continue;
                if (line.contains("#")) line = line.substring(0, line.indexOf("#")).trim();

                REGEXES.add(line);
                logger.seek("添加规则 " + line);

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
        String temp = message.getMessage().contentToString();
        return REGEXES.parallelStream().anyMatch(temp::matches);
    }


    @Override
    public boolean handleFriendMessage(FriendMessageEvent message) {
        String temp = message.getMessage().contentToString();
        return REGEXES.parallelStream().anyMatch(temp::matches);
    }


    @Override
    public boolean handleGroupMessage(GroupMessageEvent message) {
/*                return message.getMessage()
                               .parallelStream()
                               .filter(item -> item instanceof PlainText)
                               .anyMatch(
                                       item -> REGEXES.parallelStream()
                                                       .anyMatch(
                                                               regex -> item.toString().matches(regex)
                                                       )
                               );*/
        String temp = message.getMessage().contentToString();
        return REGEXES.parallelStream().anyMatch(temp::matches);
    }


}
