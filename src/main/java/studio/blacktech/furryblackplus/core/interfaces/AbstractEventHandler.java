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


@Api("基础模块类")
public abstract class AbstractEventHandler {

    @Api("模块内建的插件目录对象") protected final File FOLDER_ROOT;
    @Api("模块内建的配置目录对象") protected final File FOLDER_CONF;
    @Api("模块内建的数据目录对象") protected final File FOLDER_DATA;
    @Api("模块内建的日志目录对象") protected final File FOLDER_LOGS;
    @Api("模块内建的配置文件对象") protected final File FILE_CONFIG;

    @Api("模块内建的LoggerX实例") protected final LoggerX logger;

    @Api("模块内建的config.properties实例") protected final Properties CONFIG;

    @Api("模块标志位表示是否初始化过插件目录") protected boolean INIT_ROOT = false;
    @Api("模块标志位表示是否初始化过配置目录") protected boolean INIT_CONF = false;
    @Api("模块标志位表示是否初始化过数据目录") protected boolean INIT_DATA = false;
    @Api("模块标志位表示是否初始化过日志目录") protected boolean INIT_LOGS = false;
    @Api("模块标志位表示是否初始化过配置文件") protected boolean NEW_CONFIG = false;


    protected AbstractEventHandler(String artificial) {
        this.logger = new LoggerX(this.getClass());
        this.CONFIG = new Properties();
        this.FOLDER_ROOT = Paths.get(Driver.getModuleFolder(), artificial).toFile();
        this.FOLDER_CONF = Paths.get(this.FOLDER_ROOT.getAbsolutePath(), "conf").toFile();
        this.FOLDER_DATA = Paths.get(this.FOLDER_ROOT.getAbsolutePath(), "data").toFile();
        this.FOLDER_LOGS = Paths.get(this.FOLDER_ROOT.getAbsolutePath(), "logs").toFile();
        this.FILE_CONFIG = Paths.get(this.FOLDER_ROOT.getAbsolutePath(), "config.properties").toFile();
    }


    @Api("生命周期 初始化时")
    public abstract void init() throws InitException;

    @Api("生命周期 启动时")
    public abstract void boot() throws BotException;

    @Api("生命周期 关闭时")
    public abstract void shut() throws BotException;


    @Api("初始化插件总目录")
    protected void initRootFolder() {
        initFolder(FOLDER_ROOT);
        INIT_ROOT = true;
    }

    @Api("初始化插件配置文件目录")
    protected void initConfFolder() {
        if (!INIT_ROOT) initRootFolder();
        initFolder(FOLDER_CONF);
        INIT_CONF = true;
    }

    @Api("初始化插件数据目录")
    protected void initDataFolder() {
        if (!INIT_ROOT) initRootFolder();
        initFolder(FOLDER_DATA);
        INIT_DATA = true;
    }

    @Api("初始化插件日志目录")
    protected void initLogsFolder() {
        if (!INIT_ROOT) initRootFolder();
        initFolder(FOLDER_LOGS);
        INIT_LOGS = true;
    }

    @Api("初始化插件配置下的目录")
    protected File initConfFolder(String folderName) {
        if (!INIT_CONF) initConfFolder();
        File file = initFolder(Paths.get(FOLDER_CONF.getAbsolutePath(), folderName).toFile());
        INIT_CONF = true;
        return file;
    }

    @Api("初始化插件数据下的目录")
    protected File initDataFolder(String folderName) {
        if (!INIT_DATA) initDataFolder();
        File file = initFolder(Paths.get(FOLDER_DATA.getAbsolutePath(), folderName).toFile());
        INIT_DATA = true;
        return file;
    }

    @Api("初始化插件数据下的目录")
    protected File initLogsFolder(String folderName) {
        if (!INIT_LOGS) initLogsFolder();
        File file = initFolder(Paths.get(FOLDER_LOGS.getAbsolutePath(), folderName).toFile());
        INIT_LOGS = true;
        return file;
    }

    @Api("初始化配置文件夹下的文件")
    protected File initConfFile(String fileName) {
        if (!INIT_CONF) initConfFolder();
        return initFile(Paths.get(FOLDER_CONF.getAbsolutePath(), fileName));
    }

    @Api("初始化数据文件夹下的文件")
    protected File initDataFile(String fileName) {
        if (!INIT_DATA) initDataFolder();
        return initFile(Paths.get(FOLDER_DATA.getAbsolutePath(), fileName));
    }

    @Api("初始化日志文件夹下的文件")
    protected File initLogsFile(String fileName) {
        if (!INIT_LOGS) initLogsFolder();
        return initFile(Paths.get(FOLDER_LOGS.getAbsolutePath(), fileName));
    }

    @Api("初始化默认配置文件")
    protected void initConfiguration() {
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
        try (FileInputStream inStream = new FileInputStream(FILE_CONFIG)) {
            CONFIG.load(inStream);
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
        try (FileOutputStream outputStream = new FileOutputStream(FILE_CONFIG)) {
            CONFIG.store(outputStream, comments);
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


    protected static class ModuleInfo {

        public final String NAME;
        public final String ARTIFICIAL;
        public final String DESCRIPTION;
        public final String[] PRIVACY;

        public ModuleInfo(String name, String artificial, String description, String[] privacy) {
            if (name.equals("")) throw new IllegalArgumentException("无效的模块名称`name`");
            if (artificial.equals("")) throw new IllegalArgumentException("无效的模块全名`artificial`");
            if (description.equals("")) throw new IllegalArgumentException("无效的模块介绍`description`");
            this.NAME = name;
            this.ARTIFICIAL = artificial;
            this.DESCRIPTION = description;
            this.PRIVACY = privacy;
        }
    }
}
