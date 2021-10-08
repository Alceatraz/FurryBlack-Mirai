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


import studio.blacktech.furryblackplus.common.Api;
import studio.blacktech.furryblackplus.core.common.exception.BotException;
import studio.blacktech.furryblackplus.core.common.logger.base.LoggerX;
import studio.blacktech.furryblackplus.core.common.logger.support.NullLogger;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;


@Api(
    value = "日志工厂"
)
public final class LoggerXFactory {


    private static Class<? extends LoggerX> defaultLogger;
    private static final Map<String, Class<? extends LoggerX>> registry = new HashMap<>();

    public static void register(Class<? extends LoggerX> clazz) {
        String name = clazz.getSimpleName();
        Class<? extends LoggerX> exist = registry.get(name);
        if (exist != null) {
            throw new BotException("This Logger already registered");
        }
        registry.put(name, clazz);
    }


    public static LoggerX newLogger(Class<?> clazz) {
        if (defaultLogger == null) {
            return new NullLogger(clazz);
        }
        LoggerX loggerX;
        try {
            loggerX = defaultLogger.getConstructor(Class.class).newInstance(clazz);
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException exception) {
            throw new RuntimeException(exception);
        }
        return loggerX;
    }

    public static LoggerX newLogger(String name) {
        if (defaultLogger == null) {
            return new NullLogger(name);
        }
        LoggerX loggerX;
        try {
            loggerX = defaultLogger.getConstructor(String.class).newInstance(name);
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException exception) {
            throw new RuntimeException(exception);
        }
        return loggerX;
    }

    public static void setDefault(Class<? extends LoggerX> provider) {
        defaultLogger = provider;
    }

    public static void setDefault(String provider) {
        defaultLogger = registry.get(provider);
    }

    public static String getDefault() {
        return defaultLogger.getSimpleName();
    }
}
