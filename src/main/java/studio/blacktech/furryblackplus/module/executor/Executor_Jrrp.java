package studio.blacktech.furryblackplus.module.executor;

import net.mamoe.mirai.message.data.At;
import studio.blacktech.furryblackplus.system.annotation.ComponentHandlerExecutor;
import studio.blacktech.furryblackplus.system.command.FriendCommand;
import studio.blacktech.furryblackplus.system.command.GroupCommand;
import studio.blacktech.furryblackplus.system.command.TempCommand;
import studio.blacktech.furryblackplus.system.common.exception.BotException;
import studio.blacktech.furryblackplus.system.common.utilties.DateTool;
import studio.blacktech.furryblackplus.system.common.utilties.RandomTool;
import studio.blacktech.furryblackplus.system.handler.EventHandlerExecutor;

import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;


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
        logger.info("启动工作线程");
        timer = new Timer();
        timer.scheduleAtFixedRate(
                new TimerTask() {
                    @Override
                    public void run() {
                        JRRP.clear();
                    }
                },
                DateTool.getNextDate(),
                86400000L
        );
    }

    @Override
    public void shut() {
        timer.cancel();
    }


    @Override
    public void handleTempMessage(TempCommand message) {
        message.getSender().sendMessage("今天的运气是" + getRp(message.getSender().getId()) + "% !!!");
    }

    @Override
    public void handleFriendMessage(FriendCommand message) {
        message.getSender().sendMessage("今天的运气是" + getRp(message.getSender().getId()) + "% !!!");
    }

    @Override
    public void handleGroupMessage(GroupCommand message) {
        message.getGroup().sendMessage(new At(message.getSender()).plus("今天的运气是" + getRp(message.getSender().getId()) + "% !!!"));
    }


    private int getRp(long userid) {
        if (JRRP.containsKey(userid)) return JRRP.get(userid);
        JRRP.put(userid, RandomTool.nextInt(100));
        return JRRP.get(userid);
    }


}
