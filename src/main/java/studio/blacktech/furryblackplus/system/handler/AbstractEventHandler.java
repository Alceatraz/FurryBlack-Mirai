package studio.blacktech.furryblackplus.system.handler;


import lombok.AllArgsConstructor;
import lombok.Getter;
import studio.blacktech.furryblackplus.Driver;
import studio.blacktech.furryblackplus.system.common.exception.BotException;
import studio.blacktech.furryblackplus.system.common.exception.working.NotAFolderException;
import studio.blacktech.furryblackplus.system.common.logger.LoggerX;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.util.Properties;


public abstract class AbstractEventHandler {


    @Getter
    @AllArgsConstructor
    public static class ModuleInfo {

        public final String NAME;
        public final String DESCRIPTION;
        public final String[] PRIVACY;

    }


    public final ModuleInfo INFO;


    protected LoggerX logger;


    protected final File FOLDER_ROOT;
    protected final File FOLDER_CONF;
    protected final File FOLDER_DATA;
    protected final File FOLDER_LOGS;
    protected final File FILE_CONFIG;


    protected final Properties CONFIG;


    protected boolean NEW_CONFIG = false;


    public AbstractEventHandler(ModuleInfo INFO) {

        this.INFO = INFO;

        logger = new LoggerX(this.getClass());

        CONFIG = new Properties();

        FOLDER_ROOT = Paths.get(Driver.getModuleFolder(), this.getClass().getSimpleName()).toFile();

        FOLDER_CONF = Paths.get(FOLDER_ROOT.getAbsolutePath(), "conf").toFile();
        FOLDER_DATA = Paths.get(FOLDER_ROOT.getAbsolutePath(), "data").toFile();
        FOLDER_LOGS = Paths.get(FOLDER_ROOT.getAbsolutePath(), "logs").toFile();

        FILE_CONFIG = Paths.get(FOLDER_ROOT.getAbsolutePath(), "config.properties").toFile();


    }


    public abstract void init() throws BotException;

    public abstract void boot() throws BotException;

    public abstract void shut() throws BotException;


    public void initAppFolder() throws BotException {
        if (FOLDER_ROOT.exists()) {
            if (!FOLDER_ROOT.isDirectory()) {
                throw new NotAFolderException("文件夹被文件占位：" + FOLDER_ROOT.getAbsolutePath());
            }
        } else {
            logger.seek("创建目录 " + FOLDER_ROOT.getAbsolutePath());
            FOLDER_ROOT.mkdirs();
        }
    }


    public void initConfFolder() throws BotException {
        if (FOLDER_CONF.exists()) {
            if (!FOLDER_CONF.isDirectory()) {
                throw new NotAFolderException("文件夹被文件占位：" + FOLDER_CONF.getAbsolutePath());
            }
        } else {
            logger.seek("创建目录 " + FOLDER_CONF.getAbsolutePath());
            FOLDER_CONF.mkdirs();
        }
    }


    public void initDataFolder() throws BotException {
        if (FOLDER_DATA.exists()) {
            if (!FOLDER_DATA.isDirectory()) {
                throw new NotAFolderException("文件夹被文件占位：" + FOLDER_DATA.getAbsolutePath());
            }
        } else {
            logger.seek("创建目录 " + FOLDER_DATA.getAbsolutePath());
            FOLDER_DATA.mkdirs();
        }
    }


    public void initLogsFolder() throws BotException {
        if (FOLDER_LOGS.exists()) {
            if (!FOLDER_LOGS.isDirectory()) {
                throw new NotAFolderException("文件夹被文件占位：" + FOLDER_LOGS.getAbsolutePath());
            }
        } else {
            logger.seek("创建目录 " + FOLDER_LOGS.getAbsolutePath());
            FOLDER_LOGS.mkdirs();
        }
    }


    public void initConfiguration() throws BotException {
        if (!FILE_CONFIG.exists()) {
            logger.seek("创建文件 " + FILE_CONFIG.getAbsolutePath());
            try {
                FILE_CONFIG.createNewFile();
            } catch (IOException exception) {
                throw new BotException("初始化配置错误", exception);
            }
            NEW_CONFIG = true;
        }
    }


    public void initConfiguration(String DEFAULT_CONFIG) throws BotException {
        if (!FILE_CONFIG.exists()) {
            logger.seek("创建文件 " + FILE_CONFIG.getAbsolutePath());
            try {
                FILE_CONFIG.createNewFile();
                FileWriter writer = new FileWriter(FILE_CONFIG, StandardCharsets.UTF_8, false);
                writer.write(DEFAULT_CONFIG);
                writer.flush();
                writer.close();
            } catch (IOException exception) {
                throw new BotException("初始化配置错误", exception);
            }
            NEW_CONFIG = true;
        }
    }


    protected void loadConfig() throws BotException {
        try {
            CONFIG.load(new FileInputStream(FILE_CONFIG));
        } catch (IOException exception) {
            throw new BotException("加载配置错误", exception);
        }
    }


    protected void saveConfig() throws BotException {
        try {
            CONFIG.store(new FileOutputStream(FILE_CONFIG), null);
        } catch (IOException exception) {
            throw new BotException("保存配置错误", exception);
        }
    }


    protected void saveConfig(String comments) throws BotException {
        try {
            CONFIG.store(new FileOutputStream(FILE_CONFIG), comments);
        } catch (
                  IOException exception) {
            throw new BotException("保存配置错误", exception);
        }
    }


}


