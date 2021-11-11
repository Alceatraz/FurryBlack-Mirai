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


import studio.blacktech.furryblackplus.FurryBlack;
import studio.blacktech.furryblackplus.common.Api;
import studio.blacktech.furryblackplus.core.common.exception.BotException;
import studio.blacktech.furryblackplus.core.common.exception.ModuleException;
import studio.blacktech.furryblackplus.core.common.exception.moduels.boot.BootException;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URLClassLoader;
import java.nio.file.Paths;
import java.util.Properties;


@Api("基础模块类")
public abstract class AbstractEventHandler extends BasicModuleUtilities {


    private boolean internalInitLock;


    private URLClassLoader exclusiveClassLoader;


    // =================================================================================================================


    @Api("插件目录对象") protected File FOLDER_ROOT;
    @Api("配置目录对象") protected File FOLDER_CONF;
    @Api("数据目录对象") protected File FOLDER_DATA;
    @Api("日志目录对象") protected File FOLDER_LOGS;
    @Api("配置文件对象") protected File FILE_CONFIG;
    @Api("配置文件对象") protected Properties CONFIG;

    @Api("初始化过配置文件") protected boolean NEW_CONFIG;

    @Api("插件名字") protected String pluginName;
    @Api("模块名字") protected String moduleName;
    @Api("模块启停") protected volatile boolean enable;


    @Api("生命周期 预载时")
    protected abstract void init() throws BootException;

    @Api("生命周期 启动时")
    protected abstract void boot() throws BotException;

    @Api("生命周期 关闭时")
    protected abstract void shut() throws BotException;

    @Api("查询模块的启用状态")
    public final boolean isEnable() {
        return this.enable;
    }

    @Api("改变模块的启用状态")
    public final void setEnable(boolean enable) {
        this.enable = enable;
    }

    @Api("初始化插件总目录")
    protected final void initRootFolder() {
        this.initFolder(this.FOLDER_ROOT);
    }

    @Api("初始化插件配置文件目录")
    protected final void initConfFolder() {
        this.initFolder(this.FOLDER_CONF);
    }

    @Api("初始化插件数据目录")
    protected final void initDataFolder() {
        this.initFolder(this.FOLDER_DATA);
    }

    @Api("初始化插件日志目录")
    protected final void initLogsFolder() {
        this.initFolder(this.FOLDER_LOGS);
    }

    @Api("初始化插件配置下的目录")
    protected final File initConfFolder(String folderName) {
        return this.initFolder(Paths.get(this.FOLDER_CONF.getAbsolutePath(), folderName).toFile());
    }

    @Api("初始化插件数据下的目录")
    protected final File initDataFolder(String folderName) {
        return this.initFolder(Paths.get(this.FOLDER_DATA.getAbsolutePath(), folderName).toFile());
    }

    @Api("初始化插件数据下的目录")
    protected final File initLogsFolder(String folderName) {
        return this.initFolder(Paths.get(this.FOLDER_LOGS.getAbsolutePath(), folderName).toFile());
    }

    @Api("初始化配置文件夹下的文件")
    protected final File initConfFile(String fileName) {
        return this.initFile(Paths.get(this.FOLDER_CONF.getAbsolutePath(), fileName));
    }

    @Api("初始化数据文件夹下的文件")
    protected final File initDataFile(String fileName) {
        return this.initFile(Paths.get(this.FOLDER_DATA.getAbsolutePath(), fileName));
    }

    @Api("初始化日志文件夹下的文件")
    protected final File initLogsFile(String fileName) {
        return this.initFile(Paths.get(this.FOLDER_LOGS.getAbsolutePath(), fileName));
    }

    @Api("初始化默认配置文件")
    protected final void initConfiguration() {
        if (!this.FILE_CONFIG.exists()) {
            this.logger.seek("配置文件不存在 " + this.FILE_CONFIG.getAbsolutePath());
            try {
                this.initFile(this.FILE_CONFIG.toPath());
            } catch (Exception exception) {
                throw new ModuleException("初始化配置错误", exception);
            }
            this.NEW_CONFIG = true;
        }
    }

    @Api("加载默认配置文件")
    protected final void loadConfig() {
        try (FileInputStream inStream = new FileInputStream(this.FILE_CONFIG)) {
            this.CONFIG.load(inStream);
        } catch (IOException exception) {
            throw new ModuleException("加载配置错误", exception);
        }
    }

