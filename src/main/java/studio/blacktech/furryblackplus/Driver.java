package studio.blacktech.furryblackplus;


import net.mamoe.mirai.contact.Friend;
import net.mamoe.mirai.contact.PermissionDeniedException;
import org.jline.reader.LineReader;
import org.jline.reader.LineReaderBuilder;
import org.jline.widget.AutopairWidgets;
import studio.blacktech.furryblackplus.module.Systemd;
import studio.blacktech.furryblackplus.system.command.BasicCommand;
import studio.blacktech.furryblackplus.system.common.exception.working.NotAFolderException;
import studio.blacktech.furryblackplus.system.common.logger.LoggerX;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Properties;
import java.util.jar.JarFile;


/**
 * FurryBlack Plus Framework
 *
 * @author Alceatraz Warprays alceatraz@blacktech.studio
 * @see Driver 为启动类main方法所在地，初始化日志和目录系统，提供控制台
 * @see Systemd 是整个系统的内核所在，.exit
 */
public class Driver {


    // ==========================================================================================================================================================
    //
    // 私有变量
    //
    // ==========================================================================================================================================================


    private final static String APP_VERSION = "0.1.5";


    private final static long BOOT_TIME = System.currentTimeMillis();


    //


    private static boolean enable = true;


    private static LoggerX logger;


    private static Systemd systemd;


    private static File FOLDER_ROOT;

    private static File FOLDER_CONFIG;

    private static File FOLDER_MODULE;
    private static File FOLDER_LOGGER;

    private static File FILE_CONFIG;

    private static Properties CONFIG;

    private static JarFile JAR_INSTANCE;


    // ==========================================================================================================================================================
    //
    // 启动入口
    //
    // ==========================================================================================================================================================


