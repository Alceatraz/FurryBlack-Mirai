package studio.blacktech.furryblackplus.module.executor;

import net.mamoe.mirai.contact.Group;
import studio.blacktech.furryblackplus.system.annotation.ComponentHandlerExecutor;
import studio.blacktech.furryblackplus.system.command.FriendCommand;
import studio.blacktech.furryblackplus.system.command.GroupCommand;
import studio.blacktech.furryblackplus.system.command.TempCommand;
import studio.blacktech.furryblackplus.system.handler.EventHandlerExecutor;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;


@ComponentHandlerExecutor(
        name = "空调",
        description = "本群空调",
        privacy = {
                "按群存储耗电量",
                "按群存储耗工作模式",
                "按群存储上次更改模式的时间戳"
        },
        users = false,
        command = "acon",
        usage = {
                "/acon cost - 耗电量",
                "/acon off - 关机",
                "/acon wet - 加湿",
                "/acon dry - 除湿",
                "/acon cold - 制冰模式",
                "/acon cool - 制冷模式",
                "/acon warm - 制热模式",
                "/acon bake - 烘烤模式",
                "/acon burn - 烧烤模式",
                "/acon fire - 焚化模式",
                "/acon c2h2 - 乙炔炬模式",
                "/acon argon - 氩气引弧模式",
                "/acon plasma - 等离子模式",
                "/acon nova - 点亮一颗新星",
                "/acon cfnuke - 点燃一颗冷核武器",
                "/acon trnuke - 点燃一颗热核武器",
                "/acon tpnuke - 点燃一颗三相热核弹",
                "/acon ianova - Ia级超新星吸积引燃",
                "/acon ibnova - Ib级超新星吸积引燃",
                "/acon icnova - Ic级超新星吸积引燃",
                "/acon iinova - II级超新星吸积引燃",
                "/acon ~!C??? - Fy:????",
                "/acon ~!R[?? - FT//s??"
        }
)
public class Executor_Acon extends EventHandlerExecutor {


    public Executor_Acon(ExecutorInfo INFO) {
        super(INFO);
    }


    private final static BigInteger $1000 = BigInteger.valueOf(1000);
    private final static BigInteger $HOUR = BigInteger.valueOf(3600000);
    private final static BigInteger $FACT = BigInteger.valueOf(1980000);


    private Map<Long, AirCondition> AIR_CONDITIONS;


    @Override
    public void init() {
        AIR_CONDITIONS = new HashMap<>();
    }


    @Override
    public void boot() {

    }


    @Override
    public void shut() {

    }


    @Override
    public void handleTempMessage(TempCommand message) {

    }


    @Override
    public void handleFriendMessage(FriendCommand message) {

    }


    @Override
    public synchronized void handleGroupMessage(GroupCommand message) {


        long current = System.currentTimeMillis() / 1000;


        Group group = message.getGroup();
        long groupId = group.getId();


        AirCondition airCondition;


        if (AIR_CONDITIONS.containsKey(groupId)) {
            airCondition = AIR_CONDITIONS.get(groupId);
        } else {
            AIR_CONDITIONS.put(groupId, airCondition = new AirCondition());
        }


        if (message.hasCommandBody()) {


            switch (message.getParameterSegment(0)) {

                case "off":
                    group.sendMessage("空调已关闭");
                    airCondition.changeMode(1L);
                    return;

                case "dry":
                    group.sendMessage("切换至除湿模式");
                    airCondition.changeMode(5880L);
                    return;

                case "wet":
                    group.sendMessage("切换至加湿模式");
                    airCondition.changeMode(5880L);
                    return;

                case "cold":
                    group.sendMessage("切换至制冰模式 -20°");
                    airCondition.changeMode(14700L);
                    return;

                case "cool":
                    group.sendMessage("切换至制冷模式 26.5°");
                    airCondition.changeMode(7350L);
                    break;

                case "warm":
                    group.sendMessage("切换至制热模式 25.5°");
                    airCondition.changeMode(7350L);
                    break;

                case "bake":
                    group.sendMessage("切换至烘烤模式 285°");
                    airCondition.changeMode(14700L);
                    return;

                case "burn":
                    group.sendMessage("切换至烧烤模式 960°");
                    airCondition.changeMode(22050L);
                    return;

                case "fire":
                    group.sendMessage("切换至焚化模式 1,200°");
                    airCondition.changeMode(29400L);
                    return;

                case "c2h2":
                    group.sendMessage("切换至乙炔炬模式 3,300°");
                    airCondition.changeMode(33075L);
                    return;

                case "argon":
                    group.sendMessage("切换至氩气弧模式 7,550°");
                    airCondition.changeMode(36750L);
                    return;

                case "plasma":
                    group.sendMessage("切换至等离子模式 23,500°");
                    airCondition.changeMode(44100L);
                    return;

                case "nova":
                    group.sendMessage("切换至新星模式 1,000,000°");
                    airCondition.changeMode(7350000L);
                    return;

                case "cfnuke":
                    group.sendMessage("切换至冷核模式 100,000,000°");
                    airCondition.changeMode(29400000L);
                    return;

                case "trnuke":
                    group.sendMessage("切换至热核模式 120,000,000°");
                    airCondition.changeMode(33075000L);
                    return;

                case "tfnuke":
                    group.sendMessage("切换至三相热核模式 150,000,000°");
                    airCondition.changeMode(44100000L);
                    return;

                case "ianova":
                    group.sendMessage("切换至Ia星爆发模式 800,000,000°");
                    airCondition.changeMode(294000000L);
                    return;

                case "ibnova":
                    group.sendMessage("切换至Ib新星爆发模式 2,600,000,000°");
                    airCondition.changeMode(330750000L);
                    return;

                case "icnova":
                    group.sendMessage("切换至Ic新星爆发模式 2,800,000,000°");
                    airCondition.changeMode(441000000L);
                    return;

                case "iinova":
                    group.sendMessage("切换至II新星爆发模式 3,000,000,000°");
                    airCondition.changeMode(514500000L);
                    return;

                case "samrage":
                    group.sendMessage("父王之怒 10,000,000,000,000,000,000,000,000,000°");
                    airCondition.changeMode(73500000000L);
                    return;

                case "samrape":
                    group.sendMessage("父王之怒 -273.16°");
                    airCondition.changeMode(73500000000L);
                    return;

                default:
                    group.sendMessage(airCondition.cost());

            }

        }

        group.sendMessage(airCondition.cost());

    }


    public static class AirCondition {


        private long mode = 0;
        private long time = 0;
        private BigInteger cost = BigInteger.ZERO;


        /**
         * 将之前的运行模式计算为价格
         */
        private void updateCost() {
            long current = System.currentTimeMillis();
            long duration = current - this.time;
            duration = duration / 1000;
            BigInteger a = BigInteger.valueOf(duration);
            BigInteger b = BigInteger.valueOf(mode);
            BigInteger c = a.multiply(b);
            cost = cost.add(c);
            this.time = current;
        }


        /**
         * 更改模式
         *
         * @param mode 新的模式
         */
        public void changeMode(long mode) {
            this.updateCost();
            this.mode = mode;
        }


        /**
         * 查询价格 即使没有更新模式也要将之前的价格累加
         *
         * @return 消息
         */
        public String cost() {
            this.updateCost();
            return "累计共耗电：" + cost.divide($1000) + "kW(" + cost.divide($HOUR) + ")度\r\n群主须支付: " + cost.divide($FACT) + "元";
        }
    }


}