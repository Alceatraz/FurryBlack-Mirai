/*
 * Copyright (C) 2021 Alceatraz @ BlackTechStudio
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the BTS Anti-Commercial & GNU Affero General.

 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * BTS Anti-Commercial & GNU Affero General Public License for more details.
 *
 * You should have received a copy of the BTS Anti-Commercial & GNU Affero
 * General Public License along with this program in README or LICENSE.
 */
package studio.blacktech.furryblackplus.core.define.moduel;


import studio.blacktech.furryblackplus.Driver;
import studio.blacktech.furryblackplus.common.Api;
import studio.blacktech.furryblackplus.core.exception.BotException;
import studio.blacktech.furryblackplus.core.exception.moduels.ModuleException;
import studio.blacktech.furryblackplus.core.exception.moduels.boot.BootException;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.Properties;


@SuppressWarnings("RedundantThrows")

@Api("基础模块类")
public abstract class AbstractEventHandler extends BasicModuleUtilities {

    @Api("插件目录对象") protected File FOLDER_ROOT;
    @Api("配置目录对象") protected File FOLDER_CONF;
    @Api("数据目录对象") protected File FOLDER_DATA;
    @Api("日志目录对象") protected File FOLDER_LOGS;
    @Api("配置文件对象") protected File FILE_CONFIG;
    @Api("配置文件对象") protected Properties CONFIG;

    @Api("初始化过插件目录") protected boolean INIT_ROOT;
    @Api("初始化过配置目录") protected boolean INIT_CONF;
    @Api("初始化过数据目录") protected boolean INIT_DATA;
    @Api("初始化过日志目录") protected boolean INIT_LOGS;
    @Api("初始化过配置文件") protected boolean NEW_CONFIG;

    @Api("模块名字") protected String name;
    @Api("模块启停") protected volatile boolean enable;

    @Api("这是一个内部使用的方法")
    public void internalInit(String name) {
        this.name = name;
        this.FOLDER_ROOT = Paths.get(Driver.getModuleFolder(), this.name).toFile();
        this.FOLDER_CONF = Paths.get(this.FOLDER_ROOT.getAbsolutePath(), "conf").toFile();
        this.FOLDER_DATA = Paths.get(this.FOLDER_ROOT.getAbsolutePath(), "data").toFile();
        this.FOLDER_LOGS = Paths.get(this.FOLDER_ROOT.getAbsolutePath(), "logs").toFile();
        this.FILE_CONFIG = Paths.get(this.FOLDER_ROOT.getAbsolutePath(), "config.properties").toFile();
        this.CONFIG = new Properties();
    }


    public void initWrapper() throws BotException {
        this.init();
    }

    public void bootWrapper() throws BotException {
        this.boot();
        this.enable = true;
    }

    public void shutWrapper() throws BotException {
        this.enable = false;
        this.shut();
    }

    @Api("生命周期 预载时")
    protected abstract void init() throws BootException;

    @Api("生命周期 启动时")
    protected abstract void boot() throws BotException;

    @Api("生命周期 关闭时")
    protected abstract void shut() throws BotException;

    @Api("查询模块的启用状态")
    public boolean isEnable() {
        return this.enable;
    }

    @Api("改变模块的启用状态")
    public void setEnable(boolean enable) {
        this.enable = enable;
    }

    @Api("初始化插件总目录")
    protected void initRootFolder() {
        this.initFolder(this.FOLDER_ROOT);
        this.INIT_ROOT = true;
    }

    @Api("初始化插件配置文件目录")
    protected void initConfFolder() {
        if (!this.INIT_ROOT) this.initRootFolder();
        this.initFolder(this.FOLDER_CONF);
        this.INIT_CONF = true;
    }

    @Api("初始化插件数据目录")
    protected void initDataFolder() {
        if (!this.INIT_ROOT) this.initRootFolder();
        this.initFolder(this.FOLDER_DATA);
        this.INIT_DATA = true;
    }

    @Api("初始化插件日志目录")
    protected void initLogsFolder() {
        if (!this.INIT_ROOT) this.initRootFolder();
        this.initFolder(this.FOLDER_LOGS);
        this.INIT_LOGS = true;
    }

    @Api("初始化插件配置下的目录")
    protected File initConfFolder(String folderName) {
        if (!this.INIT_CONF) this.initConfFolder();
        File file = this.initFolder(Paths.get(this.FOLDER_CONF.getAbsolutePath(), folderName).toFile());
        this.INIT_CONF = true;
        return file;
    }

    @Api("初始化插件数据下的目录")
    protected File initDataFolder(String folderName) {
        if (!this.INIT_DATA) this.initDataFolder();
        File file = this.initFolder(Paths.get(this.FOLDER_DATA.getAbsolutePath(), folderName).toFile());
        this.INIT_DATA = true;
        return file;
    }

    @Api("初始化插件数据下的目录")
    protected File initLogsFolder(String folderName) {
        if (!this.INIT_LOGS) this.initLogsFolder();
        File file = this.initFolder(Paths.get(this.FOLDER_LOGS.getAbsolutePath(), folderName).toFile());
        this.INIT_LOGS = true;
        return file;
    }

    @Api("初始化配置文件夹下的文件")
    protected File initConfFile(String fileName) {
        if (!this.INIT_CONF) this.initConfFolder();
        return this.initFile(Paths.get(this.FOLDER_CONF.getAbsolutePath(), fileName));
    }

    @Api("初始化数据文件夹下的文件")
    protected File initDataFile(String fileName) {
        if (!this.INIT_DATA) this.initDataFolder();
        return this.initFile(Paths.get(this.FOLDER_DATA.getAbsolutePath(), fileName));
    }

    @Api("初始化日志文件夹下的文件")
    protected File initLogsFile(String fileName) {
        if (!this.INIT_LOGS) this.initLogsFolder();
        return this.initFile(Paths.get(this.FOLDER_LOGS.getAbsolutePath(), fileName));
    }

    @Api("初始化默认配置文件")
    protected void initConfiguration() {
        if (!this.FILE_CONFIG.exists()) {
            this.logger.seek("配置文件不存在 " + this.FILE_CONFIG.getAbsolutePath());
            try {
                this.initFile(this.FILE_CONFIG);
            } catch (Exception exception) {
                throw new ModuleException("初始化配置错误", exception);
            }
            this.NEW_CONFIG = true;
        }
    }

    @Api("加载默认配置文件")
    protected void loadConfig() {
        try (FileInputStream inStream = new FileInputStream(this.FILE_CONFIG)) {
            this.CONFIG.load(inStream);
        } catch (IOException exception) {
            throw new ModuleException("加载配置错误", exception);
        }
    }

    @Api("保存默认配置文件")
    protected void saveConfig() {
        this.saveConfig(null);
    }

    @Api("保存默认配置文件")
    protected void saveConfig(String comments) {
        try (FileOutputStream outputStream = new FileOutputStream(this.FILE_CONFIG)) {
            this.CONFIG.store(outputStream, comments);
        } catch (IOException exception) {
            throw new ModuleException("保存配置错误", exception);
        }
    }


    @Api("初始化文件夹")
    protected File initFolder(String folder) {
        return this.initFolder(Paths.get(this.FOLDER_ROOT.getAbsolutePath(), folder).toFile());
    }


}
