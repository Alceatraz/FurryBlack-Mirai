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

package studio.blacktech.furryblackplus.core.common.logger;


public class StringTool {


    public static String hexHash(Object object) {
        return Integer.toHexString(object.hashCode()).toUpperCase();
    }


    public static String dumpUnicode(String content) {
        StringBuilder builder = new StringBuilder();
        int length = content.length();
        for (int i = 0; i < length; i++) {
            char chat = content.charAt(i);
            builder.append("\\u");
            if (chat < 0x000F) {
                builder.append("000");
            } else if (chat < 0x00FF) {
                builder.append("00");
            } else if (chat < 0x0FFF) {
                builder.append("0");
            }
            String temp = Integer.toHexString(chat);
            builder.append(temp);
        }
        return builder.toString();
    }


}
