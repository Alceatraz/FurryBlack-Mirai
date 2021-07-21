package studio.blacktech.furryblackplus.core.define;

import studio.blacktech.furryblackplus.core.annotation.Component;
import studio.blacktech.furryblackplus.core.exception.BotException;
import studio.blacktech.furryblackplus.core.interfaces.AbstractEventHandler;
import studio.blacktech.furryblackplus.core.interfaces.EventHandlerExecutor;
import studio.blacktech.furryblackplus.core.interfaces.EventHandlerFilter;
import studio.blacktech.furryblackplus.core.interfaces.EventHandlerMonitor;
import studio.blacktech.furryblackplus.core.interfaces.EventHandlerRunner;
import studio.blacktech.furryblackplus.core.utilties.LoggerX;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

@SuppressWarnings({"unused", "unchecked"})
public class PluginPackage {

    private final LoggerX logger;

    private final File file;
    private final String name;

    private final Map<String, Class<? extends AbstractEventHandler>> modules;

    private final List<Class<? extends EventHandlerRunner>> modulesRunner;
    private final List<Class<? extends EventHandlerFilter>> modulesFilter;
    private final List<Class<? extends EventHandlerMonitor>> modulesMonitor;
    private final List<Class<? extends EventHandlerExecutor>> modulesExecutor;


    public PluginPackage(File file) {
        this.file = file;
        this.name = file.getName();
        this.logger = new LoggerX(this.name);
        this.modules = new LinkedHashMap<>();
        this.modulesRunner = new LinkedList<>();
        this.modulesFilter = new LinkedList<>();
        this.modulesMonitor = new LinkedList<>();
        this.modulesExecutor = new LinkedList<>();
    }


    public void scan() {

        if (!this.file.exists()) {
            this.logger.warning("文件不存在 " + this.file.getAbsolutePath());
        }

        URL[] url;

        try {
            url = new URL[]{this.file.toURI().toURL()};
        } catch (MalformedURLException exception) {
            throw new BotException(exception);
        }

        URLClassLoader classLoader = URLClassLoader.newInstance(url, ClassLoader.getSystemClassLoader());

        JarFile jarFile;

        try {
            jarFile = new JarFile(this.file);
        } catch (IOException exception) {
            throw new BotException(exception);
        }

        this.modules.clear();

        Enumeration<JarEntry> entries = jarFile.entries();

        while (entries.hasMoreElements()) {

            JarEntry jarEntry = entries.nextElement();

            if (jarEntry.isDirectory()) {
                continue;
            }

            String entryName = jarEntry.getName();
            if (!entryName.endsWith(".class")) {
                continue;
            }

            String className = entryName.substring(0, entryName.length() - 6).replace("/", ".");

            Class<?> clazz;
            try {
                clazz = Class.forName(className, true, classLoader);
            } catch (ClassNotFoundException exception) {
                this.logger.warning("加载类失败 " + className, exception);
                continue;
            }

            if (!clazz.isAnnotationPresent(Component.class)) {
                continue;
            }

            if (!AbstractEventHandler.class.isAssignableFrom(clazz)) {
                this.logger.warning("发现无效模块 " + className);
                continue;
            }

            this.modules.put(entryName, (Class<? extends AbstractEventHandler>) clazz);
        }

        try {
            classLoader.close();
        } catch (IOException exception) {
            throw new BotException(exception);
        }

        try {
            jarFile.close();
        } catch (IOException exception) {
            throw new BotException(exception);
        }

        this.modulesRunner.clear();
        this.modulesFilter.clear();
        this.modulesMonitor.clear();
        this.modulesExecutor.clear();


        for (Class<? extends AbstractEventHandler> clazz : this.modules.values()) {

            Component annotation = clazz.getAnnotation(Component.class);

            if (EventHandlerRunner.class.isAssignableFrom(clazz)) {
                this.modulesRunner.add((Class<? extends EventHandlerRunner>) clazz);
                continue;
            }

            if (EventHandlerFilter.class.isAssignableFrom(clazz)) {
                if (!annotation.users() && !annotation.group()) {
                    this.logger.warning("忽略过滤器 " + clazz.getName());
                    continue;
                }
                this.modulesFilter.add((Class<? extends EventHandlerFilter>) clazz);
                continue;
            }

            if (EventHandlerMonitor.class.isAssignableFrom(clazz)) {
                if (!annotation.users() && !annotation.group()) {
                    this.logger.warning("忽略监听器 " + clazz.getName());
                    continue;
                }
                this.modulesMonitor.add((Class<? extends EventHandlerMonitor>) clazz);
                continue;
            }

            if (EventHandlerExecutor.class.isAssignableFrom(clazz)) {
                if (!annotation.users() && !annotation.group()) {
                    this.logger.warning("忽略执行器 " + clazz.getName());
                    continue;
                }
                this.modulesExecutor.add((Class<? extends EventHandlerExecutor>) clazz);
                continue;
            }

            this.logger.warning("WTF if some thing already scanned but not anything");

        }

        this.modulesRunner.sort(PluginPackage::comparePriority);
        this.modulesFilter.sort(PluginPackage::comparePriority);
        this.modulesMonitor.sort(PluginPackage::comparePriority);

        int runnerSize = this.modulesRunner.size();
        if (runnerSize > 0) {
            this.logger.info("扫描到" + runnerSize + "定时器");
            for (Class<? extends EventHandlerRunner> clazz : this.modulesRunner) {
                this.logger.info(clazz.getName());
            }
        }

        int filterSize = this.modulesFilter.size();
        if (filterSize > 0) {
            this.logger.info("扫描到" + filterSize + "过滤器");
            for (Class<? extends EventHandlerFilter> clazz : this.modulesFilter) {
                this.logger.info(clazz.getName());
            }
        }

        int monitorSize = this.modulesMonitor.size();
        if (monitorSize > 0) {
            this.logger.info("扫描到" + monitorSize + "监听器");
            for (Class<? extends EventHandlerMonitor> clazz : this.modulesMonitor) {
                this.logger.info(clazz.getName());
            }
        }

        int executorSize = this.modulesExecutor.size();
        if (executorSize > 0) {
            this.logger.info("扫描到" + executorSize + "执行器");
            for (Class<? extends EventHandlerExecutor> clazz : this.modulesExecutor) {
                this.logger.info(clazz.getName());
            }
        }
    }


