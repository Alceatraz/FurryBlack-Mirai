/*
 * Copyright (C) 2021 Alceatraz @ BlackTechStudio
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the BTS Anti-Commercial & GNU Affero General
 * Public License as published by the Free Software Foundation, either
 * version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * BTS Anti-Commercial & GNU Affero General Public License for more details.
 *
 * You should have received a copy of the BTS Anti-Commercial & GNU Affero
 * General Public License along with this program.
 *
 */


package studio.blacktech.furryblackplus.core.handler.common;


import studio.blacktech.furryblackplus.common.Api;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;


@Api("命令模型")
public final class Command {

    @Api("命令名 第一个空格之前的内容 用于搜索执行器") private final String commandName;
    @Api("命令体 第一个空格之后的内容 用于解析命令模型") private final String commandBody;
    @Api("命令体 的字符数") private final int commandBodyLength;

    @Api("命令模型解析后 所有的参数") private final String[] commandParameters;
    @Api("命令模型解析后 参数的长度") private final int commandParameterLength;

    @Api("命令模型解析后 选项和开关") private final Map<String, String> commandOptions = new LinkedHashMap<>();


    @Api(
        value = "解析命令",
        usage = {
            "命令以空格(U+0020)拆分为片段，index号从0开始，但--开头的参数被视为选项会被剔除，不计入index序号",
            "如果只有一个参数(没有空格)，则为无参数命令，除commandName均为null，commandOptions为空容器",

        },
        attention = {
            "命令名解析过程中`转义不起作用(/`A B`会被拆解为/`A和B`)",
            "选项的分隔符为第一个等号(U+003D)，后续的等号不会被拆分",
            "不包含参数的选项视为开关(--XXX=XXX.XXX --XXX)，getValue为null",
        }
    )
    public Command(String message) {

        int indexOfFirstSpace = message.indexOf(' ');

        if (indexOfFirstSpace < 0) {
            this.commandName = message;
            this.commandBody = null;
            this.commandBodyLength = 0;
            this.commandParameters = null;
            this.commandParameterLength = 0;
            return;
        }

        // 命令名按照第一个空格拆分

        this.commandName = message.substring(0, indexOfFirstSpace);
        this.commandBody = message.substring(indexOfFirstSpace + 1);

        this.commandBodyLength = this.commandBody.length();

        // 命令体按照转义规则拆分

        boolean isFiled = false;
        boolean isEscape = false;

        StringBuilder builder = new StringBuilder();
        List<String> commandBodySlice = new LinkedList<>();

        for (int pointer = 0; pointer < this.commandBodyLength; pointer++) {

            char chat = this.commandBody.charAt(pointer);

            switch (chat) {
                case '\\' -> {
                    if (isEscape) {
                        builder.append("\\"); // 连续两个\\则视为\
                    }
                    isEscape = !isEscape; // 启动对下一个字符的转义
                }
                case '`' -> {
                    if (isEscape) {
                        // 开启了转义 不对field状态进行操作
                        builder.append('`');
                    } else {
                        isFiled = !isFiled;
                    }
                    isEscape = false;
                }
                case ' ' -> {
                    // field范围内不按空格进行拆分
                    if (isFiled) {
                        builder.append(chat);
                    } else {
                        if (builder.length() == 0) {
                            continue;
                        }
                        commandBodySlice.add(builder.toString());
                        builder.setLength(0);
                    }
                    isEscape = false;
                }
                default -> {
                    builder.append(chat);
                    isEscape = false;
                }
            }
        }

        commandBodySlice.add(builder.toString());

        // 对拆分后的命令分析 提取选项和开关

        List<String> commandParameterList = new LinkedList<>();

        for (String slice : commandBodySlice) {
            if (slice.startsWith("--")) {
                slice = slice.substring(2);
                int indexOfEquals = slice.indexOf("=");
                if (indexOfEquals > 0) {
                    this.commandOptions.put(slice.substring(0, indexOfEquals), slice.substring(indexOfEquals + 1)); // --XXX=XXX 选项
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

    @Api(
        value = "从指定位置拼接剩余的内容",
        attention = "index从参数开始数,0代表第一个参数而非命令名,此处所说的index与命令解析时的index序号不同"
    )
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
