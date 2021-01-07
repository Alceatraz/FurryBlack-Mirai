package studio.blacktech.furryblackplus.module.executor;

import net.mamoe.mirai.message.FriendMessageEvent;
import net.mamoe.mirai.message.GroupMessageEvent;
import net.mamoe.mirai.message.TempMessageEvent;
import net.mamoe.mirai.message.data.At;
import studio.blacktech.furryblackplus.system.annotation.ComponentHandlerExecutor;
import studio.blacktech.furryblackplus.system.command.Command;
import studio.blacktech.furryblackplus.system.common.exception.BotException;
import studio.blacktech.furryblackplus.system.common.utilties.DateTool;
import studio.blacktech.furryblackplus.system.handler.EventHandlerExecutor;

import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ThreadLocalRandom;


@ComponentHandlerExecutor(
        name = "今日运气",
        description = "查看今天的运气值 - 大失败酱",
        privacy = {
                "获取命令发送人",
                "缓存用户与运气对应表 - 每日UTC+8 00:00 清空"
        },
        command = "jrrp",
        usage = {
                "/jrrp - 查看今日运气"
        }
)
public class Executor_Jrrp extends EventHandlerExecutor {


    public Executor_Jrrp(ExecutorInfo INFO) {
        super(INFO);
    }


    private Map<Long, Integer> JRRP;

    private Timer timer;


    @Override
    public void init() throws BotException {
        JRRP = new HashMap<>();
    }

    @Override
    public void boot() throws BotException {
        timer = new Timer();
        timer.scheduleAtFixedRate(
                new TimerTask() {
                    @Override
                    public void run() {
                        JRRP.clear();
                    }
                },
                DateTool.getNextDate(),
                DateTool.durationDay()
        );
    }

    @Override
    public void shut() {
        timer.cancel();
    }


    @Override
    public void handleTempMessage(TempMessageEvent event, Command command) {
        event.getSender().sendMessage("今天的运气是" + getRp(event.getSender().getId()) + "% !!!");
    }


    @Override
    public void handleFriendMessage(FriendMessageEvent event, Command command) {
        event.getSender().sendMessage("今天的运气是" + getRp(event.getSender().getId()) + "% !!!");
    }


    @Override
    public void handleGroupMessage(GroupMessageEvent event, Command command) {
        event.getGroup().sendMessage(new At(event.getSender()).plus("今天的运气是" + getRp(event.getSender().getId()) + "% !!!"));
    }


    private int getRp(long userid) {
        if (JRRP.containsKey(userid)) return JRRP.get(userid);
        JRRP.put(userid, ThreadLocalRandom.current().nextInt(100));
        return JRRP.get(userid);
    }


}