    public static void main(String[] args) {


        Arrays.stream(args).forEach(System.out::println);


        System.out.println("[FurryBlack][BOOT] FurryBlackPlus Mirai - ver " + APP_VERSION + " " + LoggerX.formatTime("yyyy-MM-dd HH:mm:ss", BOOT_TIME));


        LineReader jlineReader = null;

        BufferedReader bufferedReader = null;

        boolean JLINE = true;

        try {


            // ==========================================================================================================================
            // 初始化文件


            JLINE = !Arrays.asList(args).contains("--nojline");


            if (JLINE) {
                jlineReader = LineReaderBuilder.builder().build();
                AutopairWidgets autopairWidgets = new AutopairWidgets(jlineReader);
                autopairWidgets.enable();
                System.out.println("[FurryBlack][INIT]使用JLine控制台");
            } else {
                bufferedReader = new BufferedReader(new InputStreamReader(System.in));
                System.out.println("[FurryBlack][INIT]BufferReader控制台");
            }


            // ==========================================================================================================================
            // 初始化文件


            System.out.println("[FurryBlack][INIT]初始化路径");


            String userDir = System.getProperty("user.dir");


            FOLDER_ROOT = Paths.get(userDir).toFile();


            FOLDER_CONFIG = Paths.get(userDir, "config").toFile();
            FOLDER_MODULE = Paths.get(userDir, "module").toFile();
            FOLDER_LOGGER = Paths.get(userDir, "logger").toFile();

            FILE_CONFIG = Paths.get(FOLDER_CONFIG.getAbsolutePath(), "application.properties").toFile();
            File FILE_LOGGER = Paths.get(FOLDER_LOGGER.getAbsolutePath(), LoggerX.formatTime("yyyy_MM_dd_HH_mm_ss", BOOT_TIME) + ".txt").toFile();


            System.out.println("[FurryBlack][INIT]初始化目录");


            if (!FOLDER_CONFIG.exists()) FOLDER_CONFIG.mkdirs();
            if (!FOLDER_MODULE.exists()) FOLDER_MODULE.mkdirs();
            if (!FOLDER_LOGGER.exists()) FOLDER_LOGGER.mkdirs();


            System.out.println("[FurryBlack][INIT]初始化检查");


            if (!FOLDER_CONFIG.isDirectory()) throw new NotAFolderException("文件夹被文件占位：" + FOLDER_CONFIG.getAbsolutePath());
            if (!FOLDER_MODULE.isDirectory()) throw new NotAFolderException("文件夹被文件占位：" + FOLDER_MODULE.getAbsolutePath());
            if (!FOLDER_LOGGER.isDirectory()) throw new NotAFolderException("文件夹被文件占位：" + FOLDER_LOGGER.getAbsolutePath());


            // ==========================================================================================================================
            // 初始化LoggerX


            System.out.println("[FurryBlack][INIT]创建日志文件");


            FILE_LOGGER.createNewFile();


            if (!FILE_LOGGER.exists()) throw new FileNotFoundException("日志文件不存在: " + FILE_LOGGER.getAbsolutePath());

            if (!FILE_LOGGER.canWrite()) throw new PermissionDeniedException("日志文件没有写权限: " + FILE_LOGGER.getAbsolutePath());


            LoggerX.init(FILE_LOGGER);


            logger = new LoggerX(Driver.class);


            System.out.println("[FurryBlack][INIT]日志系统初始化完成");


            logger.seek("应用工作目录 " + FOLDER_ROOT.getAbsolutePath());

            logger.seek("核心配置目录 " + FOLDER_CONFIG.getAbsolutePath());
            logger.seek("核心日志目录 " + FOLDER_LOGGER.getAbsolutePath());
            logger.seek("模块数据目录 " + FOLDER_MODULE.getAbsolutePath());

            logger.seek("当前日志文件 " + FILE_LOGGER.getAbsolutePath());


        } catch (Exception exception) {

            System.err.println("[FurryBlack][FATAL]核心系统初始化发生异常 终止启动");
            System.err.println(exception.getMessage());

            exception.printStackTrace();

            System.exit(-1);

        }


        try {

            logger.seek("实例化Systemd");
            systemd = new Systemd();

            logger.seek("初始化Systemd");
            systemd.init(FILE_CONFIG);

        } catch (Exception exception) {

            System.err.println("[FurryBlack][FATAL] 路由系统初始化发生异常 终止启动");
            System.err.println(exception.getMessage());

            exception.printStackTrace();

            System.exit(-1);
        }


        try {

            logger.seek("启动Systemd");
            systemd.boot();

        } catch (Exception exception) {

            System.err.println("[FurryBlack][FATAL] 路由系统启动发生异常 终止启动");
            System.err.println(exception.getMessage());

            exception.printStackTrace();

            System.exit(-1);
        }


        console:
        while (true) {


            try {

                String temp;

                if (JLINE) {
                    temp = jlineReader.readLine("[console]$ ");
                } else {
                    temp = bufferedReader.readLine();
                }

                if (temp == null || temp.equals("")) continue;

                if (temp.charAt(0) == '/') {
                    System.out.println("控制台命令不要加/");
                    continue;
                }

                BasicCommand command = new BasicCommand("/" + temp);

                switch (command.getCommandName()) {

                    case "exit":
                        break console;

                    case "help":
                        System.out.println("exit关闭");
                        System.out.println("list列出模块");
                        System.out.println("reload重启模");
                        break;

                    case "send":
                        Friend friend = systemd.getFriend(Long.parseLong(command.getParameterSegment(0)));
                        friend.sendMessage(command.join(1));
                        break;

                    case "list":
                        systemd.listAllPlugin().forEach(System.out::println);
                        break;


                    case "reload":
                        for (String s : command.getParameterSegment()) {
                            systemd.reloadPlugin(s);
                        }
                        break;


                    default:
                        System.out.println("没有此命令");
                        break;

                }


            } catch (Exception exception) {
                logger.error("命令导致了异常", exception);
            }

        }


        enable = false;


        try {

            systemd.shut();

        } catch (Exception exception) {
            logger.error("关闭异常", exception);
        }


        System.out.println("系统已关闭");


    }


    // ==========================================================================================================================================================
    //
    // Runtime相关
    //
    // ==========================================================================================================================================================


    public static String getAppVersion() {
        return APP_VERSION;
    }


    public static long getBootTime() {
        return BOOT_TIME;
    }


    public static boolean isEnable() {
        return enable;
    }


    public static String getRootFolder() {
        return FOLDER_ROOT.getAbsolutePath();
    }


    public static String getConfigFolder() {
        return FOLDER_CONFIG.getAbsolutePath();
    }


    public static String getModuleFolder() {
        return FOLDER_MODULE.getAbsolutePath();
    }


    public static String getLoggerFolder() {
        return FOLDER_LOGGER.getAbsolutePath();
    }


}
