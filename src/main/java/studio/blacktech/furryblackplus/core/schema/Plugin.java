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


package studio.blacktech.furryblackplus.core.schema;


import studio.blacktech.furryblackplus.FurryBlack;
import studio.blacktech.furryblackplus.core.common.exception.BotException;
import studio.blacktech.furryblackplus.core.common.exception.moduels.scan.ScanException;
import studio.blacktech.furryblackplus.core.common.logger.LoggerXFactory;
import studio.blacktech.furryblackplus.core.common.logger.base.LoggerX;
import studio.blacktech.furryblackplus.core.handler.EventHandlerChecker;
import studio.blacktech.furryblackplus.core.handler.EventHandlerExecutor;
import studio.blacktech.furryblackplus.core.handler.EventHandlerFilter;
import studio.blacktech.furryblackplus.core.handler.EventHandlerMonitor;
import studio.blacktech.furryblackplus.core.handler.EventHandlerRunner;
import studio.blacktech.furryblackplus.core.handler.annotation.Checker;
import studio.blacktech.furryblackplus.core.handler.annotation.Executor;
import studio.blacktech.furryblackplus.core.handler.annotation.Filter;
import studio.blacktech.furryblackplus.core.handler.annotation.Monitor;
import studio.blacktech.furryblackplus.core.handler.annotation.Runner;
import studio.blacktech.furryblackplus.core.handler.common.AbstractEventHandler;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Paths;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.jar.Attributes;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.Manifest;
import java.util.regex.Pattern;


@SuppressWarnings("unused")


public class Plugin {


    private static final Pattern PATTERN = Pattern.compile("^[0-9a-z_-]{8,64}$");


    private final LoggerX logger;


    private final File file;
    private final String name;


    private Map<String, Class<? extends AbstractEventHandler>> modules;
    private Map<Runner, Class<? extends EventHandlerRunner>> runnerClassMap;
    private Map<Filter, Class<? extends EventHandlerFilter>> filterClassMap;
    private Map<Monitor, Class<? extends EventHandlerMonitor>> monitorClassMap;
    private Map<Checker, Class<? extends EventHandlerChecker>> checkerClassMap;
    private Map<Executor, Class<? extends EventHandlerExecutor>> executorClassMap;


    public static Plugin load(File file) {

        String name;

        try (JarFile jarFile = new JarFile(file)) {

            Manifest manifest;
            try {
                manifest = jarFile.getManifest();
            } catch (IOException exception) {
                throw new ScanException("加载MANIFEST失败 -> " + file.getAbsolutePath(), exception);
            }

            Attributes attributes = manifest.getAttributes("FurryBlack-Extension");
            if (attributes == null || attributes.isEmpty()) {
                throw new ScanException("加载插件失败: MANIFEST不包含FurryBlack-Extension标签组");
            }

            String loaderVersion = attributes.getValue("Loader-Version");

            if (loaderVersion == null) {
                throw new ScanException("加载插件失败: MANIFEST中FurryBlack-Extension标签组不含Loader-Version");
            }

            if (!"1".equals(loaderVersion)) {
                throw new ScanException("加载插件失败: 加载器版本不符，此插件声明其版本为 " + loaderVersion);
            }

            name = attributes.getValue("Extension-Name");

            if (name == null) {
                throw new ScanException("加载插件失败: MANIFEST中FurryBlack-Extension标签组不含Extension-Name");
            }

            if (!PATTERN.matcher(name).find()) {
                throw new ScanException("加载插件失败: 插件包名非法，此插件声明其名称为 " + name);
            }

        } catch (IOException exception) {
            throw new ScanException(exception);
        }

        Plugin plugin;
        try {
            plugin = new Plugin(file, name);
        } catch (Exception exception) {
            throw new ScanException(exception);
        }
        return plugin;
    }


    // =========================================================================


    private Plugin(File file, String name) {

        this.file = file;
        this.name = name;

        this.logger = LoggerXFactory.newLogger(this.name);

    }


