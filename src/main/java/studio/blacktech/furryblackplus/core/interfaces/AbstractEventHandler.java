package studio.blacktech.furryblackplus.core.interfaces;


import studio.blacktech.furryblackplus.Driver;
import studio.blacktech.furryblackplus.core.annotation.Api;
import studio.blacktech.furryblackplus.core.exception.BotException;
import studio.blacktech.furryblackplus.core.exception.initlization.InitException;
import studio.blacktech.furryblackplus.core.utilties.LoggerX;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;
import java.util.stream.Collectors;


@Api("事件处理器")
public abstract class AbstractEventHandler {

    public final ModuleInfo INFO;

    protected final File FOLDER_ROOT;
    protected final File FOLDER_CONF;
    protected final File FOLDER_DATA;
    protected final File FOLDER_LOGS;
    protected final File FILE_CONFIG;

    protected final Properties CONFIG;

    protected LoggerX logger;
    protected boolean NEW_CONFIG = false;

    protected boolean INIT_ROOT = false;
    protected boolean INIT_CONF = false;
    protected boolean INIT_DATA = false;
    protected boolean INIT_LOGS = false;


    public AbstractEventHandler(ModuleInfo INFO) {
        this.INFO = INFO;
        this.logger = new LoggerX(this.getClass());
        this.CONFIG = new Properties();
        this.FOLDER_ROOT = Paths.get(Driver.getModuleFolder(), this.INFO.ARTIFICIAL).toFile();
        this.FOLDER_CONF = Paths.get(this.FOLDER_ROOT.getAbsolutePath(), "conf").toFile();
        this.FOLDER_DATA = Paths.get(this.FOLDER_ROOT.getAbsolutePath(), "data").toFile();
        this.FOLDER_LOGS = Paths.get(this.FOLDER_ROOT.getAbsolutePath(), "logs").toFile();
        this.FILE_CONFIG = Paths.get(this.FOLDER_ROOT.getAbsolutePath(), "config.properties").toFile();
    }


    public abstract void init() throws InitException;


    public abstract void boot() throws BotException;


    public abstract void shut() throws BotException;


    @Api("初始化插件总目录")
    public void initRootFolder() {
        initFolder(FOLDER_ROOT);
        INIT_ROOT = true;
    }


    @Api("初始化插件配置文件目录")
    public void initConfFolder() {
        if (!INIT_ROOT) initRootFolder();
        initFolder(FOLDER_CONF);
        INIT_CONF = true;
    }


    @Api("初始化插件数据目录")
    public void initDataFolder() {
        if (!INIT_ROOT) initRootFolder();
        initFolder(FOLDER_DATA);
        INIT_DATA = true;
    }


    @Api("初始化插件日志目录")
    public void initLogsFolder() {
        if (!INIT_ROOT) initRootFolder();
        initFolder(FOLDER_LOGS);
        INIT_LOGS = true;
    }


    @Api("初始化插件配置下的目录")
    public File initConfFolder(String folderName) {
        if (!INIT_CONF) initConfFolder();
        File file = initFolder(Paths.get(FOLDER_CONF.getAbsolutePath(), folderName).toFile());
        INIT_CONF = true;
        return file;
    }


    @Api("初始化插件数据下的目录")
    public File initDataFolder(String folderName) {
        if (!INIT_DATA) initDataFolder();
        File file = initFolder(Paths.get(FOLDER_DATA.getAbsolutePath(), folderName).toFile());
        INIT_DATA = true;
        return file;
    }

    @Api("初始化插件数据下的目录")
    public File initLogsFolder(String folderName) {
        if (!INIT_LOGS) initLogsFolder();
        File file = initFolder(Paths.get(FOLDER_LOGS.getAbsolutePath(), folderName).toFile());
        INIT_LOGS = true;
        return file;
    }


    @Api("初始化配置文件夹下的文件")
    public File initConfFile(String fileName) {
        if (!INIT_CONF) initConfFolder();
        return initFile(Paths.get(FOLDER_CONF.getAbsolutePath(), fileName));
    }


    @Api("初始化数据文件夹下的文件")
    public File initDataFile(String fileName) {
        if (!INIT_DATA) initDataFolder();
        return initFile(Paths.get(FOLDER_DATA.getAbsolutePath(), fileName));
    }


    @Api("初始化日志文件夹下的文件")
    public File initLogsFile(String fileName) {
        if (!INIT_LOGS) initLogsFolder();
        return initFile(Paths.get(FOLDER_LOGS.getAbsolutePath(), fileName));
    }