    public static int comparePriority(Class<? extends AbstractEventHandler> o1, Class<? extends AbstractEventHandler> o2) {
        Component o1Annotation = o1.getAnnotation(Component.class);
        Component o2Annotation = o2.getAnnotation(Component.class);
        return o1Annotation.priority() - o2Annotation.priority();
    }


    public Map<String, Class<? extends AbstractEventHandler>> getModules() {
        return this.modules;
    }

    public List<Class<? extends EventHandlerRunner>> getModulesRunner() {
        return this.modulesRunner;
    }

    public List<Class<? extends EventHandlerFilter>> getModulesFilter() {
        return this.modulesFilter;
    }

    public List<Class<? extends EventHandlerMonitor>> getModulesMonitor() {
        return this.modulesMonitor;
    }


    public List<Class<? extends EventHandlerRunner>> getModulesRunnerReverse() {
        ArrayList<Class<? extends EventHandlerRunner>> list = new ArrayList<>(this.modulesRunner);
        Collections.reverse(list);
        return list;
    }

    public List<Class<? extends EventHandlerFilter>> getModulesFilterReverse() {
        ArrayList<Class<? extends EventHandlerFilter>> list = new ArrayList<>(this.modulesFilter);
        Collections.reverse(list);
        return list;
    }

    public List<Class<? extends EventHandlerMonitor>> getModulesMonitorReverse() {
        ArrayList<Class<? extends EventHandlerMonitor>> list = new ArrayList<>(this.modulesMonitor);
        Collections.reverse(list);
        return list;
    }


    public List<Class<? extends EventHandlerExecutor>> getModulesExecutor() {
        ArrayList<Class<? extends EventHandlerExecutor>> list = new ArrayList<>(this.modulesExecutor);
        Collections.reverse(list);
        return list;
    }


    public int getSize() {
        return this.modules.size();
    }

    public String getName() {
        return this.name;
    }

    @Override
    public int hashCode() {
        int hash = this.file.hashCode();
        for (Class<? extends EventHandlerRunner> clazz : this.modulesRunner) {
            hash = hash + clazz.hashCode();
        }
        for (Class<? extends EventHandlerFilter> clazz : this.modulesFilter) {
            hash = hash + clazz.hashCode();
        }
        for (Class<? extends EventHandlerMonitor> clazz : this.modulesMonitor) {
            hash = hash + clazz.hashCode();
        }
        for (Class<? extends EventHandlerExecutor> clazz : this.modulesExecutor) {
            hash = hash + clazz.hashCode();
        }
        return hash;
    }


    public static class Wrapper<T> {

        private T instance;

        public Wrapper(T instance) {
            this.instance = instance;
        }

        public T getInstance() {
            return this.instance;
        }

        public void setInstance(T instance) {
            this.instance = instance;
        }
    }
}
