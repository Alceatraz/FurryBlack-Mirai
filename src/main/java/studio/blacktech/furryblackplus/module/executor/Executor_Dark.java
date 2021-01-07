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
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;


@ComponentHandlerExecutor(
        name = "挑hei选an美liao食li",
        description = "白熊精选美食 1.0版",
        privacy = {
                "获取命令发送人"
        },
        command = "dark",
        usage = {
                "/dark - 使用随机种食材随机生成一些吃完了会死的东西",
                "/dark 数字 - 使用指定种食材随机生成一些没准吃完了不会死的东西",
        }
)
public class Executor_Dark extends EventHandlerExecutor {


    public Executor_Dark(ExecutorInfo INFO) {
        super(INFO);
    }


    private int sizeCookMethod;
    private int sizeIngredient;

    private List<String> COOK_METHOD;
    private List<String> INGREDIENTS;


    private int MAX;


    @Override
    public void init() throws BotException {

        initAppFolder();
        initConfFolder();

        COOK_METHOD = new ArrayList<>();
        INGREDIENTS = new ArrayList<>();

        File FILE_COOK_METHOD = initConfFile("cook_method.txt");
        File FILE_INGREDIENTS = initConfFile("cook_ingredients.txt");

        for (String line : readFile(FILE_COOK_METHOD)) {
            logger.seek("添加方法 - " + line);
            COOK_METHOD.add(line);
        }

        for (String line : readFile(FILE_INGREDIENTS)) {

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
                for (String temp : temp1[1].split(",")) {
                    String trim = temp.trim();
                    INGREDIENTS.add(trim);
                    logger.seek("添加材料 - " + trim);
                }
            } else {
                INGREDIENTS.add(temp1[1]);
                logger.seek("添加材料 - " + temp1[1]);
            }
        }

        sizeCookMethod = COOK_METHOD.size();
        sizeIngredient = INGREDIENTS.size();

    }


    @Override
    public void boot() throws BotException {

    }

    @Override
    public void shut() throws BotException {

    }


    @Override
    public void handleTempMessage(TempMessageEvent event, Command command) {
        event.getSender().sendMessage(generate(command));
    }


    @Override
    public void handleFriendMessage(FriendMessageEvent event, Command command) {
        event.getSender().sendMessage(generate(command));
    }


    @Override
    public void handleGroupMessage(GroupMessageEvent event, Command command) {
        event.getGroup().sendMessage(new At(event.getSender()).plus(generate(command)));
    }


    private String generate(Command command) {

        StringBuilder builder = new StringBuilder();

        int size;

        if (command.hasCommandBody()) {
            try {
                size = Integer.parseInt(command.getParameterSegment(0));
            } catch (Exception exception) {
                builder.append("输入无效, 我觉得你在想peach成全你");
                size = 26;
            }
        } else {
            ThreadLocalRandom random = ThreadLocalRandom.current();
            size = random.nextInt(4) + 2;
        }

        builder.append(generate(size));

        return builder.toString();

    }


    private String generate(int size) {

        ThreadLocalRandom random = ThreadLocalRandom.current();

        List<String> USED_COOK_METHOD = new ArrayList<>(size);
        List<String> USED_INGREDIENTS = new ArrayList<>(size);

        for (int i = 1; i < size; i++) {
            String temp;
            do {
                temp = COOK_METHOD.get(random.nextInt(sizeCookMethod));
            } while (USED_COOK_METHOD.contains(temp));
            USED_COOK_METHOD.add(temp);
        }

        for (int i = 0; i < size; i++) {
            String temp;
            do {
                temp = INGREDIENTS.get(random.nextInt(sizeIngredient));
            } while (USED_INGREDIENTS.contains(temp));
            USED_INGREDIENTS.add(temp);
        }

        size = size - 1;

        StringBuilder builder = new StringBuilder();

        for (int i = 0; i < size; i++) {
            builder.append("\r\n");
            builder.append(USED_INGREDIENTS.remove(0));
            builder.append(USED_COOK_METHOD.remove(0));
        }

        builder.append("\r\n");
        builder.append(USED_INGREDIENTS.remove(0));

        return builder.toString();

    }
}