    @Api("初始化默认配置文件")
    public void initConfiguration() {
        if (!FILE_CONFIG.exists()) {
            logger.seek("配置文件不存在 " + FILE_CONFIG.getAbsolutePath());
            try {
                initFile(FILE_CONFIG);
            } catch (Exception exception) {
                throw new IllegalArgumentException("初始化配置错误", exception);
            }
            NEW_CONFIG = true;
        }
    }


    @Api("加载默认配置文件")
    protected void loadConfig() {
        try {
            CONFIG.load(new FileInputStream(FILE_CONFIG));
        } catch (IOException exception) {
            throw new IllegalArgumentException("加载配置错误", exception);
        }
    }


    @Api("保存默认配置文件")
    protected void saveConfig() {
        saveConfig(null);
    }


    @Api("保存默认配置文件")
    protected void saveConfig(String comments) {
        try {
            CONFIG.store(new FileOutputStream(FILE_CONFIG), comments);
        } catch (IOException exception) {
            throw new IllegalArgumentException("保存配置错误", exception);
        }
    }


    @Api("按行读取文件 删除注释")
    protected List<String> readFile(File file) {
        return readFile(file, false);
    }


    @Api("按行读取文件 可选注释")
    protected List<String> readFile(File file, boolean keepComment) {
        if (!file.exists()) throw new IllegalArgumentException("文件不存在 -> " + file.getAbsolutePath());
        if (!file.isFile()) throw new IllegalArgumentException("文件是目录 -> " + file.getAbsolutePath());
        if (!file.canRead()) throw new IllegalArgumentException("文件无权读取 -> " + file.getAbsolutePath());
        String line;
        List<String> temp = new LinkedList<>();
        try (
                FileInputStream fileInputStream = new FileInputStream(file);
                InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream, StandardCharsets.UTF_8);
                BufferedReader reader = new BufferedReader(inputStreamReader)
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
            throw new IllegalArgumentException(exception);
        }
    }


    @Api("初始化文件夹")
    protected File initFolder(String folder) {
        return initFolder(Paths.get(FOLDER_ROOT.getAbsolutePath(), folder).toFile());
    }


    @Api("初始化文件夹")
    protected File initFolder(File file) {
        if (file.exists()) {
            if (!file.isDirectory()) throw new IllegalArgumentException("文件夹被文件占位 -> " + file.getAbsolutePath());
        } else {
            logger.seek("创建目录 -> " + file.getAbsolutePath());
            //noinspection ResultOfMethodCallIgnored
            file.mkdirs();
        }
        return file;
    }


    @Api("初始化文件")
    protected File initFile(Path path) {
        return initFile(path.toFile());
    }


    @Api("初始化文件")
    protected File initFile(File file) {
        if (!file.exists()) {
            try {
                if (file.createNewFile()) {
                    logger.seek("创建文件 -> " + file.getAbsolutePath());
                } else {
                    throw new IllegalArgumentException("文件名被占用 -> " + file.getAbsolutePath());
                }
            } catch (IOException exception) {
                throw new IllegalArgumentException("创建文件失败 -> " + file.getAbsolutePath(), exception);
            }
        }
        if (!file.canRead()) throw new IllegalArgumentException("文件无权读取 -> " + file.getAbsolutePath());
        if (!file.canWrite()) throw new IllegalArgumentException("文件无权写入 -> " + file.getAbsolutePath());
        return file;
    }


    public static class ModuleInfo {

        public final String NAME;
        public final String ARTIFICIAL;
        public final String DESCRIPTION;
        public final String[] PRIVACY;

        public ModuleInfo(String NAME, String ARTIFICIAL, String DESCRIPTION, String[] PRIVACY) {
            if (NAME.equals("")) throw new IllegalArgumentException("NAME cannot be null");
            if (ARTIFICIAL.equals("")) throw new IllegalArgumentException("ARTIFICIAL cannot be null");
            if (DESCRIPTION.equals("")) throw new IllegalArgumentException("DESCRIPTION cannot be null");
            if (PRIVACY == null) throw new IllegalArgumentException("PRIVACY cannot be null");

            this.NAME = NAME;
            this.ARTIFICIAL = ARTIFICIAL;
            this.DESCRIPTION = DESCRIPTION;
            this.PRIVACY = PRIVACY;
        }
    }
}