    @Api("保存默认配置文件")
    protected final void saveConfig() {
        this.saveConfig(null);
    }

    @Api("保存默认配置文件")
    protected final void saveConfig(String comments) {
        try (FileOutputStream outputStream = new FileOutputStream(this.FILE_CONFIG)) {
            this.CONFIG.store(outputStream, comments);
        } catch (IOException exception) {
            throw new ModuleException("保存配置错误", exception);
        }
    }

    @Api("初始化文件夹")
    protected final File initPluginFolder(String folder) {
        return this.initFolder(Paths.get(this.FOLDER_ROOT.getAbsolutePath(), folder).toFile());
    }

    @Api("获取本模块所属插件包的名字")
    public final String getPluginName() {
        return this.pluginName;
    }

    @Api("获取本模块的名字")
    public final String getModuleName() {
        return this.moduleName;
    }


    // =================================================================================================================


    @Api("初始化文件")
    protected final File initModuleFile(String path) {
        File file = Paths.get(this.FOLDER_ROOT.getAbsolutePath(), path).toFile();
        return this.initFile(file);
    }


    @Api("初始化文件夹")
    protected final File initModuleFolder(String path) {
        File file = Paths.get(this.FOLDER_ROOT.getAbsolutePath(), path).toFile();
        return this.initFolder(file);
    }


    // =================================================================================================================


    public final void internalInit(String pluginName, String moduleName, URLClassLoader exclusiveClassLoader) {
        StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
        StackTraceElement schemaClazz = stackTrace[2];
        if (!"studio.blacktech.furryblackplus.core.schema.Schema".equals(schemaClazz.getClassName())) {
            BotException botException = new BotException("IllegalAccess - Invoke internalInit, And here is caller stack trace");
            botException.setStackTrace(stackTrace);
            throw botException;
        }
        if (!"make".equals(schemaClazz.getMethodName())) {
            BotException botException = new BotException("IllegalAccess - Invoke internalInit, And here is caller stack trace");
            botException.setStackTrace(stackTrace);
            throw botException;
        }
        if (this.internalInitLock) {
            BotException botException = new BotException("IllegalAccess - Invoke internalInit, And here is caller stack trace");
            botException.setStackTrace(stackTrace);
            throw botException;
        }
        this.internalInitLock = true;
        this.pluginName = pluginName;
        this.moduleName = moduleName;
        this.exclusiveClassLoader = exclusiveClassLoader;
        this.FOLDER_ROOT = Paths.get(FurryBlack.getModuleFolder(), this.moduleName).toFile();
        this.FOLDER_CONF = Paths.get(this.FOLDER_ROOT.getAbsolutePath(), "conf").toFile();
        this.FOLDER_DATA = Paths.get(this.FOLDER_ROOT.getAbsolutePath(), "data").toFile();
        this.FOLDER_LOGS = Paths.get(this.FOLDER_ROOT.getAbsolutePath(), "logs").toFile();
        this.FILE_CONFIG = Paths.get(this.FOLDER_ROOT.getAbsolutePath(), "config.properties").toFile();
        this.CONFIG = new Properties();
    }


    public final void initWrapper() throws BotException {
        ClassLoader contextClassLoader = Thread.currentThread().getContextClassLoader();
        try {
            Thread.currentThread().setContextClassLoader(this.exclusiveClassLoader);
            this.init();
        } finally {
            Thread.currentThread().setContextClassLoader(contextClassLoader);
        }
    }

    public final void bootWrapper() throws BotException {
        ClassLoader contextClassLoader = Thread.currentThread().getContextClassLoader();
        try {
            Thread.currentThread().setContextClassLoader(this.exclusiveClassLoader);
            this.boot();
        } finally {
            Thread.currentThread().setContextClassLoader(contextClassLoader);
        }
        this.enable = true;
    }

    public final void shutWrapper() throws BotException {
        this.enable = false;
        ClassLoader contextClassLoader = Thread.currentThread().getContextClassLoader();
        try {
            Thread.currentThread().setContextClassLoader(this.exclusiveClassLoader);
            this.shut();
        } finally {
            Thread.currentThread().setContextClassLoader(contextClassLoader);
        }
    }


}
