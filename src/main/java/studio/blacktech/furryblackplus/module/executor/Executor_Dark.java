package studio.blacktech.furryblackplus.module.executor;

import studio.blacktech.furryblackplus.system.annotation.ComponentHandlerExecutor;
import studio.blacktech.furryblackplus.system.command.FriendCommand;
import studio.blacktech.furryblackplus.system.command.GroupCommand;
import studio.blacktech.furryblackplus.system.command.TempCommand;
import studio.blacktech.furryblackplus.system.common.exception.BotException;
import studio.blacktech.furryblackplus.system.handler.EventHandlerExecutor;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;


@ComponentHandlerExecutor(
        name = "挑hei选an美liao食li",
        description = "白熊精选美食 1.0版",
        privacy = {
                "获取命令发送人"
        },
        command = "dark",
        usage = {
                "/dark XXX - 使用XXX种食材随机生成一些没准吃完了不会死的东西",
        }
)
public class Executor_Dark extends EventHandlerExecutor {


    public Executor_Dark(ExecutorInfo INFO) {
        super(INFO);
    }


    private List<List<String>> TAKEOUT;


    private List<String> METHOD;
    private Map<Integer, List<String>> FOODS;


    private int MAX;


    @Override
    public void init() throws BotException {

        initAppFolder();
        initConfFolder();

        METHOD = new ArrayList<>();
        FOODS = new LinkedHashMap<>();

        File FILE_COOK_METHOD = initConfFile("cook_method.txt");
        File FILE_INGREDIENTS = initConfFile("cook_ingredients.txt");


        for (String line : readFile(FILE_COOK_METHOD)) {
            logger.seek("添加方法 - " + line);
            METHOD.add(line);
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

            List<String> tempList;

            int probability = Integer.parseInt(temp1[0]);

            if (FOODS.containsKey(probability)) {
                tempList = FOODS.get(probability);
            } else {
                FOODS.put(probability, tempList = new LinkedList<>());
            }

            if (temp1[1].contains(",")) {
                String[] temp2 = temp1[1].split(",");
                for (String temp3 : temp2) {
                    String trim = temp3.trim();
                    tempList.add(trim);
                    logger.seek("添加原料 " + probability + "-> " + trim);
                }
            } else {
                tempList.add(temp1[1]);
                logger.seek("添加原料 " + probability + "-> " + temp1[1]);
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
    public void handleTempMessage(TempCommand message) {

    }

    @Override
    public void handleFriendMessage(FriendCommand message) {

    }

    @Override
    public void handleGroupMessage(GroupCommand message) {


        ThreadLocalRandom random = ThreadLocalRandom.current();

        int size = random.nextInt(3) + 2;

        List<String> used = new ArrayList<>(size);

        for (int i = 0; i < size; i++) {
            String temp;
            do {
                temp = METHOD.get(random.nextInt(METHOD.size()));
            } while (used.contains(temp));
            used.add(temp);
        }

        for (int i = 0; i < size; i++) {
            String temp;
            do {
                temp = METHOD.get(random.nextInt(METHOD.size()));
            } while (used.contains(temp));
            used.add(temp);
        }

    }
}