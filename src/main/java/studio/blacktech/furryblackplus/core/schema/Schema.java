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
import studio.blacktech.furryblackplus.core.common.exception.moduels.boot.BootException;
import studio.blacktech.furryblackplus.core.common.exception.moduels.load.LoadException;
import studio.blacktech.furryblackplus.core.common.exception.moduels.scan.ScanException;
import studio.blacktech.furryblackplus.core.common.logger.LoggerXFactory;
import studio.blacktech.furryblackplus.core.common.logger.base.LoggerX;
import studio.blacktech.furryblackplus.core.common.logger.base.LoggerX.Color;
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
import java.lang.reflect.InvocationTargetException;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.NavigableMap;
import java.util.Set;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.CopyOnWriteArrayList;

import static studio.blacktech.furryblackplus.core.common.logger.base.LoggerX.hexHash;

public class Schema {


    private final LoggerX logger = LoggerXFactory.newLogger(Schema.class);


    private final File folder;

    private final Map<String, Plugin> plugins;

    private final Map<String, Class<? extends AbstractEventHandler>> modules;

    private final Map<Runner, Class<? extends EventHandlerRunner>> COMPONENT_RUNNER_CLAZZ;
    private final Map<Filter, Class<? extends EventHandlerFilter>> COMPONENT_FILTER_CLAZZ;
    private final Map<Monitor, Class<? extends EventHandlerMonitor>> COMPONENT_MONITOR_CLAZZ;
    private final Map<Checker, Class<? extends EventHandlerChecker>> COMPONENT_CHECKER_CLAZZ;
    private final NavigableMap<Executor, Class<? extends EventHandlerExecutor>> COMPONENT_EXECUTOR_CLAZZ;

    private final List<Runner> SORTED_RUNNER;
    private final List<Filter> SORTED_FILTER;
    private final List<Monitor> SORTED_MONITOR;
    private final List<Checker> SORTED_CHECKER;

    private final Map<Runner, EventHandlerRunner> COMPONENT_RUNNER_INSTANCE;
    private final Map<Filter, EventHandlerFilter> COMPONENT_FILTER_INSTANCE;
    private final Map<Monitor, EventHandlerMonitor> COMPONENT_MONITOR_INSTANCE;
    private final Map<Checker, EventHandlerChecker> COMPONENT_CHECKER_INSTANCE;
    private final NavigableMap<Executor, EventHandlerExecutor> COMPONENT_EXECUTOR_INSTANCE;

    private final Map<String, Executor> COMMAND_EXECUTOR_RELATION;
    private final Map<String, String> MODULE_PLUGIN_RELATION;

    private final List<EventHandlerFilter> FILTER_USERS_CHAIN;
    private final List<EventHandlerFilter> FILTER_GROUP_CHAIN;

    private final List<EventHandlerMonitor> MONITOR_USERS_CHAIN;
    private final List<EventHandlerMonitor> MONITOR_GROUP_CHAIN;

    private final Map<String, EventHandlerExecutor> EXECUTOR_USERS_POOL;
    private final Map<String, EventHandlerExecutor> EXECUTOR_GROUP_POOL;

    private final List<EventHandlerChecker> GLOBAL_CHECKER_USERS_POOL;
    private final List<EventHandlerChecker> GLOBAL_CHECKER_GROUP_POOL;

    private final Map<String, List<EventHandlerChecker>> COMMAND_CHECKER_USERS_POOL;
    private final Map<String, List<EventHandlerChecker>> COMMAND_CHECKER_GROUP_POOL;


    public Schema(File folder) {

        this.logger.hint("加载插件模型");

        this.folder = folder;

        this.plugins = new HashMap<>();
        this.modules = new HashMap<>();

        this.COMPONENT_RUNNER_CLAZZ = new HashMap<>();
        this.COMPONENT_FILTER_CLAZZ = new HashMap<>();
        this.COMPONENT_MONITOR_CLAZZ = new HashMap<>();
        this.COMPONENT_CHECKER_CLAZZ = new HashMap<>();
        this.COMPONENT_EXECUTOR_CLAZZ = new TreeMap<>(Schema::compare);

        this.SORTED_RUNNER = new LinkedList<>();
        this.SORTED_FILTER = new LinkedList<>();
        this.SORTED_MONITOR = new LinkedList<>();
        this.SORTED_CHECKER = new LinkedList<>();

        this.COMPONENT_RUNNER_INSTANCE = new ConcurrentHashMap<>();
        this.COMPONENT_FILTER_INSTANCE = new ConcurrentHashMap<>();
        this.COMPONENT_MONITOR_INSTANCE = new ConcurrentHashMap<>();
        this.COMPONENT_CHECKER_INSTANCE = new ConcurrentHashMap<>();
        this.COMPONENT_EXECUTOR_INSTANCE = new ConcurrentSkipListMap<>(Schema::compare);

        this.COMMAND_EXECUTOR_RELATION = new HashMap<>();
        this.MODULE_PLUGIN_RELATION = new HashMap<>();

        this.FILTER_USERS_CHAIN = new CopyOnWriteArrayList<>();
        this.FILTER_GROUP_CHAIN = new CopyOnWriteArrayList<>();

        this.MONITOR_USERS_CHAIN = new CopyOnWriteArrayList<>();
        this.MONITOR_GROUP_CHAIN = new CopyOnWriteArrayList<>();

        this.EXECUTOR_USERS_POOL = new ConcurrentHashMap<>();
        this.EXECUTOR_GROUP_POOL = new ConcurrentHashMap<>();

        this.GLOBAL_CHECKER_USERS_POOL = new CopyOnWriteArrayList<>();
        this.GLOBAL_CHECKER_GROUP_POOL = new CopyOnWriteArrayList<>();

        this.COMMAND_CHECKER_USERS_POOL = new ConcurrentHashMap<>();
        this.COMMAND_CHECKER_GROUP_POOL = new ConcurrentHashMap<>();

    }


    // =================================================================================================================
    //
    // 功能接口
    //
    // =================================================================================================================


    public String generateUsersExecutorList() {
        if (this.EXECUTOR_USERS_POOL.size() == 0) {
            return "没有任何已装载的命令";
        }
        StringBuilder builder = new StringBuilder();
        for (Executor executor : this.COMPONENT_EXECUTOR_INSTANCE.keySet()) {
            if (!this.EXECUTOR_USERS_POOL.containsKey(executor.command())) {
                continue;
            }
            builder.append(executor.outline());
            builder.append("[");
            builder.append(executor.command());
            builder.append("]");
            builder.append(executor.description());
            builder.append("\r\n");
        }
        builder.setLength(builder.length() - 2);
        return builder.toString();
    }


    public String generateGroupExecutorList() {
        if (this.EXECUTOR_GROUP_POOL.size() == 0) {
            return "没有任何已装载的命令";
        }
        StringBuilder builder = new StringBuilder();
        for (Executor executor : this.COMPONENT_EXECUTOR_INSTANCE.keySet()) {
            if (!this.EXECUTOR_GROUP_POOL.containsKey(executor.command())) {
                continue;
            }
            builder.append(executor.outline());
            builder.append("[");
            builder.append(executor.command());
            builder.append("]");
            builder.append(executor.description());
            builder.append("\r\n");
        }
        builder.setLength(builder.length() - 2);
        return builder.toString();
    }


    // =================================================================================================================


    public Set<Map.Entry<String, Plugin>> getAllPlugin() {
        return this.plugins.entrySet();
    }


    // =================================================================================================================


    public Map<String, Boolean> listAllModule() {

        Map<String, Boolean> result = new LinkedHashMap<>();

        for (Runner annotation : this.SORTED_RUNNER) {
            result.put(annotation.value(), this.COMPONENT_RUNNER_INSTANCE.containsKey(annotation));
        }

        for (Filter annotation : this.SORTED_FILTER) {
            result.put(annotation.value(), this.COMPONENT_FILTER_CLAZZ.containsKey(annotation));
        }

        for (Monitor annotation : this.SORTED_MONITOR) {
            result.put(annotation.value(), this.COMPONENT_MONITOR_INSTANCE.containsKey(annotation));
        }

        for (Checker annotation : this.SORTED_CHECKER) {
            result.put(annotation.value(), this.COMPONENT_CHECKER_CLAZZ.containsKey(annotation));
        }

        for (Executor annotation : this.COMPONENT_EXECUTOR_CLAZZ.keySet()) {
            result.put(annotation.value(), this.COMPONENT_EXECUTOR_INSTANCE.containsKey(annotation));
        }

        return result;
    }


