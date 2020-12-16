package studio.blacktech.furryblackplus.module.executor;

import net.mamoe.mirai.message.data.At;
import net.mamoe.mirai.message.data.MessageChain;
import studio.blacktech.furryblackplus.system.annotation.ComponentHandlerExecutor;
import studio.blacktech.furryblackplus.system.command.BasicCommand;
import studio.blacktech.furryblackplus.system.command.FriendCommand;
import studio.blacktech.furryblackplus.system.command.GroupCommand;
import studio.blacktech.furryblackplus.system.command.TempCommand;
import studio.blacktech.furryblackplus.system.common.exception.BotException;
import studio.blacktech.furryblackplus.system.common.utilties.RandomTool;
import studio.blacktech.furryblackplus.system.handler.EventHandlerExecutor;


@ComponentHandlerExecutor(
        name = "占卜",
        description = "抽取一张大阿卡那塔罗牌为某事占卜 - 大失败酱",
        privacy = {
                "获取命令发送人"
        },
        command = "zhan",
        usage = {
                "/zhan XXX - 为某事占卜"
        }
)
public class Executor_Zhan extends EventHandlerExecutor {


    public Executor_Zhan(ExecutorInfo INFO) {
        super(INFO);
    }


    private final static String[] CARD = new String[]{
            "O. THE FOOL 愚者正位\r\n愚蠢 狂躁 挥霍无度 神志不清",
            "O. THE FOOL 愚者逆位\r\n疏忽 缺乏 暮气 无效 虚荣",
            "I. THE MAGICIAN 魔术师正位\r\n手段 灾难 痛苦 损失",
            "I. THE MAGICIAN 魔术师逆位\r\n羞辱 忧虑 精神疾病",
            "II. THE HIGH PRIESTESS 女祭司正位\r\n秘密 神秘 未来不明朗 英明",
            "II. THE HIGH PRIESTESS 女祭司逆位\r\n冲动 狂热 自负 浮于表面",
            "III. THE EMPRESS 皇后正位\r\n丰收 倡议 隐秘 困难 无知",
            "III. THE EMPRESS 皇后逆位\r\n光明 真相 喜悦",
            "IV. THE EMPEROR 皇帝正位\r\n稳定 力量 帮助 保护 信念",
            "IV. THE EMPEROR 皇帝逆位\r\n仁慈 同情 赞许 阻碍 不成熟",
            "V. THE HIEROPHANT 教皇正位\r\n宽恕 束缚 奴役 灵感",
            "V. THE HIEROPHANT 教皇逆位\r\n善解人意 和睦 过度善良 软弱",
            "VI. THE LOVERS 恋人正位\r\n吸引 爱 美丽 通过试炼",
            "VI. THE LOVERS 恋人逆位\r\n失败 愚蠢的设计",
            "VII. THE CHARIOT 战车正位\r\n救助 天意 胜利 复仇",
            "VII. THE CHARIOT 战车逆位\r\n打败 狂暴 吵架 诉讼",
            "VIII. THE STRENGTH 力量正位\r\n能量 行动 勇气 海量",
            "VIII. THE STRENGTH 力量逆位\r\n专断 弱点 滥用力量 不和",
            "IX. THE HERMIT 隐者正位\r\n慎重 叛徒 掩饰 堕落 恶事",
            "IX. THE HERMIT 隐者逆位\r\n隐蔽 害怕 伪装 过分小心",
            "X. THE WHEEL OF FORTUNE 命运之轮正位\r\n命运 好运 成功 幸福",
            "X. THE WHEEL OF FORTUNE 命运之轮逆位\r\n增加 丰富 多余",
            "XI. THE JUSTICE 正义正位\r\n公平 正义 廉洁 行政",
            "XI. THE JUSTICE 正义逆位\r\n偏执 不公 过度俭朴",
            "XII. THE HANGED MAN 吊人正位\r\n智慧 牺牲 审判 细心 眼光",
            "XII. THE HANGED MAN 吊人逆位\r\n自私 群众 人民",
            "XIII. DEATH 死亡正位\r\n终结 死亡 毁灭 腐朽",
            "XIII. DEATH 死亡逆位\r\n惯性 石化 梦游 昏 睡", // 怎 么 多 了 个 空 格 啊
            "XIV. TEMPERANCE 节制正位\r\n经济 适度 节俭 管理 住所",
            "XIV. TEMPERANCE 节制逆位\r\n教会 分离 不幸的组合 冲突的利益",
            "XV. THE DEVIL 恶魔正位\r\n毁坏 暴力 强迫 愤怒 额外努力 死亡",
            "XV. THE DEVIL 恶魔逆位\r\n死亡 弱点 盲目 琐事",
            "XVI. THE TOWER 高塔正位\r\n苦难 废墟 贫乏 耻辱 灾害 逆境 骗局",
            "XVI. THE TOWER 高塔逆位\r\n专断 监禁 受苦 损害",
            "XVII. THE STAR 星星正位\r\n丢失 窃贼 匮乏 放弃 未来的希望",
            "XVII. THE STAR 星星逆位\r\n傲慢 无能 傲气",
            "XVIII. THE MOON 月亮正位\r\n隐藏的敌人 诽谤 危险 黑暗 恐怖 错误",
            "XVIII. THE MOON 月亮逆位\r\n不稳定 易变 骗局 错误",
            "XIX. THE SUN 太阳正位\r\n喜悦 结婚 满意",
            "XIX. THE SUN 太阳逆位\r\n开心 满意",
            "XX. THE LAST JUDGMENT 审判正位\r\n变位 复兴 结果",
            "XX. THE LAST JUDGMENT 审判逆位\r\n弱点 胆怯 天真 决定 熟虑",
            "XXI. THE WORLD 世界正位\r\n成功 道路 航程 换位",
            "XXI. THE WORLD 世界逆位\r\n惯性 固执 停滞 持久",
    };


    @Override
    public void init() throws BotException {
    }

    @Override
    public void boot() throws BotException {
    }

    @Override
    public void shut() throws BotException {
    }


    @Override
    public void handleTempMessage(TempCommand message) {
        message.getSender().sendMessage(chooseCard(message));
    }

    @Override
    public void handleFriendMessage(FriendCommand message) {
        message.getSender().sendMessage(chooseCard(message));
    }

    @Override
    public void handleGroupMessage(GroupCommand message) {
        At at = new At(message.getSender());
        MessageChain temp = at.plus(chooseCard(message));
        message.getGroup().sendMessage(temp);
    }


    private String chooseCard(BasicCommand basicCommand) {
        if (basicCommand.getParameterSection() == 0) {
            return "你不能占卜空气";
        } else {
            return "你因为 " + basicCommand.getCommandBody() + "\r\n抽到了：" + CARD[RandomTool.nextInt(44)];
        }
    }
}
