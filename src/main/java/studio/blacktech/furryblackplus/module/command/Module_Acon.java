package studio.blacktech.furryblackplus.module.command;

import net.mamoe.mirai.contact.Group;
import studio.blacktech.furryblackplus.system.command.GroupCommand;
import studio.blacktech.furryblackplus.system.command.PrivateCommand;
import studio.blacktech.furryblackplus.system.common.exception.BotException;
import studio.blacktech.furryblackplus.system.module.ModuleExecutor;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;

public class Module_Acon extends ModuleExecutor {


    public Module_Acon() {
        super(
                new ModuleExecutorInfo(
                        "handler_command_acon",
                        "空调",
                        "1.0.0",
                        "本群空调",
                        new String[]{
                                "按群存储耗电量",
                                "按群存储耗工作模式",
                                "按群存储上次更改模式的时间戳"
                        },
                        "acon",
                        new String[]{
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
                        })
        );
    }


    private Map<Long, BigInteger> CONSUMPTION;
    private Map<Long, Long> LASTCHANGED;
    private Map<Long, Long> WORKINGMODE;


    @Override
    public void init() throws BotException {
        CONSUMPTION = new HashMap<>();
        LASTCHANGED = new HashMap<>();
        WORKINGMODE = new HashMap<>();
    }

    @Override
    public void boot() throws BotException {

    }

    @Override
    public void shut() throws BotException {

    }


    @Override
    public void handleTempMessage(PrivateCommand message) {

    }

    @Override
    public void handleFriendMessage(PrivateCommand message) {

    }

    @Override
    public synchronized void handleGroupMessage(GroupCommand message) {

        Group group = message.getGroup();

        long elapseTime;
        long currentTime = System.currentTimeMillis() / 1000;
        long groupId = group.getId();

        if (!CONSUMPTION.containsKey(groupId)) {
            CONSUMPTION.put(groupId, BigInteger.ZERO);
            LASTCHANGED.put(groupId, currentTime);
            WORKINGMODE.put(groupId, 0L);
        }

        if (message.getParameterSection() > 0) {

            BigInteger powerConsumption = CONSUMPTION.get(groupId);

            long lastChangModeTime = LASTCHANGED.get(groupId);
            long workingmode = WORKINGMODE.get(groupId);

            elapseTime = currentTime - lastChangModeTime;
            boolean isChangeMode = true;

            switch (message.getParameterSegment(0)) {

                case "off":
                    group.sendMessage("空调已关闭");
                    WORKINGMODE.put(groupId, 1L);
                    break;

                case "dry":
                    group.sendMessage("切换至除湿模式");
                    WORKINGMODE.put(groupId, 5880L);
                    break;

                case "wet":
                    group.sendMessage("切换至加湿模式");
                    WORKINGMODE.put(groupId, 5880L);
                    break;

                case "cold":
                    group.sendMessage("切换至制冰模式 -20°");
                    WORKINGMODE.put(groupId, 14700L);
                    break;

                case "cool":
                    group.sendMessage("切换至制冷模式 26.5°");
                    WORKINGMODE.put(groupId, 7350L);
                    break;

                case "warm":
                    group.sendMessage("切换至制热模式 25.5°");
                    WORKINGMODE.put(groupId, 7350L);
                    break;

                case "bake":
                    group.sendMessage("切换至烘烤模式 285°");
                    WORKINGMODE.put(groupId, 14700L);
                    break;

                case "burn":
                    group.sendMessage("切换至烧烤模式 960°");
                    WORKINGMODE.put(groupId, 22050L);
                    break;

                case "fire":
                    group.sendMessage("切换至焚化模式 1,200°");
                    WORKINGMODE.put(groupId, 29400L);
                    break;

                case "c2h2":
                    group.sendMessage("切换至乙炔炬模式 3,300°");
                    WORKINGMODE.put(groupId, 33075L);
                    break;

                case "argon":
                    group.sendMessage("切换至氩气弧模式 7,550°");
                    WORKINGMODE.put(groupId, 36750L);
                    break;

                case "plasma":
                    group.sendMessage("切换至等离子模式 23,500°");
                    WORKINGMODE.put(groupId, 44100L);
                    break;

                case "nova":
                    group.sendMessage("切换至新星模式 1,000,000°");
                    WORKINGMODE.put(groupId, 7350000L);
                    break;

                case "cfnuke":
                    group.sendMessage("切换至冷核模式 100,000,000°");
                    WORKINGMODE.put(groupId, 29400000L);
                    break;

                case "trnuke":
                    group.sendMessage("切换至热核模式 120,000,000°");
                    WORKINGMODE.put(groupId, 33075000L);
                    break;

                case "tfnuke":
                    group.sendMessage("切换至三相热核模式 150,000,000°");
                    WORKINGMODE.put(groupId, 44100000L);
                    break;

                case "ianova":
                    group.sendMessage("切换至Ia星爆发模式 800,000,000°");
                    WORKINGMODE.put(groupId, 294000000L);
                    break;

                case "ibnova":
                    group.sendMessage("切换至Ib新星爆发模式 2,600,000,000°");
                    WORKINGMODE.put(groupId, 330750000L);
                    break;

                case "icnova":
                    group.sendMessage("切换至Ic新星爆发模式 2,800,000,000°");
                    WORKINGMODE.put(groupId, 441000000L);
                    break;

                case "iinova":
                    group.sendMessage("切换至II新星爆发模式 3,000,000,000°");
                    WORKINGMODE.put(groupId, 514500000L);
                    break;

                case "samrage":
                    group.sendMessage("父王之怒 10,000,000,000,000,000,000,000,000,000°");
                    WORKINGMODE.put(groupId, 73500000000L);
                    break;

                case "samrape":
                    group.sendMessage("父王之怒 -273.16°");
                    WORKINGMODE.put(groupId, 73500000000L);
                    break;

                default:
                    powerConsumption = powerConsumption.add(BigInteger.valueOf(elapseTime * workingmode));
                    isChangeMode = false;

                    // @formatter:off

                   group.sendMessage(

                            String.format("累计共耗电：%skW(%s)度\r\n群主须支付：%s元",

                                    powerConsumption.divide(BigInteger.valueOf(1000)).toString(),
                                    powerConsumption.divide(BigInteger.valueOf(3600000L)).toString(),
                                    powerConsumption.divide(BigInteger.valueOf(1936800L)).toString()

                            ));

                    // @formatter:on

                    break;


            }

            if (isChangeMode) powerConsumption = powerConsumption.add(BigInteger.valueOf(elapseTime * workingmode));

            CONSUMPTION.put(groupId, powerConsumption);
            LASTCHANGED.put(groupId, currentTime);

        }
    }


}
