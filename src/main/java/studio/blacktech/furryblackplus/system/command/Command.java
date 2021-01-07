package studio.blacktech.furryblackplus.system.command;


import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;


public class Command {


    // =========================================================================


    private final String commandName;
    private final String commandBody;
    private final int commandBodyLength;

    private final String[] commandParameters;
    private final int commandParameterLength;

    private final Map<String, String> commandOptions = new LinkedHashMap<>();


    // =========================================================================


    public Command(String message) {

        int split = message.indexOf(' ');

        if (split < 0) {

            commandName = message;

            commandBody = null;
            commandBodyLength = 0;

            commandParameters = null;
            commandParameterLength = 0;

            return;

        }

        // 命令名按照第一个空格拆分

        commandName = message.substring(0, split);
        commandBody = message.substring(split + 1);

        // 命令体按照转义规则拆分

        boolean isFiled = false;
        boolean isEscape = false;

        commandBodyLength = commandBody.length();
        StringBuilder builder = new StringBuilder();
        List<String> commandBodySlice = new LinkedList<>();

        for (int pointer = 0; pointer < commandBodyLength; pointer++) {

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
                        commandBodySlice.add(builder.toString());
                        builder.setLength(0);
                    }
                    isEscape = false;
                    break;

                default:
                    builder.append(chat);
                    isEscape = false;
            }
        }

        commandBodySlice.add(builder.toString());
        builder.setLength(0);

        // 对拆分后的命令分析 提取选项和开关

        List<String> commandParameters = new LinkedList<>();

        for (String slice : commandBodySlice) {
            if (slice.startsWith("--")) {
                slice = slice.substring(2);
                int index = slice.indexOf("=");
                if (index > 0) {
                    commandOptions.put(slice.substring(0, index), slice.substring(index + 1)); // --XXX=XXX 选项
                } else {
                    commandOptions.put(slice, null); // --XXX 开关
                }
            } else {
                commandParameters.add(slice); // 提取所有其他内容为参数列表
            }
        }

        this.commandParameters = commandParameters.toArray(new String[0]);
        this.commandParameterLength = this.commandParameters.length;

    }


    // ===================================================================================


    public String getCommandName() {
        return commandName;
    }


    public String getCommandBody() {
        return commandBody;
    }


    public String getCommandBody(int length) {
        if (length > commandBodyLength) {
            return commandBody;
        } else {
            return commandBody.substring(0, length);
        }
    }

    public boolean hasCommandBody() {
        return commandParameterLength > 0;
    }

    public int getCommandParameterLength() {
        return commandParameterLength;
    }


    public String[] getParameterSegment() {
        return commandParameters;
    }


    public String getParameterSegment(int i) {
        if (commandParameters == null) return null;
        return commandParameters[i];
    }


    public boolean hasSwitch(String name) {
        return commandOptions.containsKey(name);
    }


    public String getSwitch(String name) {
        return commandOptions.get(name);
    }


    @Override
    public String toString() {
        return "Command{" +
                       "commandName='" + commandName + '\'' +
                       ", commandBody='" + commandBody + '\'' +
                       ", commandBodyLength=" + commandBodyLength +
                       ", commandParameters=" + Arrays.toString(commandParameters) +
                       ", commandParameterLength=" + commandParameterLength +
                       ", commandOptions=" + commandOptions +
                       '}';
    }
}