    // =================================================================================================================


    public Map<Runner, Boolean> listAllRunner() {
        Map<Runner, Boolean> result = new LinkedHashMap<>();
        for (Runner annotation : this.COMPONENT_RUNNER_CLAZZ.keySet()) {
            result.put(annotation, this.COMPONENT_RUNNER_INSTANCE.containsKey(annotation));
        }
        return result;
    }

    public Map<Filter, Boolean> listAllFilter() {
        Map<Filter, Boolean> result = new LinkedHashMap<>();
        for (Filter annotation : this.COMPONENT_FILTER_CLAZZ.keySet()) {
            result.put(annotation, this.COMPONENT_FILTER_INSTANCE.containsKey(annotation));
        }
        return result;
    }

    public Map<Monitor, Boolean> listAllMonitor() {
        Map<Monitor, Boolean> result = new LinkedHashMap<>();
        for (Monitor annotation : this.COMPONENT_MONITOR_CLAZZ.keySet()) {
            result.put(annotation, this.COMPONENT_MONITOR_INSTANCE.containsKey(annotation));
        }
        return result;
    }

    public Map<Checker, Boolean> listAllChecker() {
        Map<Checker, Boolean> result = new LinkedHashMap<>();
        for (Checker annotation : this.COMPONENT_CHECKER_CLAZZ.keySet()) {
            result.put(annotation, this.COMPONENT_CHECKER_INSTANCE.containsKey(annotation));
        }
        return result;
    }

    public Map<Executor, Boolean> listAllExecutor() {
        Map<Executor, Boolean> result = new LinkedHashMap<>();
        for (Executor annotation : this.COMPONENT_EXECUTOR_CLAZZ.keySet()) {
            result.put(annotation, this.COMPONENT_EXECUTOR_INSTANCE.containsKey(annotation));
        }
        return result;
    }

    public List<Checker> listGlobalUsersChecker() {
        return this.GLOBAL_CHECKER_USERS_POOL.stream().map(item -> item.getClass().getAnnotation(Checker.class)).toList();
    }

    public List<Checker> listGlobalGroupChecker() {
        return this.GLOBAL_CHECKER_USERS_POOL.stream().map(item -> item.getClass().getAnnotation(Checker.class)).toList();
    }

    public Map<String, List<Checker>> listCommandsUsersChecker() {
        Map<String, List<Checker>> result = new LinkedHashMap<>();
        for (Map.Entry<String, List<EventHandlerChecker>> entry : this.COMMAND_CHECKER_USERS_POOL.entrySet()) {
            var k = entry.getKey();
            var v = entry.getValue();
            List<Checker> collect = v.stream().map(item -> item.getClass().getAnnotation(Checker.class)).toList();
            result.put(k, collect);
        }
        return result;
    }

    public Map<String, List<Checker>> listCommandsGroupChecker() {
        Map<String, List<Checker>> result = new LinkedHashMap<>();
        for (Map.Entry<String, List<EventHandlerChecker>> entry : this.COMMAND_CHECKER_GROUP_POOL.entrySet()) {
            var k = entry.getKey();
            var v = entry.getValue();
            List<Checker> collect = v.stream().map(item -> item.getClass().getAnnotation(Checker.class)).toList();
            result.put(k, collect);
        }
        return result;
    }


    // =================================================================================================================


    public void shutModule(String name) {
        AbstractEventHandler moduleInstance = this.getModuleInstance(name);
        if (moduleInstance == null) {
            System.out.println("没有找到模块实例 -> " + name + " " + (this.getModuleClass(name) == null ? "不存在" : "未加载"));
            return;
        }
        String instanceName = moduleInstance.getClass().getName();
        this.logger.info("关闭模块 " + name + " -> " + instanceName);
        try {
            moduleInstance.shutWrapper();
        } catch (Exception exception) {
            this.logger.warning("关闭模块发生错误 " + name + " " + instanceName, exception);
        }
    }


    public void initModule(String name) {
        AbstractEventHandler moduleInstance = this.getModuleInstance(name);
        if (moduleInstance == null) {
            System.out.println("没有找到模块实例 -> " + name + " " + (this.getModuleClass(name) == null ? "不存在" : "未加载"));
            return;
        }
        String instanceName = moduleInstance.getClass().getName();
        this.logger.info("预载模块 " + name + " -> " + instanceName);
        try {
            moduleInstance.initWrapper();
        } catch (Exception exception) {
            this.logger.warning("预载模块发生错误 " + name + " " + instanceName, exception);
        }
    }


    public void bootModule(String name) {
        AbstractEventHandler moduleInstance = this.getModuleInstance(name);
        if (moduleInstance == null) {
            System.out.println("没有找到模块实例 -> " + name + " " + (this.getModuleClass(name) == null ? "不存在" : "未加载"));
            return;
        }
        String instanceName = moduleInstance.getClass().getName();
        this.logger.info("启动模块 " + name + " -> " + instanceName);
        try {
            moduleInstance.bootWrapper();
        } catch (Exception exception) {
            this.logger.warning("启动模块发生错误 " + name + " " + instanceName, exception);
        }
    }


    public void rebootModule(String name) {
        AbstractEventHandler moduleInstance = this.getModuleInstance(name);
        if (moduleInstance == null) {
            System.out.println("没有找到模块实例 -> " + name + " " + (this.getModuleClass(name) == null ? "不存在" : "未加载"));
            return;
        }
        String instanceName = moduleInstance.getClass().getName();
        this.logger.info("重启模块 " + name + " -> " + instanceName);
        try {
            moduleInstance.shutWrapper();
            moduleInstance.initWrapper();
            moduleInstance.bootWrapper();
        } catch (Exception exception) {
            this.logger.warning("重启模块发生错误 " + name + " " + instanceName, exception);
        }
    }


    public void unloadModule(String name) {

        Class<? extends AbstractEventHandler> clazz = this.modules.get(name);

        if (clazz == null) {
            this.logger.warning("不存在此名称的模块 -> " + name);
            return;
        }

        for (Map.Entry<Runner, EventHandlerRunner> entry : this.COMPONENT_RUNNER_INSTANCE.entrySet()) {
            if (entry.getKey().value().equals(name)) {
                this.unloadModule(entry.getKey());
                return;
            }
        }

        for (Map.Entry<Filter, EventHandlerFilter> entry : this.COMPONENT_FILTER_INSTANCE.entrySet()) {
            if (entry.getKey().value().equals(name)) {
                this.unloadModule(entry.getKey());
                return;
            }
        }

        for (Map.Entry<Monitor, EventHandlerMonitor> entry : this.COMPONENT_MONITOR_INSTANCE.entrySet()) {
            if (entry.getKey().value().equals(name)) {
                this.unloadModule(entry.getKey());
                return;
            }
        }

        for (Map.Entry<Checker, EventHandlerChecker> entry : this.COMPONENT_CHECKER_INSTANCE.entrySet()) {
            if (entry.getKey().value().equals(name)) {
                this.unloadModule(entry.getKey());
                return;
            }
        }

        for (Map.Entry<Executor, EventHandlerExecutor> entry : this.COMPONENT_EXECUTOR_INSTANCE.entrySet()) {
            if (entry.getKey().value().equals(name)) {
                this.unloadModule(entry.getKey());
                return;
            }
        }

        this.logger.warning("此名称的模块未加载 -> " + name);

    }


    public void unloadModule(Runner annotation) {
        EventHandlerRunner instance = this.COMPONENT_RUNNER_INSTANCE.remove(annotation);
        instance.setEnable(false);
        instance.shutWrapper();
        this.logger.info("执行器已卸载 -> " + printAnnotation(annotation));
    }

    public void unloadModule(Filter annotation) {
        EventHandlerFilter instance = this.COMPONENT_FILTER_INSTANCE.remove(annotation);
        instance.setEnable(false);
        if (annotation.users()) this.FILTER_USERS_CHAIN.remove(instance);
        if (annotation.group()) this.FILTER_GROUP_CHAIN.remove(instance);
        instance.shutWrapper();
        this.logger.info("过滤器已卸载 -> " + printAnnotation(annotation));
    }

