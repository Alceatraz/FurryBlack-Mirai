package studio.blacktech.furryblackplus.module.executor;

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.mamoe.mirai.contact.Group;
import net.mamoe.mirai.contact.Member;
import net.mamoe.mirai.message.data.At;
import net.mamoe.mirai.message.data.Face;
import net.mamoe.mirai.message.data.Message;
import net.mamoe.mirai.message.data.MessageChain;
import net.mamoe.mirai.message.data.PlainText;
import studio.blacktech.furryblackplus.system.annotation.ComponentHandlerExecutor;
import studio.blacktech.furryblackplus.system.command.FriendCommand;
import studio.blacktech.furryblackplus.system.command.GroupCommand;
import studio.blacktech.furryblackplus.system.command.TempCommand;
import studio.blacktech.furryblackplus.system.common.exception.BotException;
import studio.blacktech.furryblackplus.system.common.logger.LoggerX;
import studio.blacktech.furryblackplus.system.common.utilties.RandomTool;
import studio.blacktech.furryblackplus.system.handler.EventHandlerExecutor;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;


@ComponentHandlerExecutor(
        name = "俄罗斯轮盘赌",
        description = "你看这子弹又尖又长，这名单又大又宽",
        privacy = {
                "获取命令发送人",
                "缓存群-成员-回合 结束后丢弃"
        },
        command = "roulette",
        usage = {
                "/roulette 筹码 - 加入或者发起一局俄罗斯轮盘赌 重复下注可增加被枪毙的几率"
        }
)
public class Executor_Roulette extends EventHandlerExecutor {


    public Executor_Roulette(ExecutorInfo INFO) {
        super(INFO);
    }


    private final static String[] ICON = {
            "1️⃣", "2️⃣", "3️⃣", "4️⃣", "5️⃣", "6️⃣"
    };


    private HashMap<Long, RouletteRound> rounds;


    @Override
    public void init() throws BotException {
        rounds = new HashMap<>();
    }

    @Override
    public void boot() {
    }

    @Override
    public void shut() {
    }


    @Override
    public void handleTempMessage(TempCommand message) {
        message.getSender().sendMessage("仅支持群");
    }

    @Override
    public void handleFriendMessage(FriendCommand message) {
        message.getSender().sendMessage("仅支持群");
    }

    @Override
    public synchronized void handleGroupMessage(GroupCommand message) {

        Group group = message.getGroup();

        if (!message.hasCommandBody()) {
            group.sendMessage(new At(message.getSender()).plus("你必须下注"));
            return;
        }

        RouletteRound round;

        long current = System.currentTimeMillis();


        if (rounds.containsKey(group.getId())) {
            round = rounds.get(group.getId());
            if (round.getTime().getTime() - current < 0) {
                rounds.remove(group.getId());
                rounds.put(group.getId(), round = new RouletteRound());
            }
        } else {
            rounds.put(group.getId(), round = new RouletteRound());
        }


        if (round.join(message)) {


            int bullet = RandomTool.nextInt(6);


            Message messages = new PlainText("名单已凑齐 装填子弹中\r\n");

            RouletteRound.PlayerJetton loser = round.getGamblers().get(bullet);

            At at = new At(loser.getMember());

            for (int i = 0; i < 6; i++) {

                RouletteRound.PlayerJetton temp = round.getGamblers().get(i);

                messages = messages.plus(ICON[i]).plus(" " + temp.getMember().getNameCard() + " ").plus(new Face(169));

                if (i == bullet) {
                    messages = messages.plus("\uD83D\uDCA5\r\n"); // 💥 "\uD83D\uDCA5"
                } else {
                    messages = messages.plus("\r\n");
                }

            }

            messages = messages.plus("\r\n");
            messages = messages.plus(at);

            group.sendMessage(messages);

            group.sendMessage("目标已被击毙: " + loser.getMember().getNameCard() + "(" + loser.getMember().getId() + ") \r\n掉落了物品: " + loser.getJetton());

            rounds.remove(group.getId());

        } else {


            StringBuilder builder = new StringBuilder();


            builder.append("俄罗斯轮盘 - 当前人数 (");
            builder.append(round.getGamblers().size());
            builder.append("/6)\r\n");

            int i = 0;

            int size = round.getGamblers().size();

            for (; i < size; i++) {
                RouletteRound.PlayerJetton temp = round.getGamblers().get(i);
                builder.append(ICON[i]);
                builder.append(" ");
                builder.append(temp.getMember().getId());
                builder.append(" - ");
                builder.append(temp.getJetton());
                builder.append("\r\n");
            }

            for (; i < 6; i++) {
                builder.append(ICON[i]);
                builder.append(" - 等待加入\r\n");
            }

            builder.append("剩余时间 - ");
            builder.append(LoggerX.formatTime("mm:ss", round.getTime().getTime() - current));


            MessageChain temp = new Face(169).plus(builder.toString());
            message.getGroup().sendMessage(temp);

        }

    }


    private static class RouletteRound {


        private boolean hint = true;


        private final Date time = new Date(System.currentTimeMillis() + 600000);


        private final List<PlayerJetton> gamblers = new ArrayList<>(6);


        public boolean join(GroupCommand message) {

            if (gamblers.size() >= 6) {
                MessageChain temp = new At(message.getSender()).plus("❌ 对局已满");
                message.getGroup().sendMessage(temp);
                return false;
            }

            if (hint && gamblers.stream().anyMatch(item -> item.getMember().getId() == message.getSender().getId())) {
                MessageChain temp = new At(message.getSender()).plus("✔️ 经科学证实重复下注可有效增加被枪毙的机率");
                message.getGroup().sendMessage(temp);
                hint = false;
            }

            gamblers.add(new PlayerJetton(message.getSender(), message.getCommandBody()));
            return gamblers.size() == 6;
        }


        public Date getTime() {
            return time;
        }


        public List<PlayerJetton> getGamblers() {
            return gamblers;
        }


        @Getter
        @AllArgsConstructor
        private static class PlayerJetton {
            private final Member member;
            private final String jetton;
        }


    }


}

