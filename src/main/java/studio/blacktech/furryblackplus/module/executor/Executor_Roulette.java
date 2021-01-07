package studio.blacktech.furryblackplus.module.executor;

import net.mamoe.mirai.contact.Group;
import net.mamoe.mirai.contact.Member;
import net.mamoe.mirai.message.FriendMessageEvent;
import net.mamoe.mirai.message.GroupMessageEvent;
import net.mamoe.mirai.message.TempMessageEvent;
import net.mamoe.mirai.message.data.At;
import net.mamoe.mirai.message.data.Face;
import net.mamoe.mirai.message.data.Message;
import net.mamoe.mirai.message.data.MessageChain;
import net.mamoe.mirai.message.data.PlainText;
import studio.blacktech.furryblackplus.system.annotation.ComponentHandlerExecutor;
import studio.blacktech.furryblackplus.system.command.Command;
import studio.blacktech.furryblackplus.system.common.exception.BotException;
import studio.blacktech.furryblackplus.system.common.logger.LoggerX;
import studio.blacktech.furryblackplus.system.handler.EventHandlerExecutor;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;


@ComponentHandlerExecutor(
        name = "俄罗斯轮盘赌",
        description = "你看这子弹又尖又长，这名单又大又宽",
        privacy = {
                "获取命令发送人",
                "缓存群-成员-回合 结束后丢弃"
        },
        users = false,
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
    public void handleTempMessage(TempMessageEvent event, Command command) {

    }


    @Override
    public void handleFriendMessage(FriendMessageEvent event, Command command) {

    }


    @Override
    public void handleGroupMessage(GroupMessageEvent event, Command command) {

        Group group = event.getGroup();

        if (!command.hasCommandBody()) {
            group.sendMessage(new At(event.getSender()).plus("你必须下注"));
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


        if (round.join(event, command)) {


            if (round.isSinglePlayer()) {


                RouletteRound.PlayerJetton loser = round.getGamblers().get(0);


                group.sendMessage(new At(loser.getMember()).plus("好的，没有问题，成全你"));

                group.sendMessage(new Face(169).plus("\uD83D\uDCA5\r\n"));
                group.sendMessage(new Face(169).plus("\uD83D\uDCA5\r\n"));
                group.sendMessage(new Face(169).plus("\uD83D\uDCA5\r\n"));
                group.sendMessage(new Face(169).plus("\uD83D\uDCA5\r\n"));
                group.sendMessage(new Face(169).plus("\uD83D\uDCA5\r\n"));
                group.sendMessage(new Face(169).plus("\uD83D\uDCA5\r\n"));

                group.sendMessage("目标已被击毙: " + loser.getMember().getNameCard() + "(" + loser.getMember().getId() + ") 掉落了以下物品: " + round.getAllJetton(loser.getMember().getId()));


            } else {


                int bullet = ThreadLocalRandom.current().nextInt(6);


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

                group.sendMessage("目标已被击毙: " + loser.getMember().getNameCard() + "(" + loser.getMember().getId() + ") 掉落了以下物品: " + round.getAllJetton(loser.getMember().getId()));

            }


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
                String jetton = temp.getJetton();
                if (jetton.length() > 15) {
                    builder.append(jetton, 0, 12).append("...");
                } else {
                    builder.append(jetton);
                }
                builder.append("\r\n");
            }

            for (; i < 6; i++) {
                builder.append(ICON[i]);
                builder.append(" - 等待加入\r\n");
            }

            builder.append("剩余时间 - ");
            builder.append(LoggerX.formatTime("mm:ss", round.getTime().getTime() - current));


            MessageChain temp = new Face(169).plus(builder.toString());
            event.getGroup().sendMessage(temp);

        }

    }


    private static class RouletteRound {


        private boolean hint = true;


        private final Date time = new Date(System.currentTimeMillis() + 600000);


        private final List<PlayerJetton> gamblers = new ArrayList<>(6);


        public boolean join(GroupMessageEvent event, Command command) {

            if (gamblers.size() >= 6) {
                MessageChain temp = new At(event.getSender()).plus("❌ 对局已满");
                event.getGroup().sendMessage(temp);
                return false;
            }

            if (hint && gamblers.stream().anyMatch(item -> item.getMember().getId() == event.getSender().getId())) {
                MessageChain temp = new At(event.getSender()).plus("✔️ 经科学证实重复下注可有效增加被枪毙的机率");
                event.getGroup().sendMessage(temp);
                hint = false;
            }

            gamblers.add(new PlayerJetton(event.getSender(), command.getCommandBody(200)));
            return gamblers.size() == 6;
        }


        public Date getTime() {
            return time;
        }


        public List<PlayerJetton> getGamblers() {
            return gamblers;
        }


        public boolean isSinglePlayer() {
            long id = gamblers.get(0).getMember().getId();
            for (int i = 1; i < 6; i++) {
                long current = gamblers.get(i).getMember().getId();
                if (id != current) return false;
            }
            return true;
        }


        public String getAllJetton(long id) {
            List<PlayerJetton> jettons = gamblers.stream().filter(item -> item.getMember().getId() == id).collect(Collectors.toList());
            StringBuilder builder = new StringBuilder();
            for (RouletteRound.PlayerJetton jetton : jettons) {
                builder.append("\r\n");
                builder.append(jetton.getJetton());
            }
            return builder.toString();
        }


        private static class PlayerJetton {

            private final Member member;
            private final String jetton;


            public PlayerJetton(Member member, String jetton) {
                this.member = member;
                this.jetton = jetton;
            }

            public Member getMember() {
                return member;
            }

            public String getJetton() {
                return jetton;
            }

        }


    }


}