    public void unloadModule(Monitor annotation) {
        EventHandlerMonitor instance = this.COMPONENT_MONITOR_INSTANCE.remove(annotation);
        instance.setEnable(false);
        if (annotation.users()) this.MONITOR_USERS_CHAIN.remove(instance);
        if (annotation.group()) this.MONITOR_GROUP_CHAIN.remove(instance);
        instance.shutWrapper();
        this.logger.info("监听器已卸载 -> " + printAnnotation(annotation));
    }

    public void unloadModule(Checker annotation) {
        EventHandlerChecker instance = this.COMPONENT_CHECKER_INSTANCE.remove(annotation);
        instance.setEnable(false);
        if (annotation.users()) {
            if ("*".equals(annotation.command())) {
                this.GLOBAL_CHECKER_USERS_POOL.remove(instance);
            } else {
                this.COMMAND_CHECKER_USERS_POOL.get(annotation.command()).remove(instance);
            }
        }
        if (annotation.group()) {
            if ("*".equals(annotation.command())) {
                this.GLOBAL_CHECKER_USERS_POOL.remove(instance);
            } else {
                this.COMMAND_CHECKER_USERS_POOL.get(annotation.command()).remove(instance);
            }
        }
        instance.shutWrapper();
        this.logger.info("检查器已卸载 -> " + printAnnotation(annotation));
    }

    public void unloadModule(Executor annotation) {
        EventHandlerExecutor instance = this.COMPONENT_EXECUTOR_INSTANCE.remove(annotation);
        instance.setEnable(false);
        if (annotation.users()) this.EXECUTOR_USERS_POOL.remove(annotation.command());
        if (annotation.group()) this.EXECUTOR_GROUP_POOL.remove(annotation.command());
        this.COMMAND_EXECUTOR_RELATION.remove(annotation.command());
        instance.shutWrapper();
        this.logger.info("执行器已卸载 -> " + printAnnotation(annotation));
    }


    // =================================================================================================================


    @SuppressWarnings("unchecked")
    public <T extends EventHandlerRunner> T getRunner(Class<T> clazz) {
        List<EventHandlerRunner> collect = this.COMPONENT_RUNNER_INSTANCE.values().stream().filter(clazz::isInstance).toList();
        if (collect.size() == 1) {
            return (T) collect.get(0);
        } else {
            return null;
        }
    }


    // =================================================================================================================


    private Class<? extends AbstractEventHandler> getModuleClass(String name) {

        if (!this.modules.containsKey(name)) {
            return null;
        }

        for (Map.Entry<Runner, Class<? extends EventHandlerRunner>> entry : this.COMPONENT_RUNNER_CLAZZ.entrySet()) {
            if (entry.getKey().value().equals(name)) {
                return entry.getValue();
            }
        }

        for (Map.Entry<Filter, Class<? extends EventHandlerFilter>> entry : this.COMPONENT_FILTER_CLAZZ.entrySet()) {
            if (entry.getKey().value().equals(name)) {
                return entry.getValue();
            }
        }

        for (Map.Entry<Monitor, Class<? extends EventHandlerMonitor>> entry : this.COMPONENT_MONITOR_CLAZZ.entrySet()) {
            if (entry.getKey().value().equals(name)) {
                return entry.getValue();
            }
        }

        for (Map.Entry<Checker, Class<? extends EventHandlerChecker>> entry : this.COMPONENT_CHECKER_CLAZZ.entrySet()) {
            if (entry.getKey().value().equals(name)) {
                return entry.getValue();
            }
        }

        for (Map.Entry<Executor, Class<? extends EventHandlerExecutor>> entry : this.COMPONENT_EXECUTOR_CLAZZ.entrySet()) {
            if (entry.getKey().value().equals(name)) {
                return entry.getValue();
            }
        }

        return null;
    }


    private AbstractEventHandler getModuleInstance(String name) {

        if (!this.modules.containsKey(name)) {
            return null;
        }

        for (Map.Entry<Runner, EventHandlerRunner> entry : this.COMPONENT_RUNNER_INSTANCE.entrySet()) {
            if (entry.getKey().value().equals(name)) {
                return entry.getValue();
            }
        }

        for (Map.Entry<Filter, EventHandlerFilter> entry : this.COMPONENT_FILTER_INSTANCE.entrySet()) {
            if (entry.getKey().value().equals(name)) {
                return entry.getValue();
            }
        }

        for (Map.Entry<Monitor, EventHandlerMonitor> entry : this.COMPONENT_MONITOR_INSTANCE.entrySet()) {
            if (entry.getKey().value().equals(name)) {
                return entry.getValue();
            }
        }

        for (Map.Entry<Checker, EventHandlerChecker> entry : this.COMPONENT_CHECKER_INSTANCE.entrySet()) {
            if (entry.getKey().value().equals(name)) {
                return entry.getValue();
            }
        }

        for (Map.Entry<Executor, EventHandlerExecutor> entry : this.COMPONENT_EXECUTOR_INSTANCE.entrySet()) {
            if (entry.getKey().value().equals(name)) {
                return entry.getValue();
            }
        }

        return null;
    }


    // =================================================================================================================
    //
    // 周期方法
    //
    // =================================================================================================================


    public void find() {

        this.logger.hint("扫描插件目录");

        File[] listFiles = this.folder.listFiles();

        if (listFiles == null) {
            throw new ScanException("无法扫描模块");
        }

        if (listFiles.length == 0) {
            this.logger.warning("插件目录为空");
        }

        this.logger.seek("发现[" + listFiles.length + "]个文件");
        for (File file : listFiles) {
            this.logger.info("尝试加载 -> " + file.getName());
            Plugin plugin = Plugin.load(file);
            String name = plugin.getName();
            if (this.plugins.containsKey(name)) {
                Plugin exist = this.plugins.get(name);
                throw new ScanException("发现插件名称冲突 " + plugin.getFile().getAbsolutePath() + "名称" + name + "已被注册" + exist.getFile().getAbsolutePath());
            }
            this.plugins.put(name, plugin);
        }

        this.logger.seek("发现[" + this.plugins.size() + "]个插件");
        for (Plugin plugin : this.plugins.values()) {
            this.logger.info(plugin.getFile().getName() + " -> " + plugin.getName());
        }
    }


    public void scan() {
        this.logger.hint("扫描插件包内容");
        this.plugins.values().forEach(Plugin::scan);
    }


    // =================================================================================================================


