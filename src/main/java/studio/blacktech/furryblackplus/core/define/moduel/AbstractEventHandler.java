package studio.blacktech.furryblackplus.core.define.moduel;


import studio.blacktech.furryblackplus.Driver;
import studio.blacktech.furryblackplus.common.Api;
import studio.blacktech.furryblackplus.core.exception.BotException;
import studio.blacktech.furryblackplus.core.exception.moduels.boot.BootException;
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


@SuppressWarnings("RedundantThrows")

@Api("基础模块类")
public abstract class AbstractEventHandler {

    protected final LoggerX logger;

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


    public AbstractEventHandler() {
        this.logger = new LoggerX(this.getClass());
    }


    @Deprecated
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
                throw new RuntimeException("初始化配置错误", exception);
            }
            this.NEW_CONFIG = true;
        }
    }

    @Api("加载默认配置文件")
    protected void loadConfig() {
        try (FileInputStream inStream = new FileInputStream(this.FILE_CONFIG)) {
            this.CONFIG.load(inStream);
        } catch (IOException exception) {
            throw new RuntimeException("加载配置错误", exception);
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
            throw new RuntimeException("保存配置错误", exception);
        }
    }

    @Api("按行读取文件 删除注释")
    protected List<String> readFile(File file) {
        return this.readFile(file, false);
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
            while ((line = reader.readLine()) != null) {
                temp.add(line);
            }
            reader.close();
            inputStreamReader.close();
            fileInputStream.close();
            if (keepComment) {
                return temp;
            }
            return temp
                       .stream()
                       .filter(item -> item.length() > 0)
                       .filter(item -> item.charAt(0) != '#')
                       .map(item -> item.contains("#") ?
                                        item.substring(0, item.indexOf("#")).stripTrailing() :
                                        item)
                       .collect(Collectors.toList());
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }
    }

    @Api("初始化文件夹")
    protected File initFolder(String folder) {
        return this.initFolder(Paths.get(this.FOLDER_ROOT.getAbsolutePath(), folder).toFile());
    }

    @Api("初始化文件夹")
    protected File initFolder(File file) {
        if (file.exists()) {
            if (!file.isDirectory()) {
                throw new IllegalArgumentException("文件夹被文件占位 -> " + file.getAbsolutePath());
            }
        } else {
            if (file.mkdirs()) {
                this.logger.seek("创建新目录 -> " + file.getAbsolutePath());
            }
        }
        return file;
    }

    @Api("初始化文件")
    protected File initFile(Path path) {
        return this.initFile(path.toFile());
    }

    @Api("初始化文件")
    protected File initFile(File file) {
        try {
            if (file.createNewFile()) this.logger.seek("创建新文件 -> " + file.getAbsolutePath());
        } catch (IOException exception) {
            throw new RuntimeException("创建文件失败 -> " + file.getAbsolutePath(), exception);
        }
        if (!file.canRead()) {
            throw new IllegalArgumentException("文件无权读取 -> " + file.getAbsolutePath());
        }
        if (!file.canWrite()) {
            throw new IllegalArgumentException("文件无权写入 -> " + file.getAbsolutePath());
        }
        return file;
    }

}