    @SuppressWarnings("unchecked")
    public void scan() {

        // =====================================================================

        File depend = Paths.get(FurryBlack.getDependFolder(), this.name).toFile();

        // =====================================================================

        List<URL> tempURL = new LinkedList<>();

        try (JarFile jarFile = new JarFile(this.file)) {

            if (depend.exists()) {
                if (!depend.isDirectory()) {
                    throw new BotException("依赖文件不是目录 -> " + depend.getAbsolutePath());
                }
                File[] dependFiles = depend.listFiles();
                if (dependFiles == null) {
                    throw new BotException("列出依赖文件失败 -> " + depend.getAbsolutePath());
                }
                int size = dependFiles.length;

                for (File dependFile : dependFiles) {
                    if (dependFile.isDirectory()) {
                        continue;
                    }
                    URL url;
                    try {
                        url = dependFile.toURI().toURL();
                    } catch (MalformedURLException exception) {
                        throw new RuntimeException("That should not possible", exception);
                    }
                    tempURL.add(url);
                }
            }


            URL[] urls = tempURL.toArray(new URL[0]);

            this.logger.seek("加载依赖 -> " + depend.getAbsolutePath() + "[" + urls.length + "]");

            URLClassLoader dependClassLoader = new URLClassLoader(urls);

            URL pluginURL;
            try {
                pluginURL = this.file.toURI().toURL();
            } catch (MalformedURLException exception) {
                throw new RuntimeException("That should not possible", exception);
            }

            try (URLClassLoader pluginClassLoader = new URLClassLoader(new URL[]{pluginURL}, dependClassLoader)) {

                Map<String, Class<? extends EventHandlerExecutor>> commands = new HashMap<>();

                Enumeration<JarEntry> entries = jarFile.entries();

                // =====================================================================

                this.modules = new LinkedHashMap<>();
                this.runnerClassMap = new LinkedHashMap<>();
                this.filterClassMap = new LinkedHashMap<>();
                this.monitorClassMap = new LinkedHashMap<>();
                this.checkerClassMap = new LinkedHashMap<>();
                this.executorClassMap = new LinkedHashMap<>();

                // =====================================================================


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


                    // =========================================================================================================


                    Class<?> clazz;

                    try {
                        clazz = Class.forName(className, false, pluginClassLoader);
                    } catch (ClassNotFoundException exception) {
                        this.logger.warning("加载类失败 " + this.name + ":" + className, exception);
                        continue;
                    }

                    if (!AbstractEventHandler.class.isAssignableFrom(clazz)) {
                        continue;
                    }


                    String clazzName = clazz.getName();


                    // =========================================================================================================


                    if (EventHandlerRunner.class.isAssignableFrom(clazz)) {

                        if (!clazz.isAnnotationPresent(Runner.class)) {
                            this.logger.warning("发现无注解模块 不予注册 " + this.name);
                            continue;
                        }

                        Runner annotation = clazz.getAnnotation(Runner.class);

                        String moduleName = annotation.value();

                        if (this.modules.containsKey(moduleName)) {
                            Class<? extends AbstractEventHandler> exist = this.modules.get(moduleName);
                            this.logger.warning("发现自冲突 " + clazz.getName() + " " + moduleName + " " + exist.getName());
                            this.logger.warning("不予注册插件 " + this.name);
                            throw new ScanException("发现垃圾插件 包含自冲突");
                        }

                        this.modules.put(moduleName, (Class<? extends AbstractEventHandler>) clazz);
                        this.runnerClassMap.put(annotation, (Class<? extends EventHandlerRunner>) clazz);
                        this.logger.info("定时器 -> " + clazzName);

                        continue;


                    } else if (EventHandlerFilter.class.isAssignableFrom(clazz)) {

                        if (!clazz.isAnnotationPresent(Filter.class)) {
                            this.logger.warning("发现无注解模块 不予注册 " + this.name);
                            continue;
                        }

                        Filter annotation = clazz.getAnnotation(Filter.class);

                        String moduleName = annotation.value();

                        if (this.modules.containsKey(moduleName)) {
                            Class<? extends AbstractEventHandler> exist = this.modules.get(moduleName);
                            this.logger.warning("发现自冲突 " + clazz.getName() + " " + moduleName + " " + exist.getName());
                            this.logger.warning("不予注册插件 " + this.name);
                            throw new ScanException("发现垃圾插件 包含自冲突");
                        }

                        if (annotation.users() || annotation.group()) {
                            this.modules.put(moduleName, (Class<? extends AbstractEventHandler>) clazz);
                            this.filterClassMap.put(annotation, (Class<? extends EventHandlerFilter>) clazz);
                            this.logger.info("过滤器 -> " + clazzName);
                        } else {
                            this.logger.warning("发现未启用过滤器 " + clazzName);
                        }

                        continue;


                    } else if (EventHandlerMonitor.class.isAssignableFrom(clazz)) {

                        if (!clazz.isAnnotationPresent(Monitor.class)) {
                            this.logger.warning("发现无注解模块 不予注册 " + this.name);
                            continue;
                        }

                        Monitor annotation = clazz.getAnnotation(Monitor.class);

                        String moduleName = annotation.value();

                        if (this.modules.containsKey(moduleName)) {
                            Class<? extends AbstractEventHandler> exist = this.modules.get(moduleName);
                            this.logger.warning("发现自冲突 " + clazz.getName() + " " + moduleName + " " + exist.getName());
                            this.logger.warning("不予注册插件 " + this.name);
                            throw new ScanException("发现垃圾插件 包含自冲突");
                        }

                        if (annotation.users() || annotation.group()) {
                            this.modules.put(moduleName, (Class<? extends AbstractEventHandler>) clazz);
                            this.monitorClassMap.put(annotation, (Class<? extends EventHandlerMonitor>) clazz);
                            this.logger.info("监视器 -> " + clazzName);
                        } else {
                            this.logger.warning("发现未启用监听器 " + clazz.getName());
                        }

                        continue;


                    } else if (EventHandlerChecker.class.isAssignableFrom(clazz)) {

                        if (!clazz.isAnnotationPresent(Checker.class)) {
                            this.logger.warning("发现无注解模块 不予注册 " + this.name);
                            continue;
                        }

                        Checker annotation = clazz.getAnnotation(Checker.class);

                        String moduleName = annotation.value();

                        if (this.modules.containsKey(moduleName)) {
                            Class<? extends AbstractEventHandler> exist = this.modules.get(moduleName);
                            this.logger.warning("发现自冲突 " + clazz.getName() + " " + moduleName + " " + exist.getName());
                            this.logger.warning("不予注册插件 " + this.name);
                            throw new ScanException("发现垃圾插件 包含自冲突");
                        }

                        if (annotation.users() || annotation.group()) {
                            this.modules.put(moduleName, (Class<? extends AbstractEventHandler>) clazz);
                            this.checkerClassMap.put(annotation, (Class<? extends EventHandlerChecker>) clazz);
                            this.logger.info("检查器 -> " + clazzName);
                        } else {
                            this.logger.warning("发现未启用检查器 " + clazz.getName());
                        }

                        continue;


                    } else if (EventHandlerExecutor.class.isAssignableFrom(clazz)) {

                        if (!clazz.isAnnotationPresent(Executor.class)) {
                            this.logger.warning("发现无注解模块 不予注册 " + this.name);
                            continue;
                        }

                        Executor annotation = clazz.getAnnotation(Executor.class);

                        String moduleName = annotation.value();

                        if (this.modules.containsKey(moduleName)) {
                            Class<? extends AbstractEventHandler> exist = this.modules.get(moduleName);
                            this.logger.warning("发现自冲突 " + clazz.getName() + " " + moduleName + " " + exist.getName());
                            this.logger.warning("不予注册插件 " + this.name);
                            throw new ScanException("发现垃圾插件 包含自冲突");
                        }

                        String command = annotation.command();

                        if (commands.containsKey(command)) {
                            Class<? extends EventHandlerExecutor> exist = commands.get(command);
                            this.logger.warning("发现自冲突命令 " + command + " " + clazz.getName() + " " + moduleName + " " + exist.getName());
                            this.logger.warning("不予注册插件 " + this.name);
                            throw new ScanException("发现垃圾插件 包含自冲突");
                        }

                        if (annotation.users() || annotation.group()) {
                            commands.put(command, (Class<? extends EventHandlerExecutor>) clazz);
                            this.modules.put(moduleName, (Class<? extends AbstractEventHandler>) clazz);
                            this.executorClassMap.put(annotation, (Class<? extends EventHandlerExecutor>) clazz);
                            this.logger.info("执行器 -> " + clazzName);
                        } else {
                            this.logger.warning("发现未启用执行器 " + clazzName);
                        }

                        continue;


                    }

                    this.logger.warning("不支持自行创建的分支模块 不予注册 " + this.name + ":" + className);

                }
            }
        } catch (IOException exception) {
            throw new ScanException(exception);
        }
    }


    public String getName() {
        return this.name;
    }

    public File getFile() {
        return this.file;
    }

    public Map<String, Class<? extends AbstractEventHandler>> getModules() {
        return this.modules;
    }

    public Map<Runner, Class<? extends EventHandlerRunner>> getRunnerClassMap() {
        return this.runnerClassMap;
    }

    public Map<Filter, Class<? extends EventHandlerFilter>> getFilterClassMap() {
        return this.filterClassMap;
    }

    public Map<Monitor, Class<? extends EventHandlerMonitor>> getMonitorClassMap() {
        return this.monitorClassMap;
    }

    public Map<Checker, Class<? extends EventHandlerChecker>> getCheckerClassMap() {
        return this.checkerClassMap;
    }

    public Map<Executor, Class<? extends EventHandlerExecutor>> getExecutorClassMap() {
        return this.executorClassMap;
    }

}