    public void load() {


        this.logger.hint("向插件模型注册模块");


        for (Map.Entry<String, Plugin> pluginEntry : this.plugins.entrySet()) {

            var pluginName = pluginEntry.getKey();
            var pluginPackage = pluginEntry.getValue();

            this.logger.seek("尝试注册插件 -> " + pluginName);

            if (pluginPackage.getModules().isEmpty()) {
                this.logger.warning("插件包内不含任何模块 " + pluginName);
                return;
            }

            this.logger.info("模块冲突检查 -> " + pluginName);

            for (Map.Entry<Runner, Class<? extends EventHandlerRunner>> moduleEntry : pluginPackage.getRunnerClassMap().entrySet()) {
                var k = moduleEntry.getKey();
                var v = moduleEntry.getValue();
                if (this.COMPONENT_RUNNER_CLAZZ.containsKey(k)) {
                    Class<? extends AbstractEventHandler> exist = this.COMPONENT_RUNNER_CLAZZ.get(k);
                    throw new ScanException("发现模块名冲突 " + pluginName + ":" + v.getName() + "与" + this.COMPONENT_RUNNER_CLAZZ.get(k) + ":" + exist.getName());
                }
            }

            for (Map.Entry<Filter, Class<? extends EventHandlerFilter>> moduleEntry : pluginPackage.getFilterClassMap().entrySet()) {
                var k = moduleEntry.getKey();
                var v = moduleEntry.getValue();
                if (this.COMPONENT_FILTER_CLAZZ.containsKey(k)) {
                    Class<? extends AbstractEventHandler> exist = this.COMPONENT_FILTER_CLAZZ.get(k);
                    throw new ScanException("发现模块名冲突 " + pluginName + ":" + v.getName() + "与" + this.COMPONENT_FILTER_CLAZZ.get(k) + ":" + exist.getName());
                }
            }

            for (Map.Entry<Monitor, Class<? extends EventHandlerMonitor>> moduleEntry : pluginPackage.getMonitorClassMap().entrySet()) {
                var k = moduleEntry.getKey();
                var v = moduleEntry.getValue();
                if (this.COMPONENT_MONITOR_CLAZZ.containsKey(k)) {
                    Class<? extends AbstractEventHandler> exist = this.COMPONENT_MONITOR_CLAZZ.get(k);
                    throw new ScanException("发现模块名冲突 " + pluginName + ":" + v.getName() + "与" + this.COMPONENT_MONITOR_CLAZZ.get(k) + ":" + exist.getName());
                }
            }

            for (Map.Entry<Checker, Class<? extends EventHandlerChecker>> moduleEntry : pluginPackage.getCheckerClassMap().entrySet()) {
                var k = moduleEntry.getKey();
                var v = moduleEntry.getValue();
                if (this.COMPONENT_CHECKER_CLAZZ.containsKey(k)) {
                    Class<? extends AbstractEventHandler> exist = this.COMPONENT_CHECKER_CLAZZ.get(k);
                    throw new ScanException("发现模块名冲突 " + pluginName + ":" + v.getName() + "与" + this.COMPONENT_CHECKER_CLAZZ.get(k) + ":" + exist.getName());
                }
            }

            for (Map.Entry<Executor, Class<? extends EventHandlerExecutor>> moduleEntry : pluginPackage.getExecutorClassMap().entrySet()) {
                var k = moduleEntry.getKey();
                var v = moduleEntry.getValue();
                if (this.COMPONENT_EXECUTOR_CLAZZ.containsKey(k)) {
                    Class<? extends AbstractEventHandler> exist = this.COMPONENT_EXECUTOR_CLAZZ.get(k);
                    throw new ScanException("发现模块名冲突 " + pluginName + ":" + v.getName() + "与" + this.COMPONENT_EXECUTOR_CLAZZ.get(k) + ":" + exist.getName());
                }
            }

            for (Map.Entry<Executor, Class<? extends EventHandlerExecutor>> entry : pluginPackage.getExecutorClassMap().entrySet()) {
                var k = entry.getKey();
                var v = entry.getValue();
                String command = k.command();
                if (this.COMMAND_EXECUTOR_RELATION.containsKey(command)) {
                    Executor annotation = this.COMMAND_EXECUTOR_RELATION.get(command);
                    Class<? extends EventHandlerExecutor> exist = this.COMPONENT_EXECUTOR_CLAZZ.get(annotation);
                    String existPluginName = this.MODULE_PLUGIN_RELATION.get(annotation.value());
                    throw new ScanException("发现命令冲突 " + command + " - " + pluginName + ":" + v.getName() + "已注册为" + existPluginName + ":" + exist.getName());
                }
            }

            this.logger.info("冲突检查通过 -> " + pluginName);

            for (Map.Entry<Runner, Class<? extends EventHandlerRunner>> entry : pluginPackage.getRunnerClassMap().entrySet()) {
                var k = entry.getKey();
                var v = entry.getValue();
                String moduleName = k.value();
                this.modules.put(moduleName, v);
                this.SORTED_RUNNER.add(k);
                this.COMPONENT_RUNNER_CLAZZ.put(k, v);
                this.MODULE_PLUGIN_RELATION.put(moduleName, pluginName);
                this.logger.info("注册定时器" + pluginName + ":" + moduleName + "[" + k.priority() + "] -> " + v.getName());
            }

            for (Map.Entry<Filter, Class<? extends EventHandlerFilter>> entry : pluginPackage.getFilterClassMap().entrySet()) {
                var k = entry.getKey();
                var v = entry.getValue();
                String moduleName = k.value();
                this.modules.put(moduleName, v);
                this.SORTED_FILTER.add(k);
                this.COMPONENT_FILTER_CLAZZ.put(k, v);
                this.MODULE_PLUGIN_RELATION.put(moduleName, pluginName);
                this.logger.info("注册过滤器" + pluginName + ":" + moduleName + "[" + k.priority() + "] -> " + v.getName());
            }

            for (Map.Entry<Monitor, Class<? extends EventHandlerMonitor>> entry : pluginPackage.getMonitorClassMap().entrySet()) {
                var k = entry.getKey();
                var v = entry.getValue();
                String moduleName = k.value();
                this.modules.put(moduleName, v);
                this.SORTED_MONITOR.add(k);
                this.COMPONENT_MONITOR_CLAZZ.put(k, v);
                this.MODULE_PLUGIN_RELATION.put(moduleName, pluginName);
                this.logger.info("注册监听器" + pluginName + ":" + moduleName + "[" + k.priority() + "] -> " + v.getName());
            }

            for (Map.Entry<Checker, Class<? extends EventHandlerChecker>> entry : pluginPackage.getCheckerClassMap().entrySet()) {
                var k = entry.getKey();
                var v = entry.getValue();
                String moduleName = k.value();
                this.modules.put(moduleName, v);
                this.SORTED_CHECKER.add(k);
                this.COMPONENT_CHECKER_CLAZZ.put(k, v);
                this.MODULE_PLUGIN_RELATION.put(moduleName, pluginName);
                this.logger.info("注册检查器" + pluginName + ":" + moduleName + "[" + k.priority() + "] -> " + v.getName());
            }

            for (Map.Entry<Executor, Class<? extends EventHandlerExecutor>> entry : pluginPackage.getExecutorClassMap().entrySet()) {
                var k = entry.getKey();
                var v = entry.getValue();
                String moduleName = k.value();
                this.modules.put(moduleName, v);
                this.COMMAND_EXECUTOR_RELATION.put(k.command(), k);
                this.COMPONENT_EXECUTOR_CLAZZ.put(k, v);
                this.MODULE_PLUGIN_RELATION.put(moduleName, pluginName);
                this.logger.info("注册执行器" + pluginName + ":" + moduleName + "[" + k.command() + "] -> " + v.getName());
            }
        }

        this.SORTED_RUNNER.sort(Schema::compare);
        this.SORTED_FILTER.sort(Schema::compare);
        this.SORTED_MONITOR.sort(Schema::compare);
        this.SORTED_CHECKER.sort(Schema::compare);


    }


    // =================================================================================================================


