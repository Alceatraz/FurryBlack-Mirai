package studio.blacktech.furryblackplus.module.executor;

import net.mamoe.mirai.message.FriendMessageEvent;
import net.mamoe.mirai.message.GroupMessageEvent;
import net.mamoe.mirai.message.TempMessageEvent;
import net.mamoe.mirai.message.data.At;
import studio.blacktech.furryblackplus.system.annotation.ComponentHandlerExecutor;
import studio.blacktech.furryblackplus.system.command.Command;
import studio.blacktech.furryblackplus.system.common.exception.BotException;
import studio.blacktech.furryblackplus.system.handler.EventHandlerExecutor;

import java.io.File;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

/**
 * 这功能是三木提的
 */
@ComponentHandlerExecutor(
        name = "外卖吃什么",
        description = "随机选择外卖吃什么 解决选择困难",
        privacy = {
                "获取命令发送人"
        },
        command = "food",
        usage = {
                "/food - 全范围抽取",
                "/food XXX - 某类别抽取",
                "/food list - 列出所有分类",
        }
)
public class Executor_Food extends EventHandlerExecutor {


    public Executor_Food(ExecutorInfo INFO) {
        super(INFO);
    }


    private Food FOOD;


    @Override
    public void init() throws BotException {

        initAppFolder();
        initConfFolder();

        FOOD = new Food();


        File FILE_TAKEOUT = initConfFile("takeout.txt");


        for (String line : readFile(FILE_TAKEOUT)) {

            if (!line.contains(":")) {
                logger.warning("配置无效 " + line);
                continue;
            }

            String[] temp1 = line.split(":");

            if (temp1.length != 2) {
                logger.warning("配置无效 " + line);
                continue;
            }

            if (temp1[1].contains(",")) {
                String[] temp2 = temp1[1].split(",");
                for (String temp3 : temp2) {
                    String trim = temp3.trim();
                    FOOD.add(temp1[0], trim);
                    logger.seek("添加选项 " + temp1[0] + "-> " + trim);
                }
            } else {
                FOOD.add(temp1[0], temp1[1]);
                logger.seek("添加选项 " + temp1[0] + "-> " + temp1[1]);
            }
        }

        FOOD.update();

    }


    @Override
    public void boot() throws BotException {

    }

    @Override
    public void shut() throws BotException {

    }


    @Override
    public void handleTempMessage(TempMessageEvent event, Command command) {

    }


    @Override
    public void handleFriendMessage(FriendMessageEvent event, Command command) {

    }


    @Override
    public void handleGroupMessage(GroupMessageEvent event, Command command) {

        if (command.hasCommandBody()) {

            switch (command.getParameterSegment(0)) {

                case "dark":
                    event.getGroup().sendMessage(new At(event.getSender()).plus("请使用/dark以获取极致美食体验"));
                    break;

                case "list":
                    event.getGroup().sendMessage(new At(event.getSender()).plus(FOOD.getList()));
                    break;

                default:
                    try {
                        int type = Integer.parseInt(command.getParameterSegment(0));
                        event.getGroup().sendMessage(new At(event.getSender()).plus(FOOD.random(type - 1)));
                    } catch (Exception exception) {
                        event.getGroup().sendMessage(new At(event.getSender()).plus("没有这个类别 你在想Peach"));
                    }
            }

        } else {
            event.getGroup().sendMessage(new At(event.getSender()).plus(FOOD.random()));
        }
    }


    public static class Food {

        private int size;
        private String list;
        private final List<String> TYPE;
        private final Map<Integer, Integer> SIZE;
        private final Map<Integer, List<String>> NAME;

        public Food() {
            TYPE = new LinkedList<>();
            SIZE = new LinkedHashMap<>();
            NAME = new LinkedHashMap<>();
        }

        public void add(String type, String name) {
            List<String> temp;
            if (TYPE.contains(type)) {
                int index = TYPE.indexOf(type);
                temp = NAME.get(index);
            } else {
                int size = TYPE.size();
                TYPE.add(type);
                NAME.put(size, temp = new LinkedList<>());
            }
            temp.add(name);
        }

        public void update() {
            size = TYPE.size();
            for (int i = 0; i < size; i++) {
                List<String> list = NAME.get(i);
                SIZE.put(i, list.size());
            }
            int i = 0;
            StringBuilder builder = new StringBuilder();
            builder.append("可用的类别: \r\n");
            for (String name : TYPE) {
                builder.append(i + 1);
                builder.append(" - ");
                builder.append(name);
                builder.append("(");
                builder.append(SIZE.get(i));
                builder.append(")");
                builder.append("\r\n");
                i++;
            }
            builder.setLength(builder.length() - 2);
            list = builder.toString();
        }

        public String random() {
            ThreadLocalRandom random = ThreadLocalRandom.current();
            return random(random.nextInt(size));
        }

        public String random(int type) {
            if (!SIZE.containsKey(type)) throw new IllegalArgumentException();
            ThreadLocalRandom random = ThreadLocalRandom.current();
            int length = SIZE.get(type);
            List<String> list = NAME.get(type);
            return list.get(random.nextInt(length));
        }

        public String getList() {
            return list;
        }
    }
}