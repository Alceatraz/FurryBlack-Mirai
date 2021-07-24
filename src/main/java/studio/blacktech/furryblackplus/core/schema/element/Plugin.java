package studio.blacktech.furryblackplus.core.schema.element;

import studio.blacktech.furryblackplus.core.annotation.Executor;
import studio.blacktech.furryblackplus.core.annotation.Filter;
import studio.blacktech.furryblackplus.core.annotation.Monitor;
import studio.blacktech.furryblackplus.core.annotation.Runner;
import studio.blacktech.furryblackplus.core.define.moduel.AbstractEventHandler;
import studio.blacktech.furryblackplus.core.define.moduel.EventHandlerExecutor;
import studio.blacktech.furryblackplus.core.exception.moduels.scan.ScanException;
import studio.blacktech.furryblackplus.core.interfaces.EventHandlerFilter;
import studio.blacktech.furryblackplus.core.interfaces.EventHandlerMonitor;
import studio.blacktech.furryblackplus.core.interfaces.EventHandlerRunner;
import studio.blacktech.furryblackplus.core.utilties.LoggerX;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Enumeration;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class Plugin {

    private final LoggerX logger = new LoggerX(this.getClass());

    private final File file;

    private final Map<String, Class<? extends AbstractEventHandler>> modules;

    private final Map<Runner, Class<? extends EventHandlerRunner>> runnerClassMap;
    private final Map<Filter, Class<? extends EventHandlerFilter>> filterClassMap;
    private final Map<Monitor, Class<? extends EventHandlerMonitor>> monitorClassMap;
    private final Map<Executor, Class<? extends EventHandlerExecutor>> executorClassMap;

    @SuppressWarnings("unchecked")
    public Plugin(File file) {

        this.file = file;

        this.modules = new LinkedHashMap<>();
        this.runnerClassMap = new LinkedHashMap<>();
        this.filterClassMap = new LinkedHashMap<>();
        this.monitorClassMap = new LinkedHashMap<>();
        this.executorClassMap = new LinkedHashMap<>();

        String fileName = this.file.getName();


        int index = fileName.lastIndexOf("\\.");

        String pluginName;

        if (index > 0) {
            pluginName = fileName.substring(0, index);
        } else {
            pluginName = fileName;
        }

        JarFile jarFile;

        try {
            jarFile = new JarFile(file);
        } catch (IOException exception) {
            throw new ScanException(exception);
        }

        URLClassLoader classLoader;

        try {
            URL[] urls = {file.toURI().toURL()};
            classLoader = new URLClassLoader(urls, ClassLoader.getSystemClassLoader());
        } catch (MalformedURLException exception) {
            throw new ScanException(exception);
        }


        // =====================================================================


        Enumeration<JarEntry> entries = jarFile.entries();


        while (entries.hasMoreElements()) {

            JarEntry jarEntry = entries.nextElement();

            if (jarEntry.isDirectory()) {
                continue;
            }

            String jarEntryName = jarEntry.getName();

            if (!jarEntryName.endsWith(".class")) {
                continue;
            }

            String className = jarEntryName.substring(0, jarEntryName.length() - 6).replace("/", ".");

            Class<?> clazz;

            try {
                clazz = Class.forName(className, false, classLoader);
            } catch (ClassNotFoundException exception) {
                this.logger.warning("加载类失败 " + pluginName + ":" + className, exception);
                continue;
            }

            if (!AbstractEventHandler.class.isAssignableFrom(clazz)) {
                continue;
            }

            if (EventHandlerRunner.class.isAssignableFrom(clazz)) {
                if (!clazz.isAnnotationPresent(Runner.class)) {
                    this.logger.warning("发现无注解模块 不予注册 " + pluginName);
                    continue;
                }
                Runner annotation = clazz.getAnnotation(Runner.class);
                String moduleName = annotation.value();
                if (this.modules.containsKey(moduleName)) {
                    Class<? extends AbstractEventHandler> exist = this.modules.get(moduleName);
                    this.logger.warning("发现自冲突 " + clazz.getName() + " " + moduleName + " " + exist.getName());
                    this.logger.warning("不予注册插件 " + pluginName);
                    throw new ScanException("发现垃圾插件 包含自冲突");
                }
                this.modules.put(moduleName, (Class<? extends AbstractEventHandler>) clazz);
                this.runnerClassMap.put(annotation, (Class<? extends EventHandlerRunner>) clazz);
            } else if (EventHandlerFilter.class.isAssignableFrom(clazz)) {
                if (!clazz.isAnnotationPresent(Filter.class)) {
                    this.logger.warning("发现无注解模块 不予注册 " + pluginName);
                    continue;
                }
                Filter annotation = clazz.getAnnotation(Filter.class);
                String moduleName = annotation.value();
                if (this.modules.containsKey(moduleName)) {
                    Class<? extends AbstractEventHandler> exist = this.modules.get(moduleName);
                    this.logger.warning("发现自冲突 " + clazz.getName() + " " + moduleName + " " + exist.getName());
                    this.logger.warning("不予注册插件 " + pluginName);
                    throw new ScanException("发现垃圾插件 包含自冲突");
                }
                this.modules.put(moduleName, (Class<? extends AbstractEventHandler>) clazz);
                this.filterClassMap.put(annotation, (Class<? extends EventHandlerFilter>) clazz);
            } else if (EventHandlerMonitor.class.isAssignableFrom(clazz)) {
                if (!clazz.isAnnotationPresent(Monitor.class)) {
                    this.logger.warning("发现无注解模块 不予注册 " + pluginName);
                    continue;
                }
                Monitor annotation = clazz.getAnnotation(Monitor.class);
                String moduleName = annotation.value();
                if (this.modules.containsKey(moduleName)) {
                    Class<? extends AbstractEventHandler> exist = this.modules.get(moduleName);
                    this.logger.warning("发现自冲突 " + clazz.getName() + " " + moduleName + " " + exist.getName());
                    this.logger.warning("不予注册插件 " + pluginName);
                    throw new ScanException("发现垃圾插件 包含自冲突");
                }
                this.modules.put(moduleName, (Class<? extends AbstractEventHandler>) clazz);
                this.monitorClassMap.put(annotation, (Class<? extends EventHandlerMonitor>) clazz);
            } else if (EventHandlerExecutor.class.isAssignableFrom(clazz)) {
                if (!clazz.isAnnotationPresent(Executor.class)) {
                    this.logger.warning("发现无注解模块 不予注册 " + pluginName);
                    continue;
                }
                Executor annotation = clazz.getAnnotation(Executor.class);
                String moduleName = annotation.value();
                if (this.modules.containsKey(moduleName)) {
                    Class<? extends AbstractEventHandler> exist = this.modules.get(moduleName);
                    this.logger.warning("发现自冲突 " + clazz.getName() + " " + moduleName + " " + exist.getName());
                    this.logger.warning("不予注册插件 " + pluginName);
                    throw new ScanException("发现垃圾插件 包含自冲突");
                }
                this.modules.put(moduleName, (Class<? extends AbstractEventHandler>) clazz);
                this.executorClassMap.put(annotation, (Class<? extends EventHandlerExecutor>) clazz);
            }

            this.logger.warning("不支持自行创建的分支模块 不予注册 " + pluginName + ":" + className);

        }


        // =====================================================================


        try {
            classLoader.close();
        } catch (IOException exception) {
            throw new ScanException(exception);
        }

        try {
            jarFile.close();
        } catch (IOException exception) {
            throw new ScanException(exception);
        }


    }
}