    public void make() {

        this.logger.hint("加载定时器 " + this.COMPONENT_RUNNER_CLAZZ.size());

        for (Runner annotation : this.SORTED_RUNNER) {
            Class<? extends EventHandlerRunner> clazz = this.COMPONENT_RUNNER_CLAZZ.get(annotation);
            String moduleName = annotation.value();
            String pluginName = this.MODULE_PLUGIN_RELATION.get(moduleName);
            Plugin plugin = this.plugins.get(pluginName);
            URLClassLoader dependClassLoader = plugin.getDependClassLoader();
            this.logger.info("加载定时器" + pluginName + ":" + moduleName + "[" + annotation.priority() + "] -> " + clazz.getName());
            EventHandlerRunner instance;
            try {
                instance = clazz.getConstructor().newInstance();
                instance.internalInit(pluginName, moduleName, dependClassLoader);
            } catch (InvocationTargetException | InstantiationException | IllegalAccessException | NoSuchMethodException e) {
                throw new LoadException("加载定时器失败 " + pluginName + ":" + moduleName + "[" + annotation.priority() + "] -> " + clazz.getName());
            }

            this.COMPONENT_RUNNER_INSTANCE.put(annotation, instance);
        }


        this.logger.hint("加载过滤器 " + this.COMPONENT_FILTER_CLAZZ.size());

        for (Filter annotation : this.SORTED_FILTER) {
            Class<? extends EventHandlerFilter> clazz = this.COMPONENT_FILTER_CLAZZ.get(annotation);
            String moduleName = annotation.value();
            String pluginName = this.MODULE_PLUGIN_RELATION.get(moduleName);
            Plugin plugin = this.plugins.get(pluginName);
            URLClassLoader dependClassLoader = plugin.getDependClassLoader();
            this.logger.info("加载过滤器" + pluginName + ":" + moduleName + "[" + annotation.priority() + "] -> " + clazz.getName());
            EventHandlerFilter instance;
            try {
                instance = clazz.getConstructor().newInstance();
                instance.internalInit(pluginName, moduleName, dependClassLoader);
            } catch (InvocationTargetException | InstantiationException | IllegalAccessException | NoSuchMethodException e) {
                throw new LoadException("加载过滤器失败 " + this.MODULE_PLUGIN_RELATION.get(moduleName) + ":" + moduleName + " " + clazz.getName());
            }
            this.COMPONENT_FILTER_INSTANCE.put(annotation, instance);
            if (annotation.users()) this.FILTER_USERS_CHAIN.add(instance);
            if (annotation.group()) this.FILTER_GROUP_CHAIN.add(instance);
        }


        this.logger.hint("加载监听器 " + this.COMPONENT_MONITOR_CLAZZ.size());

        for (Monitor annotation : this.SORTED_MONITOR) {
            Class<? extends EventHandlerMonitor> clazz = this.COMPONENT_MONITOR_CLAZZ.get(annotation);
            String moduleName = annotation.value();
            String pluginName = this.MODULE_PLUGIN_RELATION.get(moduleName);
            Plugin plugin = this.plugins.get(pluginName);
            URLClassLoader dependClassLoader = plugin.getDependClassLoader();
            this.logger.info("加载监听器" + pluginName + ":" + moduleName + "[" + annotation.priority() + "] -> " + clazz.getName());
            EventHandlerMonitor instance;
            try {
                instance = clazz.getConstructor().newInstance();
                instance.internalInit(pluginName, moduleName, dependClassLoader);
            } catch (InvocationTargetException | InstantiationException | IllegalAccessException | NoSuchMethodException e) {
                throw new LoadException("加载监听器失败 " + this.MODULE_PLUGIN_RELATION.get(moduleName) + ":" + moduleName + " " + clazz.getName());
            }
            this.COMPONENT_MONITOR_INSTANCE.put(annotation, instance);
            if (annotation.users()) this.MONITOR_USERS_CHAIN.add(instance);
            if (annotation.group()) this.MONITOR_GROUP_CHAIN.add(instance);
        }


        this.logger.hint("加载检查器 " + this.COMPONENT_CHECKER_CLAZZ.size());

        for (Checker annotation : this.SORTED_CHECKER) {
            Class<? extends EventHandlerChecker> clazz = this.COMPONENT_CHECKER_CLAZZ.get(annotation);
            String moduleName = annotation.value();
            String pluginName = this.MODULE_PLUGIN_RELATION.get(moduleName);
            Plugin plugin = this.plugins.get(pluginName);
            URLClassLoader dependClassLoader = plugin.getDependClassLoader();
            this.logger.info("加载检查器" + pluginName + ":" + moduleName + "[" + annotation.priority() + "] -> " + clazz.getName());
            EventHandlerChecker instance;
            try {
                instance = clazz.getConstructor().newInstance();
                instance.internalInit(pluginName, moduleName, dependClassLoader);
            } catch (InvocationTargetException | InstantiationException | IllegalAccessException | NoSuchMethodException e) {
                throw new LoadException("加载检查器失败 " + this.MODULE_PLUGIN_RELATION.get(moduleName) + ":" + moduleName + " " + clazz.getName());
            }
            this.COMPONENT_CHECKER_INSTANCE.put(annotation, instance);
            if (annotation.command().equals("*")) {
                if (annotation.users()) this.GLOBAL_CHECKER_USERS_POOL.add(instance);
                if (annotation.group()) this.GLOBAL_CHECKER_GROUP_POOL.add(instance);
            } else {
                if (annotation.users()) {
                    List<EventHandlerChecker> checkerList = this.COMMAND_CHECKER_USERS_POOL.computeIfAbsent(annotation.command(), k1 -> new CopyOnWriteArrayList<>());
                    checkerList.add(instance);
                    checkerList.sort((o1, o2) -> {
                        Checker o1Annotation = o1.getClass().getAnnotation(Checker.class);
                        Checker o2Annotation = o2.getClass().getAnnotation(Checker.class);
                        return o1Annotation.priority() - o2Annotation.priority();
                    });
                }
                if (annotation.group()) {
                    List<EventHandlerChecker> checkerList = this.COMMAND_CHECKER_GROUP_POOL.computeIfAbsent(annotation.command(), k1 -> new CopyOnWriteArrayList<>());
                    checkerList.add(instance);
                    checkerList.sort((o1, o2) -> {
                        Checker o1Annotation = o1.getClass().getAnnotation(Checker.class);
                        Checker o2Annotation = o2.getClass().getAnnotation(Checker.class);
                        return o1Annotation.priority() - o2Annotation.priority();
                    });
                }
            }
        }


        this.logger.hint("加载执行器 " + this.COMPONENT_EXECUTOR_CLAZZ.size());

        for (Map.Entry<Executor, Class<? extends EventHandlerExecutor>> entry : this.COMPONENT_EXECUTOR_CLAZZ.entrySet()) {
            Executor annotation = entry.getKey();
            Class<? extends EventHandlerExecutor> clazz = entry.getValue();
            String moduleName = annotation.value();
            String pluginName = this.MODULE_PLUGIN_RELATION.get(moduleName);
            Plugin plugin = this.plugins.get(pluginName);
            URLClassLoader dependClassLoader = plugin.getDependClassLoader();
            this.logger.info("加载执行器" + pluginName + ":" + moduleName + "[" + annotation.command() + "] -> " + clazz.getName());
            EventHandlerExecutor instance;
            try {
                instance = clazz.getConstructor().newInstance();
                instance.internalInit(pluginName, moduleName, dependClassLoader);
                instance.buildHelp(annotation);
            } catch (InvocationTargetException | InstantiationException | IllegalAccessException | NoSuchMethodException e) {
                throw new LoadException("加载执行器失败 " + this.MODULE_PLUGIN_RELATION.get(moduleName) + ":" + moduleName + " " + clazz.getName());
            }
            this.COMPONENT_EXECUTOR_INSTANCE.put(annotation, instance);
            if (annotation.users()) this.EXECUTOR_USERS_POOL.put(annotation.command(), instance);
            if (annotation.group()) this.EXECUTOR_GROUP_POOL.put(annotation.command(), instance);
        }

    }


    // =================================================================================================================


    public void init() {

        this.logger.hint("预载定时器");

        for (Runner annotation : this.SORTED_RUNNER) {
            EventHandlerRunner instance = this.COMPONENT_RUNNER_INSTANCE.get(annotation);
            this.logger.info("预载定时器" + annotation.value() + "[" + annotation.priority() + "] -> " + instance.getClass().getName());
            try {
                instance.initWrapper();
            } catch (Exception exception) {
                throw new BootException("预载定时器失败 " + this.MODULE_PLUGIN_RELATION.get(annotation.value()) + ":" + annotation.value() + " -> " + instance.getClass().getName(), exception);
            }
        }


        this.logger.hint("预载过滤器");

        for (Filter annotation : this.SORTED_FILTER) {
            EventHandlerFilter instance = this.COMPONENT_FILTER_INSTANCE.get(annotation);
            this.logger.info("预载过滤器" + annotation.value() + "[" + annotation.priority() + "] -> " + instance.getClass().getName());
            try {
                instance.initWrapper();
            } catch (Exception exception) {
                throw new BootException("预载过滤器失败 " + this.MODULE_PLUGIN_RELATION.get(annotation.value()) + ":" + annotation.value() + " -> " + instance.getClass().getName(), exception);
            }
        }


        this.logger.hint("预载监听器");

        for (Monitor annotation : this.SORTED_MONITOR) {
            EventHandlerMonitor instance = this.COMPONENT_MONITOR_INSTANCE.get(annotation);
            this.logger.info("预载监听器" + annotation.value() + "[" + annotation.priority() + "] -> " + instance.getClass().getName());
            try {
                instance.initWrapper();
            } catch (Exception exception) {
                throw new BootException("预载监听器失败 " + this.MODULE_PLUGIN_RELATION.get(annotation.value()) + ":" + annotation.value() + " -> " + instance.getClass().getName(), exception);
            }
        }


        this.logger.hint("预载过滤器");

        for (Checker annotation : this.SORTED_CHECKER) {
            EventHandlerChecker instance = this.COMPONENT_CHECKER_INSTANCE.get(annotation);
            this.logger.info("预载过滤器" + annotation.value() + "[" + annotation.priority() + "] -> " + instance.getClass().getName());
            try {
                instance.initWrapper();
            } catch (Exception exception) {
                throw new BootException("预载过滤器失败 " + this.MODULE_PLUGIN_RELATION.get(annotation.value()) + ":" + annotation.value() + " -> " + instance.getClass().getName(), exception);
            }
        }


        this.logger.hint("预载执行器");

        for (Map.Entry<Executor, EventHandlerExecutor> entry : this.COMPONENT_EXECUTOR_INSTANCE.entrySet()) {
            Executor annotation = entry.getKey();
            EventHandlerExecutor instance = entry.getValue();
            this.logger.info("预载执行器" + annotation.value() + "[" + annotation.command() + "] -> " + instance.getClass().getName());
            try {
                instance.initWrapper();
            } catch (Exception exception) {
                throw new BootException("预载执行器失败 " + this.MODULE_PLUGIN_RELATION.get(annotation.value()) + ":" + annotation.value() + " -> " + instance.getClass().getName(), exception);
            }
        }
    }


