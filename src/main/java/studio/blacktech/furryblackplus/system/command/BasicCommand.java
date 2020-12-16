package studio.blacktech.furryblackplus.system.command;


import lombok.ToString;
import net.mamoe.mirai.message.data.MessageChain;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


@ToString
public class BasicCommand {


    private final String message;

    private final boolean command;


    private String commandName;
    private String commandBody;

    private int section;
    private String[] parameter;


    private Map<String, String> switchs;


    private static final Pattern pattern = Pattern.compile("^/[a-z]+");


    public BasicCommand(String message) {
        this.message = message;

        Matcher matcher = pattern.matcher(message);


        // 有个傻逼表情叫 /emm
        command = matcher.find();

        if (!command) return;

        int split = message.indexOf(' ');

        if (split < 0) {
            commandName = message.substring(1);
            section = 0;
            return;
        }

        commandName = message.substring(1, split);
        commandBody = message.substring(split + 1);

        boolean isFiled = false;
        boolean isEscape = false;

        StringBuilder builder = new StringBuilder();

        List<String> temp = new LinkedList<>();

        int length = commandBody.length();


        /*
         * /admin exec --module=shui execute `SELECT * FROM \`chat_record\` LIMIT 10` --show
         * 将`作为包裹符号其中的空格不进行拆分
         * 如果需要\ 则需要输入\\
         * 如果需要` 则需要输入\`
         */


        for (int pointer = 0; pointer < length; pointer++) {

            char chat = commandBody.charAt(pointer);

            switch (chat) {

                case '\\':
                    if (isEscape) builder.append("\\"); // 连续两个\\则视为\
                    isEscape = !isEscape; // 启动对下一个字符的转义
                    break;

                case '`':
                    if (isEscape) {
                        // 开启了转义 不对feild状态进行操作
                        builder.append('`');
                    } else {
                        isFiled = !isFiled;
                    }
                    isEscape = false;
                    break;

                case ' ':
                    // feild范围内不按空格进行拆分
                    if (isFiled) {
                        builder.append(chat);
                    } else {
                        if (builder.length() == 0) continue;
                        temp.add(builder.toString());
                        builder.setLength(0);
                    }
                    isEscape = false;
                    break;

                default:
                    builder.append(chat);
                    isEscape = false;
                    break;
            }

        }

        temp.add(builder.toString());

        List<String> result = new LinkedList<>();

        for (String slice : temp) {

            if (slice.startsWith("--")) {

                if (switchs == null) switchs = new LinkedHashMap<>();

                slice = slice.substring(2);
                int index = slice.indexOf("=");

                if (index > 0) {
                    switchs.put(slice.substring(0, index), slice.substring(index + 1)); // --XXX=XXX 选项
                } else {
                    switchs.put(slice, null); // --XXX 开关
                }
            } else {
                result.add(slice); // 提取所有其他内容为参数列表
            }
        }

        parameter = result.toArray(new String[]{});
        section = parameter.length;


        System.out.println("[MESSAGE][DEBUG] " + this.toString());


    }


    public BasicCommand(MessageChain messages) {
        this(messages.contentToString());
    }


    // ===================================================================================


    /**
     * 将消息去掉命令以后 从指定位置拼接
     *
     * @param i index位置
     *
     * @return 拼接后的内容
     */
    public String join(int i) {
        if (section == 0) {
            return "";
        } else {
            StringBuilder builder = new StringBuilder();
            for (; i < section; i++) {
                builder.append(parameter[i]).append(" ");
            }
            return builder.substring(0, builder.length() - 1);
        }
    }


    public String getMessage() {
        return message;
    }


    public boolean isCommand() {
        return command;
    }


    public String getCommandName() {
        return commandName;
    }


    public String getCommandBody() {
        return commandBody;
    }

    public boolean hasCommandBody() {
        return section > 0;
    }

    public int getParameterSection() {
        return section;
    }


    public String[] getParameterSegment() {
        if (parameter == null) return null;
        return parameter;
    }


    public String getParameterSegment(int i) {
        if (parameter == null) return null;
        return parameter[i];
    }


    public boolean hasSwitch(String name) {
        if (switchs == null) return false;
        return switchs.containsKey(name);
    }

    public String getSwitch(String name) {
        return switchs.get(name);
    }


}
