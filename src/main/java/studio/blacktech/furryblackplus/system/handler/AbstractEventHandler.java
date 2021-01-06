package studio.blacktech.furryblackplus.system.handler;


import studio.blacktech.furryblackplus.Driver;
import studio.blacktech.furryblackplus.system.common.exception.BotException;
import studio.blacktech.furryblackplus.system.common.exception.working.NotAFolderException;
import studio.blacktech.furryblackplus.system.common.logger.LoggerX;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;
import java.util.stream.Collectors;


public abstract class AbstractEventHandler {


    public static class ModuleInfo {

        public final String NAME;
        public final String DESCRIPTION;
        public final String[] PRIVACY;

        public ModuleInfo(String NAME, String DESCRIPTION, String[] PRIVACY) {
            if (NAME.equals("")) throw new IllegalArgumentException("NAME cannot be null");
            if (DESCRIPTION.equals("")) throw new IllegalArgumentException("DESCRIPTION cannot be null");
            if (PRIVACY == null) throw new IllegalArgumentException("PRIVACY cannot be null");

            this.NAME = NAME;
            this.DESCRIPTION = DESCRIPTION;
            this.PRIVACY = PRIVACY;
        }
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


    public File initConfFile(String fileName) throws BotException {
        return initFile(Paths.get(FOLDER_CONF.getAbsolutePath(), fileName));
    }


    public File initDataFile(String fileName) throws BotException {
        return initFile(Paths.get(FOLDER_DATA.getAbsolutePath(), fileName));
    }


    public File initLogsFile(String fileName) throws BotException {
        return initFile(Paths.get(FOLDER_LOGS.getAbsolutePath(), fileName));
    }


    public File initFile(Path path) throws BotException {
        return initFile(path.toFile());
    }


    public File initFile(File file) throws BotException {
        if (!file.exists()) {
            try {
                file.createNewFile();
                logger.seek("创建新的配置文件 -> " + file.getAbsolutePath());
            } catch (IOException exception) {
                throw new BotException("创建文件失败 -> " + file.getAbsolutePath(), exception);
            }
        }
        if (!file.canRead()) throw new BotException("文件无权读取 -> " + file.getAbsolutePath());
        if (!file.canWrite()) throw new BotException("文件无权写入 -> " + file.getAbsolutePath());
        return file;
    }


    public List<String> readFile(File file) throws BotException {
        return readFile(file, false);
    }


    public List<String> readFile(File file, boolean keepComment) throws BotException {
        if (!file.exists()) throw new BotException("文件不存在 -> " + file.getAbsolutePath());
        if (!file.isFile()) throw new BotException("文件是目录 -> " + file.getAbsolutePath());
        if (!file.canRead()) throw new BotException("文件无权读取 -> " + file.getAbsolutePath());
        String line;
        List<String> temp = new LinkedList<>();
        try (
                FileInputStream fileInputStream = new FileInputStream(file);
                InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream, StandardCharsets.UTF_8);
                BufferedReader reader = new BufferedReader(inputStreamReader);
        ) {
            while ((line = reader.readLine()) != null) temp.add(line);

            reader.close();
            inputStreamReader.close();
            fileInputStream.close();

            if (keepComment) return temp;

            // @formatter:off

            return temp
               .parallelStream()
               .filter(item-> item.length()>0)
               .filter(item -> item.charAt(0) != '#')
               .map(item -> item.contains("#") ?
                            item.substring(0, item.indexOf("#")).stripTrailing() :
                            item)
               .collect(Collectors.toList());

            // @formatter:on

        } catch (Exception exception) {
            throw new BotException(exception);
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