    // =================================================================================================================


    public void boot() {


        this.logger.hint("启动定时器");

        for (Runner annotation : this.SORTED_RUNNER) {
            EventHandlerRunner clazz = this.COMPONENT_RUNNER_INSTANCE.get(annotation);
            this.logger.info("启动定时器" + annotation.value() + "[" + annotation.priority() + "] -> " + clazz.getClass().getName());
            try {
                clazz.bootWrapper();
            } catch (Exception exception) {
                throw new BootException("启动定时器失败 " + this.MODULE_PLUGIN_RELATION.get(annotation.value()) + ":" + annotation.value() + " -> " + clazz.getClass().getName(), exception);
            }
        }


        this.logger.hint("启动过滤器");

        for (Filter annotation : this.SORTED_FILTER) {
            EventHandlerFilter clazz = this.COMPONENT_FILTER_INSTANCE.get(annotation);
            this.logger.info("启动过滤器" + annotation.value() + "[" + annotation.priority() + "] -> " + clazz.getClass().getName());
            try {
                clazz.bootWrapper();
            } catch (Exception exception) {
                throw new BootException("启动过滤器失败 " + this.MODULE_PLUGIN_RELATION.get(annotation.value()) + ":" + annotation.value() + " -> " + clazz.getClass().getName(), exception);
            }
        }


        this.logger.hint("启动监听器");

        for (Monitor annotation : this.SORTED_MONITOR) {
            EventHandlerMonitor clazz = this.COMPONENT_MONITOR_INSTANCE.get(annotation);
            this.logger.info("启动监听器" + annotation.value() + "[" + annotation.priority() + "] -> " + clazz.getClass().getName());
            try {
                clazz.bootWrapper();
            } catch (Exception exception) {
                throw new BootException("启动监听器失败 " + this.MODULE_PLUGIN_RELATION.get(annotation.value()) + ":" + annotation.value() + " -> " + clazz.getClass().getName(), exception);
            }
        }


        this.logger.hint("启动检查器");

        for (Checker annotation : this.SORTED_CHECKER) {
            EventHandlerChecker clazz = this.COMPONENT_CHECKER_INSTANCE.get(annotation);
            this.logger.info("启动检查器" + annotation.value() + "[" + annotation.priority() + "] -> " + clazz.getClass().getName());
            try {
                clazz.bootWrapper();
            } catch (Exception exception) {
                throw new BootException("启动检查器失败 " + this.MODULE_PLUGIN_RELATION.get(annotation.value()) + ":" + annotation.value() + " -> " + clazz.getClass().getName(), exception);
            }
        }

        this.logger.hint("启动执行器");

        for (Map.Entry<Executor, EventHandlerExecutor> entry : this.COMPONENT_EXECUTOR_INSTANCE.entrySet()) {
            Executor annotation = entry.getKey();
            EventHandlerExecutor clazz = entry.getValue();
            this.logger.info("启动执行器" + annotation.value() + "[" + annotation.command() + "] -> " + clazz.getClass().getName());
            try {
                clazz.bootWrapper();
            } catch (Exception exception) {
                throw new BootException("启动执行器失败 " + this.MODULE_PLUGIN_RELATION.get(annotation.value()) + ":" + annotation.value() + " -> " + clazz.getClass().getName(), exception);
            }
        }
    }


    // =================================================================================================================


    public void shut() {


        this.logger.hint("关闭执行器");

        for (Map.Entry<Executor, EventHandlerExecutor> entry : this.COMPONENT_EXECUTOR_INSTANCE.entrySet()) {
            Executor annotation = entry.getKey();
            EventHandlerExecutor instance = entry.getValue();
            try {
                if (FurryBlack.isShutModeDrop()) {
                    this.logger.info("丢弃执行器" + annotation.value() + "[" + annotation.command() + "] -> " + instance.getClass().getName());
                    Thread thread = new Thread(instance::shutWrapper);
                    thread.setDaemon(true);
                    thread.start();
                } else {
                    this.logger.info("关闭执行器" + annotation.value() + "[" + annotation.command() + "] -> " + instance.getClass().getName());
                    instance.shutWrapper();
                }
            } catch (Exception exception) {
                this.logger.warning("关闭执行器异常" + annotation.value() + "[" + annotation.command() + "] -> " + instance.getClass().getName(), exception);
            }
        }


        this.logger.hint("关闭检查器");

        List<Checker> checkers = new ArrayList<>(this.SORTED_CHECKER);
        Collections.reverse(checkers);
        for (Checker annotation : checkers) {
            EventHandlerChecker instance = this.COMPONENT_CHECKER_INSTANCE.get(annotation);
            try {
                if (FurryBlack.isShutModeDrop()) {
                    this.logger.info("丢弃检查器" + annotation.value() + "[" + annotation.command() + "/" + annotation.priority() + "] -> " + instance.getClass().getName());
                    Thread thread = new Thread(instance::shutWrapper);
                    thread.setDaemon(true);
                    thread.start();
                } else {
                    this.logger.info("关闭检查器" + annotation.value() + "[" + annotation.command() + "/" + annotation.priority() + "] -> " + instance.getClass().getName());
                    instance.shutWrapper();
                }
            } catch (Exception exception) {
                this.logger.warning("关闭检查器异常" + annotation.value() + "[" + annotation.command() + "/" + annotation.priority() + "] -> " + instance.getClass().getName(), exception);
            }
        }


        this.logger.hint("关闭监听器");

        List<Monitor> monitors = new ArrayList<>(this.SORTED_MONITOR);
        Collections.reverse(monitors);
        for (Monitor annotation : monitors) {
            EventHandlerMonitor instance = this.COMPONENT_MONITOR_INSTANCE.get(annotation);
            try {
                if (FurryBlack.isShutModeDrop()) {
                    this.logger.info("丢弃检查器" + annotation.value() + "[" + annotation.priority() + "] -> " + instance.getClass().getName());
                    Thread thread = new Thread(instance::shutWrapper);
                    thread.setDaemon(true);
                    thread.start();
                } else {
                    this.logger.info("关闭检查器" + annotation.value() + "[" + annotation.priority() + "] -> " + instance.getClass().getName());
                    instance.shutWrapper();
                }
            } catch (Exception exception) {
                this.logger.warning("关闭检查器异常" + annotation.value() + "[" + annotation.priority() + "] -> " + instance.getClass().getName(), exception);
            }
        }


        this.logger.hint("关闭过滤器");


        List<Filter> filters = new ArrayList<>(this.SORTED_FILTER);
        Collections.reverse(filters);
        for (Filter annotation : filters) {
            EventHandlerFilter instance = this.COMPONENT_FILTER_INSTANCE.get(annotation);
            try {
                if (FurryBlack.isShutModeDrop()) {
                    this.logger.info("丢弃过滤器" + annotation.value() + "[" + annotation.priority() + "] -> " + instance.getClass().getName());
                    Thread thread = new Thread(instance::shutWrapper);
                    thread.setDaemon(true);
                    thread.start();
                } else {
                    this.logger.info("关闭过滤器" + annotation.value() + "[" + annotation.priority() + "] -> " + instance.getClass().getName());
                    instance.shutWrapper();
                }
            } catch (Exception exception) {
                this.logger.warning("关闭过滤器异常" + annotation.value() + "[" + annotation.priority() + "] -> " + instance.getClass().getName(), exception);
            }
        }


        this.logger.hint("关闭定时器");


        List<Runner> runners = new ArrayList<>(this.SORTED_RUNNER);
        Collections.reverse(runners);
        for (Runner annotation : runners) {
            EventHandlerRunner instance = this.COMPONENT_RUNNER_INSTANCE.get(annotation);
            try {
                if (FurryBlack.isShutModeDrop()) {
                    this.logger.info("丢弃定时器" + annotation.value() + "[" + annotation.priority() + "] -> " + instance.getClass().getName());
                    Thread thread = new Thread(instance::shutWrapper);
                    thread.setDaemon(true);
                    thread.start();
                } else {
                    this.logger.info("关闭定时器" + annotation.value() + "[" + annotation.priority() + "] -> " + instance.getClass().getName());
                    instance.shutWrapper();
                }
            } catch (Exception exception) {
                this.logger.warning("关闭定时器异常" + annotation.value() + "[" + annotation.priority() + "] -> " + instance.getClass().getName(), exception);
            }
        }


    }


