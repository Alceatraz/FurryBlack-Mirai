/*
 * Copyright (C) 2021 Alceatraz @ BlackTechStudio
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the BTS Anti Commercial & GNU Affero General.

 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * BTS Anti Commercial & GNU Affero General Public License for more details.
 *
 * You should have received a copy of the BTS Anti Commercial & GNU Affero
 * General Public License along with this program in README or LICENSE.
 */

package studio.blacktech.furryblackplus.core.define;


import studio.blacktech.furryblackplus.common.Api;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;


@Api("命令解析工具")
public final class Command {

    private final String commandName;
    private final String commandBody;
    private final int commandBodyLength;

    private final String[] commandParameters;
    private final int commandParameterLength;

    private final Map<String, String> commandOptions = new LinkedHashMap<>();

    public Command(String message) {

        int split = message.indexOf(' ');

        if (split < 0) {
            this.commandName = message;
            this.commandBody = null;
            this.commandBodyLength = 0;
            this.commandParameters = null;
            this.commandParameterLength = 0;
            return;
        }

        // 命令名按照第一个空格拆分

        this.commandName = message.substring(0, split);
        this.commandBody = message.substring(split + 1);

        // 命令体按照转义规则拆分

        boolean isFiled = false;
        boolean isEscape = false;

        this.commandBodyLength = this.commandBody.length();
        StringBuilder builder = new StringBuilder();
        List<String> commandBodySlice = new LinkedList<>();

        for (int pointer = 0; pointer < this.commandBodyLength; pointer++) {

            char chat = this.commandBody.charAt(pointer);

            switch (chat) {

                case '\\':
                    if (isEscape) builder.append("\\"); // 连续两个\\则视为\
                    isEscape = !isEscape; // 启动对下一个字符的转义
                    break;

                case '`':
                    if (isEscape) {
                        // 开启了转义 不对field状态进行操作
                        builder.append('`');
                    } else {
                        isFiled = !isFiled;
                    }
                    isEscape = false;
                    break;

                case ' ':
                    // field范围内不按空格进行拆分
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

        List<String> commandParameterList = new LinkedList<>();

        for (String slice : commandBodySlice) {
            if (slice.startsWith("--")) {
                slice = slice.substring(2);
                int index = slice.indexOf("=");
                if (index > 0) {
                    this.commandOptions.put(slice.substring(0, index), slice.substring(index + 1)); // --XXX=XXX 选项
                } else {
                    this.commandOptions.put(slice, null); // --XXX 开关
                }
            } else {
                commandParameterList.add(slice); // 提取所有其他内容为参数列表
            }
        }

        this.commandParameters = commandParameterList.toArray(new String[0]);
        this.commandParameterLength = this.commandParameters.length;
    }

    // ===================================================================================

    @Api("从指定位置拼接剩余的内容")
    public String join(int index) {
        if (this.commandParameterLength == 0 || index > this.commandParameterLength) {
            return null;
        }
        StringBuilder builder = new StringBuilder();
        for (int i = index; i < this.commandParameterLength; i++) {
            builder.append(this.commandParameters[index]).append(" ");
        }
        return builder.toString();
    }

    @Api("获取命令名")
    public String getCommandName() {
        return this.commandName;
    }

    @Api("获取命令体")
    public String getCommandBody() {
        return this.commandBody;
    }

    @Api("获取命令体 - 最大字符限制")
    public String getCommandBody(int length) {
        if (length > this.commandBodyLength) {
            return this.commandBody;
        } else {
            return this.commandBody.substring(0, length);
        }
    }

    @Api("获取参数长度")
    public int getCommandBodyLength() {
        return this.commandBodyLength;
    }

    @Api("是否有命令体")
    public boolean hasCommandBody() {
        return this.commandParameterLength > 0;
    }

    @Api("获取参数个数")
    public int getParameterLength() {
        return this.commandParameterLength;
    }

    @Api("获取所有参数")
    public String[] getParameterSegment() {
        return this.commandParameters;
    }

    @Api("获取指定位置的参数")
    public String getParameterSegment(int i) {
        return this.commandParameters[i];
    }

    @Api("是否包含选项")
    public boolean hasSwitch(String name) {
        return this.commandOptions.containsKey(name);
    }

    @Api("获取指定选项")
    public String getSwitch(String name) {
        return this.commandOptions.get(name);
    }

    @Override
    public String toString() {
        return "Command{" +
                   "commandName='" + this.commandName + '\'' +
                   ", commandBody='" + this.commandBody + '\'' +
                   ", commandBodyLength=" + this.commandBodyLength +
                   ", commandParameters=" + Arrays.toString(this.commandParameters) +
                   ", commandParameterLength=" + this.commandParameterLength +
                   ", commandOptions=" + this.commandOptions +
                   '}';
    }
}
