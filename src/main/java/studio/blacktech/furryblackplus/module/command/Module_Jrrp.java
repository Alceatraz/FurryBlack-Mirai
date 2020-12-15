package studio.blacktech.furryblackplus.module.command;

import net.mamoe.mirai.message.data.At;
import studio.blacktech.furryblackplus.Driver;
import studio.blacktech.furryblackplus.system.command.GroupCommand;
import studio.blacktech.furryblackplus.system.command.PrivateCommand;
import studio.blacktech.furryblackplus.system.common.exception.BotException;
import studio.blacktech.furryblackplus.system.common.utilties.RandomTool;
import studio.blacktech.furryblackplus.system.module.ModuleExecutor;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class Module_Jrrp extends ModuleExecutor {


    public Module_Jrrp() {
        super(
                new ModuleExecutorInfo(
                        "handler_command_jrrp",
                        "今日运气",
                        "1.0.0",
                        "查看今天的运气值",
                        new String[]{
                                "获取命令发送人",
                                "用户与运气对应表 - 每日UTC+8 00:00 清空"
                        },
                        "jrrp",
                        new String[]{
                                "/jrrp - 查看今日运气"
                        })
        );
    }


    private Thread thread;


    private Map<Long, Integer> JRRP;


    @Override
    public void init() throws BotException {
        JRRP = new HashMap<>();
    }

    @Override
    public void boot() throws BotException {
        logger.info("启动工作线程");
        thread = new Thread(new Worker());
        thread.start();
    }

    @Override
    public void shut() throws BotException {
        try {
            thread.interrupt();
            thread.join();
            logger.info("关闭工作线程");
        } catch (Exception exception) {
            throw new BotException(exception);
        }
    }


    @Override
    public void handleTempMessage(PrivateCommand message) {
        message.getSender().sendMessage(new At(message.getSender())
                .plus("今天的运气是" + getRp(message.getSender().getId()) + "% !!!")
        );
    }

    @Override
    public void handleFriendMessage(PrivateCommand message) {
        message.getSender().sendMessage(new At(message.getSender())
                .plus("今天的运气是" + getRp(message.getSender().getId()) + "% !!!")
        );
    }

    @Override
    public void handleGroupMessage(GroupCommand message) {
        message.getGroup().sendMessage(new At(message.getSender())
                .plus("今天的运气是" + getRp(message.getSender().getId()) + "% !!!")
        );
    }


    private int getRp(long userid) {
        if (JRRP.containsKey(userid)) return JRRP.get(userid);
        JRRP.put(userid, RandomTool.nextInt(100));
        return JRRP.get(userid);
    }


    private class Worker implements Runnable {


        @Override
        public void run() {

            long time;
            Date date;

            do {

                try {

                    //noinspection InfiniteLoopStatement
                    while (true) {

                        date = new Date();
                        time = 86400L;
                        time = time - date.getSeconds();
                        time = time - date.getMinutes() * 60L;
                        time = time - date.getHours() * 3600L;
                        time = time * 1000L;

                        Thread.sleep(time);

                        JRRP.clear();
                    }

                } catch (InterruptedException ignored) {


                }


            } while (Driver.isEnable());


        }
    }
}