    // =================================================================================================================
    //
    // 内部方法
    //
    // =================================================================================================================


    private static int compare(Runner o1, Runner o2) {
        return o1.priority() - o2.priority();
    }


    private static int compare(Filter o1, Filter o2) {
        return o1.priority() - o2.priority();
    }


    private static int compare(Monitor o1, Monitor o2) {
        return o1.priority() - o2.priority();
    }


    private static int compare(Checker o1, Checker o2) {
        return o1.priority() - o2.priority();
    }


    private static int compare(Executor o1, Executor o2) {
        return CharSequence.compare(o1.command(), o2.command());
    }


    private static String printAnnotation(Runner annotation) {
        return annotation.value() + '[' + annotation.priority() + ']';
    }

    private static String printAnnotation(Filter annotation) {
        return annotation.value() + '[' + annotation.priority() + "]{" + (annotation.users() ? "U" : "") + (annotation.group() ? "G" : "") + "}";
    }

    private static String printAnnotation(Monitor annotation) {
        return annotation.value() + '[' + annotation.priority() + "]{" + (annotation.users() ? "U" : "") + (annotation.group() ? "G" : "") + "}";
    }

    private static String printAnnotation(Checker annotation) {
        return annotation.value() + '[' + annotation.priority() + ']' + '(' + annotation.command() + "){" + (annotation.users() ? "U" : "") + (annotation.group() ? "G" : "") + "}";
    }

    private static String printAnnotation(Executor annotation) {
        return annotation.value() + '(' + annotation.command() + "){" + (annotation.users() ? "U" : "") + (annotation.group() ? "G" : "") + "}";
    }


    // =================================================================================================================
    //
    //
    //
    // =================================================================================================================

    public List<EventHandlerFilter> getFilterUsersChain() {
        return this.FILTER_USERS_CHAIN;
    }

    public List<EventHandlerFilter> getFilterGroupChain() {
        return this.FILTER_GROUP_CHAIN;
    }

    public List<EventHandlerMonitor> getMonitorUsersChain() {
        return this.MONITOR_USERS_CHAIN;
    }

    public List<EventHandlerMonitor> getMonitorGroupChain() {
        return this.MONITOR_GROUP_CHAIN;
    }

    public Map<String, EventHandlerExecutor> getExecutorUsersPool() {
        return this.EXECUTOR_USERS_POOL;
    }

    public Map<String, EventHandlerExecutor> getExecutorGroupPool() {
        return this.EXECUTOR_GROUP_POOL;
    }

    public List<EventHandlerChecker> getGlobalCheckerUsersPool() {
        return this.GLOBAL_CHECKER_USERS_POOL;
    }

    public List<EventHandlerChecker> getGlobalCheckerGroupPool() {
        return this.GLOBAL_CHECKER_GROUP_POOL;
    }

    public List<EventHandlerChecker> getCommandCheckerUsersPool(String name) {
        return this.COMMAND_CHECKER_USERS_POOL.get(name);
    }

    public List<EventHandlerChecker> getCommandCheckerGroupPool(String name) {
        return this.COMMAND_CHECKER_GROUP_POOL.get(name);
    }


    // =================================================================================================================
    //
    //
    //
    // =================================================================================================================


