/*
 * Copyright (C) 2021 Alceatraz @ BlackTechStudio
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the BTS Anti Commercial & GNU Affero General.

 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * BTS Anti Commercial & GNU Affero General Public License for more details.
 *
 * You should have received a copy of the BTS Anti Commercial & GNU Affero
 * General Public License along with this program in README or LICENSE.
 */

package studio.blacktech.furryblackplus.core.define.schema;

import studio.blacktech.furryblackplus.core.define.annotation.Checker;
import studio.blacktech.furryblackplus.core.define.annotation.Executor;
import studio.blacktech.furryblackplus.core.define.annotation.Filter;
import studio.blacktech.furryblackplus.core.define.annotation.Monitor;
import studio.blacktech.furryblackplus.core.define.annotation.Runner;
import studio.blacktech.furryblackplus.core.define.moduel.AbstractEventHandler;
import studio.blacktech.furryblackplus.core.define.moduel.EventHandlerChecker;
import studio.blacktech.furryblackplus.core.define.moduel.EventHandlerExecutor;
import studio.blacktech.furryblackplus.core.define.moduel.EventHandlerFilter;
import studio.blacktech.furryblackplus.core.define.moduel.EventHandlerMonitor;
import studio.blacktech.furryblackplus.core.define.moduel.EventHandlerRunner;
import studio.blacktech.furryblackplus.core.exception.moduels.scan.ScanException;
import studio.blacktech.furryblackplus.core.utilties.logger.LoggerX;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import static studio.blacktech.furryblackplus.core.utilties.logger.LoggerX.hash;

public class Plugin {

    private final LoggerX logger;

    private final File file;

    private final String name;

    private final Map<String, Class<? extends AbstractEventHandler>> modules;

    private final Map<Runner, Class<? extends EventHandlerRunner>> runnerClassMap;
    private final Map<Filter, Class<? extends EventHandlerFilter>> filterClassMap;
    private final Map<Monitor, Class<? extends EventHandlerMonitor>> monitorClassMap;
    private final Map<Checker, Class<? extends EventHandlerChecker>> checkerClassMap;
    private final Map<Executor, Class<? extends EventHandlerExecutor>> executorClassMap;

    public File getFile() {
        return this.file;
    }

    public String getName() {
        return this.name;
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

    public Plugin(File file) {

        this.file = file;

        this.modules = new LinkedHashMap<>();
        this.runnerClassMap = new LinkedHashMap<>();
        this.filterClassMap = new LinkedHashMap<>();
        this.monitorClassMap = new LinkedHashMap<>();
        this.checkerClassMap = new LinkedHashMap<>();
        this.executorClassMap = new LinkedHashMap<>();

        String fileName = this.file.getName();

        int index = fileName.lastIndexOf(".");

        if (index > 0) {
            this.name = fileName.substring(0, index);
        } else {
            this.name = fileName;
        }

        this.logger = new LoggerX(this.name);
    }


    @SuppressWarnings({"unchecked", "DuplicatedCode"})
    public void scan() {

        URL[] urls;

        try {
            urls = new URL[]{this.file.toURI().toURL()};
        } catch (MalformedURLException exception) {
            throw new ScanException(exception);
        }

        this.logger.seek("扫描插件包 " + urls[0]);

        ClassLoader systemClassLoader = ClassLoader.getSystemClassLoader();

        try (
            JarFile jarFile = new JarFile(this.file);
            URLClassLoader classLoader = new URLClassLoader(urls, systemClassLoader)
        ) {

            Enumeration<JarEntry> entries = jarFile.entries();

            Map<String, Class<? extends EventHandlerExecutor>> commands = new HashMap<>();

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
                    this.logger.warning("加载类失败 " + this.name + ":" + className, exception);
                    continue;
                }

                if (!AbstractEventHandler.class.isAssignableFrom(clazz)) {
                    continue;
                }

                String clazzHashName = clazz.getName() + ":" + hash(clazz);

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
                    this.logger.info("定时器 -> " + clazzHashName);

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
                        this.logger.info("过滤器 -> " + clazzHashName);
                    } else {
                        this.logger.warning("发现未启用过滤器 " + clazzHashName);
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
                        this.logger.info("监视器 -> " + clazzHashName);
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
                        this.logger.info("检查器 -> " + clazzHashName);
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
                        this.logger.info("执行器 -> " + clazzHashName);
                    } else {
                        this.logger.warning("发现未启用执行器 " + clazzHashName);
                    }

                    continue;
                }

                this.logger.warning("不支持自行创建的分支模块 不予注册 " + this.name + ":" + className);

            }

        } catch (IOException exception) {
            throw new ScanException(exception);
        }
    }
}