    public void verboseStatus() {

        System.out.println(Color.BRIGHT_MAGENTA + ">> PLUGINS" + Color.RESET);

        for (Map.Entry<String, Plugin> entry : this.plugins.entrySet()) {
            var k = entry.getKey();
            var v = entry.getValue();
            System.out.println(Color.BRIGHT_CYAN + k + ":" + hexHash(v) + " " + v.getFile() + Color.RESET);
            for (Map.Entry<String, Class<? extends AbstractEventHandler>> classEntry : v.getModules().entrySet()) {
                var classK = classEntry.getKey();
                var classV = classEntry.getValue();
                System.out.println(classK + " -> " + classV.getName() + ":" + hexHash(classV));
            }
        }

        System.out.println(Color.BRIGHT_MAGENTA + ">> MODULES" + Color.RESET);

        for (Map.Entry<String, Class<? extends AbstractEventHandler>> entry : this.modules.entrySet()) {
            var k = entry.getKey();
            var v = entry.getValue();
            System.out.println(k + " -> " + v.getName() + ":" + hexHash(v));
        }

        System.out.println(Color.BRIGHT_MAGENTA + ">> MODULE_PLUGIN_RELATION" + Color.RESET);

        for (Map.Entry<String, String> entry : this.MODULE_PLUGIN_RELATION.entrySet()) {
            var k = entry.getKey();
            var v = entry.getValue();
            System.out.println(k + " -> " + v);
        }

        System.out.println(Color.BRIGHT_CYAN + ">> COMPONENT_RUNNER_CLAZZ" + Color.RESET);

        for (Map.Entry<Runner, Class<? extends EventHandlerRunner>> entry : this.COMPONENT_RUNNER_CLAZZ.entrySet()) {
            var k = entry.getKey();
            var v = entry.getValue();
            System.out.println(printAnnotation(k) + ":" + hexHash(k) + " -> " + v.getName() + ":" + hexHash(v));
        }

        System.out.println(Color.BRIGHT_CYAN + ">> COMPONENT_FILTER_CLAZZ" + Color.RESET);

        for (Map.Entry<Filter, Class<? extends EventHandlerFilter>> entry : this.COMPONENT_FILTER_CLAZZ.entrySet()) {
            var k = entry.getKey();
            var v = entry.getValue();
            System.out.println(printAnnotation(k) + ":" + hexHash(k) + " -> " + v.getName() + ":" + hexHash(v));
        }

        System.out.println(Color.BRIGHT_CYAN + ">> COMPONENT_MONITOR_CLAZZ" + Color.RESET);

        for (Map.Entry<Monitor, Class<? extends EventHandlerMonitor>> entry : this.COMPONENT_MONITOR_CLAZZ.entrySet()) {
            var k = entry.getKey();
            var v = entry.getValue();
            System.out.println(printAnnotation(k) + ":" + hexHash(k) + " -> " + v.getName() + ":" + hexHash(v));
        }

        System.out.println(Color.BRIGHT_CYAN + ">> COMPONENT_CHECKER_CLAZZ" + Color.RESET);

        for (Map.Entry<Checker, Class<? extends EventHandlerChecker>> entry : this.COMPONENT_CHECKER_CLAZZ.entrySet()) {
            var k = entry.getKey();
            var v = entry.getValue();
            System.out.println(printAnnotation(k) + ":" + hexHash(k) + " -> " + v.getName() + ":" + hexHash(v));
        }

        System.out.println(Color.BRIGHT_CYAN + ">> COMPONENT_EXECUTOR_CLAZZ" + Color.RESET);

        for (Map.Entry<Executor, Class<? extends EventHandlerExecutor>> entry : this.COMPONENT_EXECUTOR_CLAZZ.entrySet()) {
            var k = entry.getKey();
            var v = entry.getValue();
            System.out.println(printAnnotation(k) + ":" + hexHash(k) + " -> " + v.getName() + ":" + hexHash(v));
        }

        System.out.println(Color.BRIGHT_CYAN + ">> SORTED_RUNNER" + Color.RESET);

        for (Runner entry : this.SORTED_RUNNER) {
            System.out.println(printAnnotation(entry) + ":" + hexHash(entry));
        }

        System.out.println(Color.BRIGHT_CYAN + ">> SORTED_FILTER" + Color.RESET);

        for (Filter entry : this.SORTED_FILTER) {
            System.out.println(printAnnotation(entry) + ":" + hexHash(entry));
        }

        System.out.println(Color.BRIGHT_CYAN + ">> SORTED_MONITOR" + Color.RESET);

        for (Monitor entry : this.SORTED_MONITOR) {
            System.out.println(printAnnotation(entry) + ":" + hexHash(entry));
        }

        System.out.println(Color.BRIGHT_CYAN + ">> SORTED_CHECKER" + Color.RESET);

        for (Checker entry : this.SORTED_CHECKER) {
            System.out.println(printAnnotation(entry) + ":" + hexHash(entry));
        }

        System.out.println(Color.BRIGHT_CYAN + ">> COMPONENT_RUNNER_INSTANCE" + Color.RESET);

        for (Map.Entry<Runner, EventHandlerRunner> entry : this.COMPONENT_RUNNER_INSTANCE.entrySet()) {
            var k = entry.getKey();
            var v = entry.getValue();
            System.out.println(printAnnotation(k) + ":" + hexHash(k) + " -> " + v.getClass().getName() + ":" + hexHash(v));
        }

        System.out.println(Color.BRIGHT_CYAN + ">> COMPONENT_FILTER_INSTANCE" + Color.RESET);

        for (Map.Entry<Filter, EventHandlerFilter> entry : this.COMPONENT_FILTER_INSTANCE.entrySet()) {
            var k = entry.getKey();
            var v = entry.getValue();
            System.out.println(printAnnotation(k) + ":" + hexHash(k) + " -> " + v.getClass().getName() + ":" + hexHash(v));
        }

        System.out.println(Color.BRIGHT_CYAN + ">> COMPONENT_MONITOR_INSTANCE" + Color.RESET);

        for (Map.Entry<Monitor, EventHandlerMonitor> entry : this.COMPONENT_MONITOR_INSTANCE.entrySet()) {
            var k = entry.getKey();
            var v = entry.getValue();
            System.out.println(printAnnotation(k) + ":" + hexHash(k) + " -> " + v.getClass().getName() + ":" + hexHash(v));
        }

        System.out.println(Color.BRIGHT_CYAN + ">> COMPONENT_CHECKER_INSTANCE" + Color.RESET);

        for (Map.Entry<Checker, EventHandlerChecker> entry : this.COMPONENT_CHECKER_INSTANCE.entrySet()) {
            var k = entry.getKey();
            var v = entry.getValue();
            System.out.println(printAnnotation(k) + ":" + hexHash(k) + " -> " + v.getClass().getName() + ":" + hexHash(v));
        }

        System.out.println(Color.BRIGHT_CYAN + ">> COMPONENT_EXECUTOR_INSTANCE" + Color.RESET);

        for (Map.Entry<Executor, EventHandlerExecutor> entry : this.COMPONENT_EXECUTOR_INSTANCE.entrySet()) {
            var k = entry.getKey();
            var v = entry.getValue();
            System.out.println(printAnnotation(k) + ":" + hexHash(k) + " -> " + v.getClass().getName() + ":" + hexHash(v));
        }


        System.out.println(Color.BRIGHT_CYAN + ">> FILTER_USERS_CHAIN" + Color.RESET);

        for (EventHandlerFilter item : this.FILTER_USERS_CHAIN) {
            System.out.println(item.getClass().getName() + ":" + hexHash(item));
        }

        System.out.println(Color.BRIGHT_CYAN + ">> FILTER_GROUP_CHAIN" + Color.RESET);

        for (EventHandlerFilter item : this.FILTER_GROUP_CHAIN) {
            System.out.println(item.getClass().getName() + ":" + hexHash(item));
        }

        System.out.println(Color.BRIGHT_CYAN + ">> MONITOR_USERS_CHAIN" + Color.RESET);

        for (EventHandlerMonitor item : this.MONITOR_USERS_CHAIN) {
            System.out.println(item.getClass().getName() + ":" + hexHash(item));
        }

        System.out.println(Color.BRIGHT_CYAN + ">> MONITOR_GROUP_CHAIN" + Color.RESET);

        for (EventHandlerMonitor item : this.MONITOR_GROUP_CHAIN) {
            System.out.println(item.getClass().getName() + ":" + hexHash(item));
        }

        System.out.println(Color.BRIGHT_CYAN + ">> GLOBAL_CHECKER_USERS_POOL" + Color.RESET);

        for (EventHandlerChecker item : this.GLOBAL_CHECKER_USERS_POOL) {
            System.out.println(item.getClass().getName() + ":" + hexHash(item));
        }

        System.out.println(Color.BRIGHT_CYAN + ">> GLOBAL_CHECKER_GROUP_POOL" + Color.RESET);

        for (EventHandlerChecker item : this.GLOBAL_CHECKER_GROUP_POOL) {
            System.out.println(item.getClass().getName() + ":" + hexHash(item));
        }

        System.out.println(Color.BRIGHT_CYAN + ">> COMMAND_CHECKER_USERS_POOL" + Color.RESET);

        for (Map.Entry<String, List<EventHandlerChecker>> entry : this.COMMAND_CHECKER_USERS_POOL.entrySet()) {
            var k = entry.getKey();
            var v = entry.getValue();
            System.out.println(Color.CYAN + k + Color.RESET + " " + v.size());
            for (EventHandlerChecker checker : v) {
                System.out.println(checker.getClass().getName() + ":" + hexHash(checker));
            }
        }

        System.out.println(Color.BRIGHT_CYAN + ">> COMMAND_CHECKER_GROUP_POOL" + Color.RESET);

        for (Map.Entry<String, List<EventHandlerChecker>> entry : this.COMMAND_CHECKER_GROUP_POOL.entrySet()) {
            var k = entry.getKey();
            var v = entry.getValue();
            System.out.println(Color.CYAN + k + Color.RESET + " " + v.size());
            for (EventHandlerChecker checker : v) {
                System.out.println(checker.getClass().getName() + ":" + hexHash(checker));
            }
        }

        System.out.println(Color.BRIGHT_CYAN + ">> EXECUTOR_USERS_POOL" + Color.RESET);

        for (Map.Entry<String, EventHandlerExecutor> entry : this.EXECUTOR_USERS_POOL.entrySet()) {
            var k = entry.getKey();
            var v = entry.getValue();
            System.out.println(k + " -> " + v.getClass().getName() + ":" + hexHash(v));
        }

        System.out.println(Color.BRIGHT_CYAN + ">> EXECUTOR_GROUP_POOL" + Color.RESET);

        for (Map.Entry<String, EventHandlerExecutor> entry : this.EXECUTOR_GROUP_POOL.entrySet()) {
            var k = entry.getKey();
            var v = entry.getValue();
            System.out.println(k + " -> " + v.getClass().getName() + ":" + hexHash(v));
        }

        System.out.println(Color.BRIGHT_CYAN + ">> COMMAND_EXECUTOR_RELATION" + Color.RESET);

        for (Map.Entry<String, Executor> entry : this.COMMAND_EXECUTOR_RELATION.entrySet()) {
            var k = entry.getKey();
            var v = entry.getValue();
            System.out.println(Color.CYAN + k + Color.RESET + " -> " + v.value() + ":" + hexHash(v) + " {" + (v.users() ? "U" : "") + (v.group() ? "G" : "") + "} " + v.outline() + ":" + v.description());
            for (String temp : v.usage()) {
                System.out.println(temp);
            }
            for (String temp : v.privacy()) {
                System.out.println(temp);
            }
        }

    }


}
