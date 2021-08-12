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

import studio.blacktech.furryblackplus.Driver;
import studio.blacktech.furryblackplus.common.Api;
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
import studio.blacktech.furryblackplus.core.exception.BotException;
import studio.blacktech.furryblackplus.core.exception.moduels.boot.BootException;
import studio.blacktech.furryblackplus.core.exception.moduels.load.LoadException;
import studio.blacktech.furryblackplus.core.exception.moduels.scan.ScanException;
import studio.blacktech.furryblackplus.core.utilties.logger.LoggerX;
import studio.blacktech.furryblackplus.core.utilties.logger.LoggerX.Color;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

import static studio.blacktech.furryblackplus.core.utilties.logger.LoggerX.hash;


@Api("插件与模块持有")
public final class Schema {

    private final LoggerX logger = new LoggerX(Schema.class);

    private final File folder;

    private final ConcurrentHashMap<String, Plugin> PLUGINS;
    private final ConcurrentHashMap<String, Class<? extends AbstractEventHandler>> MODULES;

    private final ConcurrentSkipListMap<Runner, Class<? extends EventHandlerRunner>> COMPONENT_RUNNER_CLAZZ;
    private final ConcurrentSkipListMap<Filter, Class<? extends EventHandlerFilter>> COMPONENT_FILTER_CLAZZ;
    private final ConcurrentSkipListMap<Monitor, Class<? extends EventHandlerMonitor>> COMPONENT_MONITOR_CLAZZ;
    private final ConcurrentSkipListMap<Checker, Class<? extends EventHandlerChecker>> COMPONENT_CHECKER_CLAZZ;
    private final ConcurrentSkipListMap<Executor, Class<? extends EventHandlerExecutor>> COMPONENT_EXECUTOR_CLAZZ;

    private final ConcurrentSkipListMap<Runner, EventHandlerRunner> COMPONENT_RUNNER_INSTANCE;
    private final ConcurrentSkipListMap<Filter, EventHandlerFilter> COMPONENT_FILTER_INSTANCE;
    private final ConcurrentSkipListMap<Monitor, EventHandlerMonitor> COMPONENT_MONITOR_INSTANCE;
    private final ConcurrentSkipListMap<Checker, EventHandlerChecker> COMPONENT_CHECKER_INSTANCE;
    private final ConcurrentSkipListMap<Executor, EventHandlerExecutor> COMPONENT_EXECUTOR_INSTANCE;

    private final ConcurrentHashMap<String, Executor> COMMAND_EXECUTOR_RELATION;
    private final ConcurrentHashMap<String, String> MODULE_PLUGIN_RELATION;

    private final CopyOnWriteArrayList<EventHandlerFilter> FILTER_USERS_CHAIN;
    private final CopyOnWriteArrayList<EventHandlerFilter> FILTER_GROUP_CHAIN;

    private final CopyOnWriteArrayList<EventHandlerMonitor> MONITOR_USERS_CHAIN;
    private final CopyOnWriteArrayList<EventHandlerMonitor> MONITOR_GROUP_CHAIN;

    private final Map<String, EventHandlerExecutor> EXECUTOR_USERS_POOL;
    private final Map<String, EventHandlerExecutor> EXECUTOR_GROUP_POOL;

    private final CopyOnWriteArrayList<EventHandlerChecker> GLOBAL_CHECKER_USERS_POOL;
    private final CopyOnWriteArrayList<EventHandlerChecker> GLOBAL_CHECKER_GROUP_POOL;

    private final Map<String, CopyOnWriteArrayList<EventHandlerChecker>> COMMAND_CHECKER_USERS_POOL;
    private final Map<String, CopyOnWriteArrayList<EventHandlerChecker>> COMMAND_CHECKER_GROUP_POOL;

    public Schema(File folder) {

        this.folder = folder;

        this.PLUGINS = new ConcurrentHashMap<>(); // 1
        this.MODULES = new ConcurrentHashMap<>(); // 2

        this.COMPONENT_RUNNER_CLAZZ = new ConcurrentSkipListMap<>(Schema::compare); // 3
        this.COMPONENT_FILTER_CLAZZ = new ConcurrentSkipListMap<>(Schema::compare); // 4
        this.COMPONENT_MONITOR_CLAZZ = new ConcurrentSkipListMap<>(Schema::compare); // 5
        this.COMPONENT_CHECKER_CLAZZ = new ConcurrentSkipListMap<>(Schema::compare); // 6
        this.COMPONENT_EXECUTOR_CLAZZ = new ConcurrentSkipListMap<>(Schema::compare); // 7

        this.COMPONENT_RUNNER_INSTANCE = new ConcurrentSkipListMap<>(Schema::compare); // 8
        this.COMPONENT_FILTER_INSTANCE = new ConcurrentSkipListMap<>(Schema::compare); // 9
        this.COMPONENT_MONITOR_INSTANCE = new ConcurrentSkipListMap<>(Schema::compare); // 10
        this.COMPONENT_CHECKER_INSTANCE = new ConcurrentSkipListMap<>(Schema::compare); // 11
        this.COMPONENT_EXECUTOR_INSTANCE = new ConcurrentSkipListMap<>(Schema::compare); // 12

        this.COMMAND_EXECUTOR_RELATION = new ConcurrentHashMap<>(); // 13
        this.MODULE_PLUGIN_RELATION = new ConcurrentHashMap<>(); // 14

        this.FILTER_USERS_CHAIN = new CopyOnWriteArrayList<>(); // 15
        this.FILTER_GROUP_CHAIN = new CopyOnWriteArrayList<>(); // 16

        this.MONITOR_USERS_CHAIN = new CopyOnWriteArrayList<>(); // 17
        this.MONITOR_GROUP_CHAIN = new CopyOnWriteArrayList<>(); // 18

        this.EXECUTOR_USERS_POOL = new ConcurrentHashMap<>(); // 19
        this.EXECUTOR_GROUP_POOL = new ConcurrentHashMap<>(); // 20

        this.GLOBAL_CHECKER_USERS_POOL = new CopyOnWriteArrayList<>(); // 21
        this.GLOBAL_CHECKER_GROUP_POOL = new CopyOnWriteArrayList<>(); // 22

        this.COMMAND_CHECKER_USERS_POOL = new ConcurrentHashMap<>(); // 23
        this.COMMAND_CHECKER_GROUP_POOL = new ConcurrentHashMap<>(); // 24

    }


    // =================================================================================================================
    //
    // 提供接口
    //
    // =================================================================================================================


    // =================================================================================================================
    // 组装消息


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
    //
    // 插件相关
    //
    // =================================================================================================================


    // =================================================================================================================
    // 列出插件


    public Set<Map.Entry<String, Plugin>> getAllPlugin() {
        return this.PLUGINS.entrySet();
    }

    public Set<String> listAllPluginName() {
        return this.PLUGINS.keySet();
    }

    // =================================================================================================================
    // 导入插件


    public void importPlugin(String fileName) {

        File pluginPackage = Paths.get(Driver.getPluginFolder(), fileName).toFile();

        if (!pluginPackage.exists()) {
            throw new ScanException("指定的文件不存在 " + pluginPackage.getAbsolutePath());
        }

        if (pluginPackage.isDirectory()) {
            throw new ScanException("指定的文件是目录 " + pluginPackage.getAbsolutePath());
        }

        if (!pluginPackage.canRead()) {
            throw new ScanException("指定的文件不可读 " + pluginPackage.getAbsolutePath());
        }

        Plugin plugin = new Plugin(pluginPackage);
        Plugin exist = this.PLUGINS.get(plugin.getName());

        if (exist != null) {
            throw new ScanException("同名插件已存在 " + pluginPackage.getAbsolutePath() + " -> " + exist.getFile().getAbsolutePath());
        }

        plugin.scan();

        this.load(plugin);
        this.make(plugin);
        this.init(plugin);
        this.boot(plugin);

        this.PLUGINS.put(plugin.getName(), plugin);
    }


    // =================================================================================================================
    // 重载插件


    public void reloadPlugin(String name) {

        Plugin plugin = this.PLUGINS.get(name);

        if (plugin == null) {
            throw new BotException("没有此模块 -> " + name);
        }

        this.unloadPlugin(name);

        Plugin newPlugin = new Plugin(plugin.getFile());

        newPlugin.scan();

        this.load(newPlugin);
        this.make(newPlugin);
        this.init(newPlugin);
        this.boot(newPlugin);

        this.PLUGINS.put(name, newPlugin);
    }


    // =================================================================================================================
    // 卸载插件


    public void unloadPlugin(String name) {

        Plugin plugin = this.PLUGINS.remove(name);

        if (plugin == null) {
            System.out.println("没有这个插件 " + name);
            return;
        }

        Set<Executor> pendingExecutors = new HashSet<>(plugin.getExecutorClassMap().keySet());
        List<Executor> executors = new ArrayList<>(this.COMPONENT_EXECUTOR_CLAZZ.descendingKeySet());
        for (Executor annotation : executors) {
            if (!pendingExecutors.remove(annotation)) {
                continue;
            }
            this.unloadExecutorInstance(annotation);
            this.MODULES.remove(annotation.value());
            this.COMPONENT_EXECUTOR_CLAZZ.remove(annotation);
            this.MODULE_PLUGIN_RELATION.remove(annotation.value());
            this.COMMAND_EXECUTOR_RELATION.remove(annotation.command());
        }

        Set<Checker> pendingCheckers = new HashSet<>(plugin.getCheckerClassMap().keySet());
        List<Checker> checkers = new ArrayList<>(this.COMPONENT_CHECKER_CLAZZ.descendingKeySet());
        for (Checker annotation : checkers) {
            if (!pendingCheckers.remove(annotation)) {
                continue;
            }
            this.unloadCheckerInstance(annotation);
            this.MODULES.remove(annotation.value());
            this.COMPONENT_CHECKER_CLAZZ.remove(annotation);
            this.MODULE_PLUGIN_RELATION.remove(annotation.value());
        }

        Set<Monitor> pendingMonitors = new HashSet<>(plugin.getMonitorClassMap().keySet());
        List<Monitor> monitors = new ArrayList<>(this.COMPONENT_MONITOR_CLAZZ.descendingKeySet());
        for (Monitor annotation : monitors) {
            if (!pendingMonitors.remove(annotation)) {
                continue;
            }
            this.unloadMonitorInstance(annotation);
            this.MODULES.remove(annotation.value());
            this.COMPONENT_MONITOR_CLAZZ.remove(annotation);
            this.MODULE_PLUGIN_RELATION.remove(annotation.value());
        }

        Set<Filter> pendingFilters = new HashSet<>(plugin.getFilterClassMap().keySet());
        List<Filter> filters = new ArrayList<>(this.COMPONENT_FILTER_CLAZZ.descendingKeySet());
        for (Filter annotation : filters) {
            if (!pendingFilters.remove(annotation)) {
                continue;
            }
            this.unloadFilterInstance(annotation);
            this.MODULES.remove(annotation.value());
            this.COMPONENT_FILTER_CLAZZ.remove(annotation);
            this.MODULE_PLUGIN_RELATION.remove(annotation.value());
        }

        Set<Runner> pendingRunners = new HashSet<>(plugin.getRunnerClassMap().keySet());
        List<Runner> runners = new ArrayList<>(this.COMPONENT_RUNNER_CLAZZ.descendingKeySet());
        for (Runner annotation : runners) {
            if (!pendingRunners.remove(annotation)) {
                continue;
            }
            this.unloadRunnerInstance(annotation);
            this.MODULES.remove(annotation.value());
            this.COMPONENT_RUNNER_CLAZZ.remove(annotation);
            this.MODULE_PLUGIN_RELATION.remove(annotation.value());
        }
    }


    // =================================================================================================================
    //
    // 模块相关
    //
    // =================================================================================================================


    // =================================================================================================================
    // 列出模块


    public Map<String, Boolean> listAllModule() {

        Map<String, Boolean> result = new LinkedHashMap<>();

        for (Runner annotation : this.COMPONENT_RUNNER_CLAZZ.keySet()) {
            result.put(annotation.value(), this.COMPONENT_RUNNER_INSTANCE.containsKey(annotation));
        }

        for (Filter annotation : this.COMPONENT_FILTER_CLAZZ.keySet()) {
            result.put(annotation.value(), this.COMPONENT_FILTER_CLAZZ.containsKey(annotation));
        }

        for (Monitor annotation : this.COMPONENT_MONITOR_CLAZZ.keySet()) {
            result.put(annotation.value(), this.COMPONENT_MONITOR_INSTANCE.containsKey(annotation));
        }

        for (Checker annotation : this.COMPONENT_CHECKER_CLAZZ.keySet()) {
            result.put(annotation.value(), this.COMPONENT_CHECKER_CLAZZ.containsKey(annotation));
        }

        for (Executor annotation : this.COMPONENT_EXECUTOR_CLAZZ.keySet()) {
            result.put(annotation.value(), this.COMPONENT_EXECUTOR_INSTANCE.containsKey(annotation));
        }

        return result;
    }


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
            result.put(annotation, this.COMPONENT_FILTER_CLAZZ.containsKey(annotation));
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

    public List<Checker> listGlobalUsersChecker() {
        return this.GLOBAL_CHECKER_USERS_POOL.stream()
                   .map(item -> item.getClass().getAnnotation(Checker.class))
                   .collect(Collectors.toUnmodifiableList());
    }


    public List<Checker> listGlobalGroupChecker() {
        return this.GLOBAL_CHECKER_USERS_POOL.stream()
                   .map(item -> item.getClass().getAnnotation(Checker.class))
                   .collect(Collectors.toUnmodifiableList());
    }


    public Map<String, List<Checker>> listCommandsUsersChecker() {
        Map<String, List<Checker>> result = new LinkedHashMap<>();
        for (Map.Entry<String, CopyOnWriteArrayList<EventHandlerChecker>> entry : this.COMMAND_CHECKER_USERS_POOL.entrySet()) {
            var k = entry.getKey();
            var v = entry.getValue();
            List<Checker> collect = v.stream().map(item -> item.getClass().getAnnotation(Checker.class)).collect(Collectors.toUnmodifiableList());
            result.put(k, collect);
        }
        return result;
    }

    public Map<String, List<Checker>> listCommandsGroupChecker() {
        Map<String, List<Checker>> result = new LinkedHashMap<>();
        for (Map.Entry<String, CopyOnWriteArrayList<EventHandlerChecker>> entry : this.COMMAND_CHECKER_GROUP_POOL.entrySet()) {
            var k = entry.getKey();
            var v = entry.getValue();
            List<Checker> collect = v.stream().map(item -> item.getClass().getAnnotation(Checker.class)).collect(Collectors.toUnmodifiableList());
            result.put(k, collect);
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


    // =================================================================================================================
    // 模块操作


    public void shutModule(String name) {
        AbstractEventHandler moduleInstance = this.getModuleInstance(name);
        if (moduleInstance == null) {
            System.out.println("没有找到模块实例 -> " + name + " " + (this.getModuleClass(name) == null ? "不存在" : "未加载"));
            return;
        }
        String instanceName = moduleInstance.getClass().getName() + ":" + hash(moduleInstance);
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
        String instanceName = moduleInstance.getClass().getName() + ":" + hash(moduleInstance);
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
        String instanceName = moduleInstance.getClass().getName() + ":" + hash(moduleInstance);
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
        String instanceName = moduleInstance.getClass().getName() + ":" + hash(moduleInstance);
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

        for (Executor annotation : this.COMPONENT_EXECUTOR_INSTANCE.keySet()) {
            if (annotation.value().equals(name)) {
                this.unloadExecutorInstance(annotation);
                return;
            }
        }

        for (Checker annotation : this.COMPONENT_CHECKER_INSTANCE.keySet()) {
            if (annotation.value().equals(name)) {
                this.unloadCheckerInstance(annotation);
                return;
            }
        }

        for (Monitor annotation : this.COMPONENT_MONITOR_INSTANCE.keySet()) {
            if (annotation.value().equals(name)) {
                this.unloadMonitorInstance(annotation);
                return;
            }
        }

        for (Filter annotation : this.COMPONENT_FILTER_CLAZZ.keySet()) {
            if (annotation.value().equals(name)) {
                this.unloadFilterInstance(annotation);
                return;
            }
        }

        for (Runner annotation : this.COMPONENT_RUNNER_INSTANCE.keySet()) {
            if (annotation.value().equals(name)) {
                this.unloadRunnerInstance(annotation);
                return;
            }
        }

    }


    @SuppressWarnings("deprecation")
    public void reloadModule(String name) {


        for (Map.Entry<Runner, EventHandlerRunner> entry : this.COMPONENT_RUNNER_INSTANCE.entrySet()) {
            var annotation = entry.getKey();
            var oldInstance = entry.getValue();
            if (annotation.value().equals(name)) {
                this.logger.info("关闭定时器 -> " + name + ":" + hash(oldInstance));
                oldInstance.shutWrapper();
                Class<? extends EventHandlerRunner> clazz = this.COMPONENT_RUNNER_CLAZZ.get(annotation);
                this.logger.info("创建新实例 -> " + this.MODULE_PLUGIN_RELATION.get(annotation.value()) + ":" + clazz.getName());
                EventHandlerRunner newInstance;
                try {
                    newInstance = clazz.getConstructor().newInstance();
                } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException exception) {
                    throw new BotException("重载定时器失败 " + clazz.getName(), exception);
                }
                newInstance.internalInit(annotation.value());
                this.logger.info("预载定时器 -> " + clazz.getName() + ":" + hash(newInstance));
                newInstance.initWrapper();
                this.logger.info("关闭定时器 -> " + clazz.getName() + ":" + hash(newInstance));
                newInstance.bootWrapper();
                this.COMPONENT_RUNNER_INSTANCE.put(annotation, newInstance);
                return;
            }
        }


        for (Map.Entry<Filter, EventHandlerFilter> entry : this.COMPONENT_FILTER_INSTANCE.entrySet()) {
            var annotation = entry.getKey();
            var oldInstance = entry.getValue();
            if (annotation.value().equals(name)) {
                this.logger.info("关闭旧过滤器 -> " + name + ":" + hash(oldInstance));
                oldInstance.shutWrapper();
                Class<? extends EventHandlerFilter> clazz = this.COMPONENT_FILTER_CLAZZ.get(annotation);
                this.logger.info("创建新实例 -> " + this.MODULE_PLUGIN_RELATION.get(annotation.value()) + ":" + clazz.getName());
                EventHandlerFilter newInstance;
                try {
                    newInstance = clazz.getConstructor().newInstance();
                } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException exception) {
                    throw new BotException("创建过滤器失败 " + clazz.getName(), exception);
                }
                newInstance.internalInit(annotation.value());
                this.logger.info("预载过滤器 -> " + clazz.getName() + ":" + hash(newInstance));
                newInstance.initWrapper();
                this.logger.info("关闭过滤器 -> " + clazz.getName() + ":" + hash(newInstance));
                newInstance.bootWrapper();
                this.COMPONENT_FILTER_INSTANCE.put(annotation, newInstance);
                this.logger.info("注册过滤器 -> " + clazz.getName() + ":" + hash(newInstance));
                if (annotation.users()) {
                    int usersIndex = this.FILTER_USERS_CHAIN.indexOf(oldInstance);
                    this.FILTER_USERS_CHAIN.remove(oldInstance);
                    this.FILTER_USERS_CHAIN.add(usersIndex, newInstance);
                }
                if (annotation.group()) {
                    int groupIndex = this.FILTER_GROUP_CHAIN.indexOf(oldInstance);
                    this.FILTER_GROUP_CHAIN.remove(oldInstance);
                    this.FILTER_GROUP_CHAIN.add(groupIndex, newInstance);
                }
                return;
            }
        }


        for (Map.Entry<Monitor, EventHandlerMonitor> entry : this.COMPONENT_MONITOR_INSTANCE.entrySet()) {
            var annotation = entry.getKey();
            var oldInstance = entry.getValue();
            if (annotation.value().equals(name)) {
                this.logger.info("关闭监听器 -> " + name + ":" + hash(oldInstance));
                oldInstance.shutWrapper();
                Class<? extends EventHandlerMonitor> clazz = this.COMPONENT_MONITOR_CLAZZ.get(annotation);
                this.logger.info("创建新实例 -> " + this.MODULE_PLUGIN_RELATION.get(annotation.value()) + ":" + clazz.getName());
                EventHandlerMonitor newInstance;
                try {
                    newInstance = clazz.getConstructor().newInstance();
                } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException exception) {
                    throw new BotException("重载监听器失败 " + clazz.getName(), exception);
                }
                newInstance.internalInit(annotation.value());
                this.logger.info("预载监听器 -> " + clazz.getName() + ":" + hash(newInstance));
                newInstance.initWrapper();
                this.logger.info("关闭监听器 -> " + clazz.getName() + ":" + hash(newInstance));
                newInstance.bootWrapper();
                this.COMPONENT_MONITOR_INSTANCE.put(annotation, newInstance);
                if (annotation.users()) {
                    int usersIndex = this.MONITOR_USERS_CHAIN.indexOf(oldInstance);
                    this.MONITOR_USERS_CHAIN.remove(oldInstance);
                    this.MONITOR_USERS_CHAIN.add(usersIndex, newInstance);
                }
                if (annotation.group()) {
                    int groupIndex = this.MONITOR_GROUP_CHAIN.indexOf(oldInstance);
                    this.MONITOR_GROUP_CHAIN.remove(oldInstance);
                    this.MONITOR_GROUP_CHAIN.add(groupIndex, newInstance);
                }
                return;
            }
        }


        for (Map.Entry<Checker, EventHandlerChecker> entry : this.COMPONENT_CHECKER_INSTANCE.entrySet()) {
            var annotation = entry.getKey();
            var oldInstance = entry.getValue();
            if (annotation.value().equals(name)) {
                this.logger.info("关闭检查器 -> " + name + ":" + hash(oldInstance));
                oldInstance.shutWrapper();
                Class<? extends EventHandlerChecker> clazz = this.COMPONENT_CHECKER_CLAZZ.get(annotation);
                this.logger.info("创建新实例 -> " + this.MODULE_PLUGIN_RELATION.get(annotation.value()) + ":" + clazz.getName());
                EventHandlerChecker newInstance;
                try {
                    newInstance = clazz.getConstructor().newInstance();
                } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException exception) {
                    throw new BotException("重载检查器失败 " + clazz.getName(), exception);
                }
                newInstance.internalInit(annotation.value());
                this.logger.info("预载检查器 -> " + clazz.getName() + ":" + hash(newInstance));
                newInstance.initWrapper();
                this.logger.info("关闭检查器 -> " + clazz.getName() + ":" + hash(newInstance));
                newInstance.bootWrapper();
                this.COMPONENT_CHECKER_INSTANCE.put(annotation, newInstance);
                if (annotation.command().equals("\\*")) {
                    if (annotation.users()) {
                        int usersIndex = this.GLOBAL_CHECKER_USERS_POOL.indexOf(oldInstance);
                        this.GLOBAL_CHECKER_USERS_POOL.remove(oldInstance);
                        this.GLOBAL_CHECKER_USERS_POOL.add(usersIndex, newInstance);
                    }
                    if (annotation.group()) {
                        int usersIndex = this.GLOBAL_CHECKER_GROUP_POOL.indexOf(oldInstance);
                        this.GLOBAL_CHECKER_GROUP_POOL.remove(oldInstance);
                        this.GLOBAL_CHECKER_GROUP_POOL.add(usersIndex, newInstance);
                    }
                } else {
                    if (annotation.users()) {
                        List<EventHandlerChecker> checkerList = this.COMMAND_CHECKER_USERS_POOL.get(annotation.command());
                        int usersIndex = checkerList.indexOf(oldInstance);
                        checkerList.remove(oldInstance);
                        checkerList.add(usersIndex, newInstance);
                    }
                    if (annotation.group()) {
                        List<EventHandlerChecker> checkerList = this.COMMAND_CHECKER_GROUP_POOL.get(annotation.command());
                        int usersIndex = checkerList.indexOf(oldInstance);
                        checkerList.remove(oldInstance);
                        checkerList.add(usersIndex, newInstance);
                    }
                }
                return;
            }
        }


        for (Map.Entry<Executor, EventHandlerExecutor> entry : this.COMPONENT_EXECUTOR_INSTANCE.entrySet()) {
            var annotation = entry.getKey();
            var oldInstance = entry.getValue();
            if (annotation.value().equals(name)) {
                this.logger.info("关闭执行器 -> " + name + ":" + hash(oldInstance));
                oldInstance.shutWrapper();
                Class<? extends EventHandlerExecutor> clazz = this.COMPONENT_EXECUTOR_CLAZZ.get(annotation);
                this.logger.info("创建新实例 -> " + this.MODULE_PLUGIN_RELATION.get(annotation.value()) + ":" + clazz.getName());
                EventHandlerExecutor newInstance;
                try {
                    newInstance = clazz.getConstructor().newInstance();
                } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException exception) {
                    throw new BotException("重载执行器失败 " + clazz.getName(), exception);
                }
                newInstance.internalInit(annotation.value());
                this.logger.info("预载执行器 -> " + clazz.getName() + ":" + hash(newInstance));
                newInstance.initWrapper();
                this.logger.info("关闭执行器 -> " + clazz.getName() + ":" + hash(newInstance));
                newInstance.bootWrapper();
                this.COMPONENT_EXECUTOR_INSTANCE.put(annotation, newInstance);
                if (annotation.users()) this.EXECUTOR_USERS_POOL.put(annotation.command(), newInstance);
                if (annotation.group()) this.EXECUTOR_GROUP_POOL.put(annotation.command(), newInstance);
                return;
            }
        }
    }


    // =================================================================================================================
    // 获取Runner


    @SuppressWarnings("unchecked")
    public <T extends EventHandlerRunner> T getRunner(Class<T> clazz) {
        List<EventHandlerRunner> collect = this.COMPONENT_RUNNER_INSTANCE.values().stream()
                                               .filter(clazz::isInstance)
                                               .collect(Collectors.toUnmodifiableList());
        if (collect.size() == 1) {
            return (T) collect.get(0);
        } else {
            return null;
        }
    }


    // =================================================================================================================
    //
    // 模型相关
    //
    // =================================================================================================================


    // =================================================================================================================
    // 扫描


    public void scan() {

        File[] files = this.folder.listFiles();

        if (files == null) {
            this.logger.warning("无法扫描模块 插件目录为空");
            return;
        }

        this.logger.hint("扫描插件");

        for (File file : files) {
            if (file.isDirectory()) {
                continue;
            }
            try {
                Plugin plugin = new Plugin(file);
                String name = plugin.getName();
                if (this.PLUGINS.containsKey(name)) {
                    Plugin exist = this.PLUGINS.get(name);
                    throw new ScanException("发现插件名称冲突 " + plugin.getFile().getAbsolutePath() + "名称" + name + "已被注册" + exist.getFile().getAbsolutePath());
                }
                plugin.scan();
                this.PLUGINS.put(name, plugin);
            } catch (ScanException exception) {
                this.logger.warning("扫描插件失败 " + file.getName(), exception);
            }
        }

    }


    // =================================================================================================================
    // 检查和注册


    public void load() {
        this.logger.hint("扫描模块 " + this.PLUGINS.size());
        for (Plugin plugin : this.PLUGINS.values()) {
            this.load(plugin);
        }
    }


    public void load(Plugin pluginItem) {

        String pluginName = pluginItem.getName();

        if (pluginItem.getModules().isEmpty()) {
            this.logger.warning("插件包内不含任何模块 " + pluginName);
            return;
        }


        this.logger.info("模块冲突检查 -> " + pluginName);


        for (Map.Entry<Runner, Class<? extends EventHandlerRunner>> moduleEntry : pluginItem.getRunnerClassMap().entrySet()) {
            var k = moduleEntry.getKey();
            var v = moduleEntry.getValue();
            if (this.COMPONENT_RUNNER_CLAZZ.containsKey(k)) {
                Class<? extends AbstractEventHandler> exist = this.COMPONENT_RUNNER_CLAZZ.get(k);
                throw new ScanException("发现模块名冲突 " + pluginName + ":" + v.getName() + "与" + this.COMPONENT_RUNNER_CLAZZ.get(k) + ":" + exist.getName());
            }
        }

        for (Map.Entry<Filter, Class<? extends EventHandlerFilter>> moduleEntry : pluginItem.getFilterClassMap().entrySet()) {
            var k = moduleEntry.getKey();
            var v = moduleEntry.getValue();
            if (this.COMPONENT_FILTER_CLAZZ.containsKey(k)) {
                Class<? extends AbstractEventHandler> exist = this.COMPONENT_FILTER_CLAZZ.get(k);
                throw new ScanException("发现模块名冲突 " + pluginName + ":" + v.getName() + "与" + this.COMPONENT_FILTER_CLAZZ.get(k) + ":" + exist.getName());
            }
        }

        for (Map.Entry<Monitor, Class<? extends EventHandlerMonitor>> moduleEntry : pluginItem.getMonitorClassMap().entrySet()) {
            var k = moduleEntry.getKey();
            var v = moduleEntry.getValue();
            if (this.COMPONENT_MONITOR_CLAZZ.containsKey(k)) {
                Class<? extends AbstractEventHandler> exist = this.COMPONENT_MONITOR_CLAZZ.get(k);
                throw new ScanException("发现模块名冲突 " + pluginName + ":" + v.getName() + "与" + this.COMPONENT_MONITOR_CLAZZ.get(k) + ":" + exist.getName());
            }
        }

        for (Map.Entry<Checker, Class<? extends EventHandlerChecker>> moduleEntry : pluginItem.getCheckerClassMap().entrySet()) {
            var k = moduleEntry.getKey();
            var v = moduleEntry.getValue();
            if (this.COMPONENT_CHECKER_CLAZZ.containsKey(k)) {
                Class<? extends AbstractEventHandler> exist = this.COMPONENT_CHECKER_CLAZZ.get(k);
                throw new ScanException("发现模块名冲突 " + pluginName + ":" + v.getName() + "与" + this.COMPONENT_CHECKER_CLAZZ.get(k) + ":" + exist.getName());
            }
        }

        for (Map.Entry<Executor, Class<? extends EventHandlerExecutor>> moduleEntry : pluginItem.getExecutorClassMap().entrySet()) {
            var k = moduleEntry.getKey();
            var v = moduleEntry.getValue();
            if (this.COMPONENT_EXECUTOR_CLAZZ.containsKey(k)) {
                Class<? extends AbstractEventHandler> exist = this.COMPONENT_EXECUTOR_CLAZZ.get(k);
                throw new ScanException("发现模块名冲突 " + pluginName + ":" + v.getName() + "与" + this.COMPONENT_EXECUTOR_CLAZZ.get(k) + ":" + exist.getName());
            }
        }

        for (Map.Entry<Executor, Class<? extends EventHandlerExecutor>> entry : pluginItem.getExecutorClassMap().entrySet()) {
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


        this.logger.info("模块检查通过 -> " + pluginName);


        for (Map.Entry<Runner, Class<? extends EventHandlerRunner>> entry : pluginItem.getRunnerClassMap().entrySet()) {
            var k = entry.getKey();
            var v = entry.getValue();
            String moduleName = k.value();
            this.MODULES.put(moduleName, v);
            this.COMPONENT_RUNNER_CLAZZ.put(k, v);
            this.MODULE_PLUGIN_RELATION.put(moduleName, pluginName);
            this.logger.info("注册定时器" + pluginName + ":" + moduleName + "[" + k.priority() + "] -> " + v.getName());
        }

        for (Map.Entry<Filter, Class<? extends EventHandlerFilter>> entry : pluginItem.getFilterClassMap().entrySet()) {
            var k = entry.getKey();
            var v = entry.getValue();
            String moduleName = k.value();
            this.MODULES.put(moduleName, v);
            this.COMPONENT_FILTER_CLAZZ.put(k, v);
            this.MODULE_PLUGIN_RELATION.put(moduleName, pluginName);
            this.logger.info("注册过滤器" + pluginName + ":" + moduleName + "[" + k.priority() + "] -> " + v.getName());
        }

        for (Map.Entry<Monitor, Class<? extends EventHandlerMonitor>> entry : pluginItem.getMonitorClassMap().entrySet()) {
            var k = entry.getKey();
            var v = entry.getValue();
            String moduleName = k.value();
            this.MODULES.put(moduleName, v);
            this.COMPONENT_MONITOR_CLAZZ.put(k, v);
            this.MODULE_PLUGIN_RELATION.put(moduleName, pluginName);
            this.logger.info("注册监听器" + pluginName + ":" + moduleName + "[" + k.priority() + "] -> " + v.getName());
        }

        for (Map.Entry<Checker, Class<? extends EventHandlerChecker>> entry : pluginItem.getCheckerClassMap().entrySet()) {
            var k = entry.getKey();
            var v = entry.getValue();
            String moduleName = k.value();
            this.MODULES.put(moduleName, v);
            this.COMPONENT_CHECKER_CLAZZ.put(k, v);
            this.MODULE_PLUGIN_RELATION.put(moduleName, pluginName);
            this.logger.info("注册检查器" + pluginName + ":" + moduleName + "[" + k.priority() + "] -> " + v.getName());
        }

        for (Map.Entry<Executor, Class<? extends EventHandlerExecutor>> entry : pluginItem.getExecutorClassMap().entrySet()) {
            var k = entry.getKey();
            var v = entry.getValue();
            String moduleName = k.value();
            this.MODULES.put(moduleName, v);
            this.COMMAND_EXECUTOR_RELATION.put(k.command(), k);
            this.COMPONENT_EXECUTOR_CLAZZ.put(k, v);
            this.MODULE_PLUGIN_RELATION.put(moduleName, pluginName);
            this.logger.info("注册执行器" + pluginName + ":" + moduleName + "[" + k.command() + "] -> " + v.getName());
        }
    }


    // =================================================================================================================
    // 实例化


    public void make() {

        this.logger.hint("加载定时器" + this.COMPONENT_RUNNER_CLAZZ.size());
        this.COMPONENT_RUNNER_CLAZZ.forEach(this::makeRunner);

        this.logger.hint("加载过滤器" + this.COMPONENT_FILTER_CLAZZ.size());
        this.COMPONENT_FILTER_CLAZZ.forEach(this::makeFilter);

        this.logger.hint("加载监听器" + this.COMPONENT_MONITOR_CLAZZ.size());
        this.COMPONENT_MONITOR_CLAZZ.forEach(this::makeMonitor);

        this.logger.hint("加载检查器" + this.COMPONENT_CHECKER_CLAZZ.size());
        this.COMPONENT_CHECKER_CLAZZ.forEach(this::makeChecker);

        this.logger.hint("加载执行器" + this.COMPONENT_EXECUTOR_CLAZZ.size());
        this.COMPONENT_EXECUTOR_CLAZZ.forEach(this::makeExecutor);

    }


    /**
     * 为什么危险：
     * 如果插件B依赖了插件A，正常启动过程将所有的模块按优先级注册再统一执行生命周期，是保证安全的。
     * 但是重载插件只重载指定插件，后果未知，会出现插件B中持有已关闭的旧对象，IoC保存的是新版本对象。
     *
     * @param plugin 插件
     */
    public void make(Plugin plugin) {

        Set<Runner> runnerHashSet = new HashSet<>(plugin.getRunnerClassMap().keySet());

        this.logger.hint("加载定时器" + runnerHashSet.size());

        for (Map.Entry<Runner, Class<? extends EventHandlerRunner>> entry : this.COMPONENT_RUNNER_CLAZZ.entrySet()) {
            var k = entry.getKey();
            var v = entry.getValue();
            if (!runnerHashSet.contains(k)) {
                continue;
            }
            this.makeRunner(k, v);
        }


        Set<Filter> filterHashSet = new HashSet<>(plugin.getFilterClassMap().keySet());

        this.logger.hint("加载过滤器" + filterHashSet.size());

        for (Map.Entry<Filter, Class<? extends EventHandlerFilter>> entry : this.COMPONENT_FILTER_CLAZZ.entrySet()) {
            var k = entry.getKey();
            var v = entry.getValue();
            if (!filterHashSet.contains(k)) {
                continue;
            }
            this.makeFilter(k, v);
        }


        Set<Monitor> monitorHashSet = new HashSet<>(plugin.getMonitorClassMap().keySet());

        this.logger.hint("加载监听器" + monitorHashSet.size());

        for (Map.Entry<Monitor, Class<? extends EventHandlerMonitor>> entry : this.COMPONENT_MONITOR_CLAZZ.entrySet()) {
            var k = entry.getKey();
            var v = entry.getValue();
            if (!monitorHashSet.contains(k)) {
                continue;
            }
            this.makeMonitor(k, v);
        }

        Set<Checker> checkerHashSet = new HashSet<>(plugin.getCheckerClassMap().keySet());

        this.logger.hint("加载执行器" + checkerHashSet.size());

        for (Map.Entry<Checker, Class<? extends EventHandlerChecker>> entry : this.COMPONENT_CHECKER_CLAZZ.entrySet()) {
            var k = entry.getKey();
            var v = entry.getValue();
            if (!checkerHashSet.contains(k)) {
                continue;
            }
            this.makeChecker(k, v);
        }

        Set<Executor> executorHashSet = new HashSet<>(plugin.getExecutorClassMap().keySet());

        this.logger.hint("加载执行器" + executorHashSet.size());

        for (Map.Entry<Executor, Class<? extends EventHandlerExecutor>> entry : this.COMPONENT_EXECUTOR_CLAZZ.entrySet()) {
            var k = entry.getKey();
            var v = entry.getValue();
            if (!executorHashSet.contains(k)) {
                continue;
            }
            this.makeExecutor(k, v);
        }

    }


    // =================================================================================================================
    // 执行预载


    public void init() {

        this.logger.hint("预载定时器");
        this.COMPONENT_RUNNER_INSTANCE.forEach(this::initRunner);

        this.logger.hint("预载过滤器");
        this.COMPONENT_FILTER_INSTANCE.forEach(this::initFilter);

        this.logger.hint("预载监听器");
        this.COMPONENT_MONITOR_INSTANCE.forEach(this::initMonitor);

        this.logger.hint("预载检查器");
        this.COMPONENT_CHECKER_INSTANCE.forEach(this::initChecker);

        this.logger.hint("预载执行器");
        this.COMPONENT_EXECUTOR_INSTANCE.forEach(this::initExecutor);

    }


    public void init(Plugin plugin) {


        Set<Runner> runnerHashSet = new HashSet<>(plugin.getRunnerClassMap().keySet());
        this.logger.hint("预载定时器");
        for (Map.Entry<Runner, EventHandlerRunner> entry : this.COMPONENT_RUNNER_INSTANCE.entrySet()) {
            var k = entry.getKey();
            var v = entry.getValue();
            if (!runnerHashSet.contains(k)) {
                continue;
            }
            this.initRunner(k, v);
        }


        Set<Filter> filterHashSet = new HashSet<>(plugin.getFilterClassMap().keySet());
        this.logger.hint("预载过滤器");
        for (Map.Entry<Filter, EventHandlerFilter> entry : this.COMPONENT_FILTER_INSTANCE.entrySet()) {
            var k = entry.getKey();
            var v = entry.getValue();
            if (!filterHashSet.contains(k)) {
                continue;
            }
            this.initFilter(k, v);
        }


        Set<Monitor> monitorHashSet = new HashSet<>(plugin.getMonitorClassMap().keySet());
        this.logger.hint("预载监听器");
        for (Map.Entry<Monitor, EventHandlerMonitor> entry : this.COMPONENT_MONITOR_INSTANCE.entrySet()) {
            var k = entry.getKey();
            var v = entry.getValue();
            if (!monitorHashSet.contains(k)) {
                continue;
            }
            this.initMonitor(k, v);
        }


        Set<Checker> checkerHashSet = new HashSet<>(plugin.getCheckerClassMap().keySet());
        this.logger.hint("预载检查器");
        for (Map.Entry<Checker, EventHandlerChecker> entry : this.COMPONENT_CHECKER_INSTANCE.entrySet()) {
            var k = entry.getKey();
            var v = entry.getValue();
            if (!checkerHashSet.contains(k)) {
                continue;
            }
            this.initChecker(k, v);
        }


        Set<Executor> executorHashSet = new HashSet<>(plugin.getExecutorClassMap().keySet());
        this.logger.hint("预载执行器");
        for (Map.Entry<Executor, EventHandlerExecutor> entry : this.COMPONENT_EXECUTOR_INSTANCE.entrySet()) {
            var k = entry.getKey();
            var v = entry.getValue();
            if (!executorHashSet.contains(k)) {
                continue;
            }
            this.initExecutor(k, v);
        }

    }


    // =================================================================================================================
    // 执行启动


    public void boot() {

        this.logger.hint("启动定时器");
        this.COMPONENT_RUNNER_INSTANCE.forEach(this::bootRunner);

        this.logger.hint("启动过滤器");
        this.COMPONENT_FILTER_INSTANCE.forEach(this::bootFilter);

        this.logger.hint("启动监听器");
        this.COMPONENT_MONITOR_INSTANCE.forEach(this::bootMonitor);

        this.logger.hint("启动检查器");
        this.COMPONENT_CHECKER_INSTANCE.forEach(this::bootChecker);

        this.logger.hint("启动执行器");
        this.COMPONENT_EXECUTOR_INSTANCE.forEach(this::bootExecutor);

    }


    public void boot(Plugin plugin) {

        Set<Runner> runnerHashSet = new HashSet<>(plugin.getRunnerClassMap().keySet());
        this.logger.hint("启动定时器");
        for (Map.Entry<Runner, EventHandlerRunner> entry : this.COMPONENT_RUNNER_INSTANCE.entrySet()) {
            var k = entry.getKey();
            var v = entry.getValue();
            if (!runnerHashSet.contains(k)) {
                continue;
            }
            this.bootRunner(k, v);
        }


        Set<Filter> filterHashSet = new HashSet<>(plugin.getFilterClassMap().keySet());
        this.logger.hint("启动过滤器");
        for (Map.Entry<Filter, EventHandlerFilter> entry : this.COMPONENT_FILTER_INSTANCE.entrySet()) {
            var k = entry.getKey();
            var v = entry.getValue();
            if (!filterHashSet.contains(k)) {
                continue;
            }
            this.logger.info("启动过滤器 " + k.value() + "[" + k.priority() + "] -> " + v.getClass().getName());
            try {
                v.bootWrapper();
            } catch (Exception exception) {
                throw new BootException("启动过滤器失败 " + this.MODULE_PLUGIN_RELATION.get(k.value()) + ":" + k.value() + " -> " + v.getClass().getName(), exception);
            }
            this.bootFilter(k, v);
        }


        Set<Monitor> monitorHashSet = new HashSet<>(plugin.getMonitorClassMap().keySet());
        this.logger.hint("启动监听器");
        for (Map.Entry<Monitor, EventHandlerMonitor> entry : this.COMPONENT_MONITOR_INSTANCE.entrySet()) {
            var k = entry.getKey();
            var v = entry.getValue();
            if (!monitorHashSet.contains(k)) {
                continue;
            }
            this.bootMonitor(k, v);
        }


        Set<Checker> checkerHashSet = new HashSet<>(plugin.getCheckerClassMap().keySet());
        this.logger.hint("启动检查器");
        for (Map.Entry<Checker, EventHandlerChecker> entry : this.COMPONENT_CHECKER_INSTANCE.entrySet()) {
            var k = entry.getKey();
            var v = entry.getValue();
            if (!checkerHashSet.contains(k)) {
                continue;
            }
            this.bootChecker(k, v);
        }


        Set<Executor> executorHashSet = new HashSet<>(plugin.getExecutorClassMap().keySet());
        this.logger.hint("启动执行器");
        for (Map.Entry<Executor, EventHandlerExecutor> entry : this.COMPONENT_EXECUTOR_INSTANCE.entrySet()) {
            var k = entry.getKey();
            var v = entry.getValue();
            if (!executorHashSet.contains(k)) {
                continue;
            }
            this.bootExecutor(k, v);
        }

    }


    // =================================================================================================================
    // 执行关闭


    public void shut() {

        this.logger.hint("关闭执行器");

        for (Map.Entry<Executor, EventHandlerExecutor> entry : this.COMPONENT_EXECUTOR_INSTANCE.descendingMap().entrySet()) {
            var k = entry.getKey();
            var v = entry.getValue();
            try {
                if (Driver.isShutModeDrop()) {
                    Thread thread = new Thread(v::shutWrapper);
                    thread.setDaemon(true);
                    thread.start();
                } else {
                    v.shutWrapper();
                }
            } catch (Exception exception) {
                this.logger.warning("关闭执行器发生异常" + printAnnotation(k) + ":" + hash(k) + " -> " + v.getClass().getName() + ":" + hash(v), exception);
            }
            this.logger.info("关闭执行器" + printAnnotation(k) + ":" + hash(k) + " -> " + v.getClass().getName() + ":" + hash(v));
        }

        this.logger.hint("关闭检查器");

        for (Map.Entry<Checker, EventHandlerChecker> entry : this.COMPONENT_CHECKER_INSTANCE.descendingMap().entrySet()) {
            var k = entry.getKey();
            var v = entry.getValue();
            try {
                if (Driver.isShutModeDrop()) {
                    Thread thread = new Thread(v::shutWrapper);
                    thread.setDaemon(true);
                    thread.start();
                } else {
                    v.shutWrapper();
                }
            } catch (Exception exception) {
                this.logger.warning("关闭检查器发生异常" + printAnnotation(k) + ":" + hash(k) + " -> " + v.getClass().getName() + ":" + hash(v), exception);
            }
            this.logger.info("关闭检查器" + printAnnotation(k) + ":" + hash(k) + " -> " + v.getClass().getName() + ":" + hash(v));
        }

        this.logger.hint("关闭监听器");

        for (Map.Entry<Monitor, EventHandlerMonitor> entry : this.COMPONENT_MONITOR_INSTANCE.descendingMap().entrySet()) {
            var k = entry.getKey();
            var v = entry.getValue();
            try {
                if (Driver.isShutModeDrop()) {
                    Thread thread = new Thread(v::shutWrapper);
                    thread.setDaemon(true);
                    thread.start();
                } else {
                    v.shutWrapper();
                }
            } catch (Exception exception) {
                this.logger.warning("关闭监听器发生异常" + printAnnotation(k) + ":" + hash(k) + " -> " + v.getClass().getName() + ":" + hash(v), exception);
            }
            this.logger.info("关闭监听器" + printAnnotation(k) + ":" + hash(k) + " -> " + v.getClass().getName() + ":" + hash(v));
        }

        this.logger.hint("关闭过滤器");

        for (Map.Entry<Filter, EventHandlerFilter> entry : this.COMPONENT_FILTER_INSTANCE.descendingMap().entrySet()) {
            var k = entry.getKey();
            var v = entry.getValue();
            try {
                if (Driver.isShutModeDrop()) {
                    Thread thread = new Thread(v::shutWrapper);
                    thread.setDaemon(true);
                    thread.start();
                } else {
                    v.shutWrapper();
                }
            } catch (Exception exception) {
                this.logger.warning("关闭过滤器发生异常" + printAnnotation(k) + ":" + hash(k) + " -> " + v.getClass().getName() + ":" + hash(v), exception);
            }
            this.logger.info("关闭过滤器" + printAnnotation(k) + ":" + hash(k) + " -> " + v.getClass().getName() + ":" + hash(v));
        }

        this.logger.hint("关闭定时器");

        for (Map.Entry<Runner, EventHandlerRunner> entry : this.COMPONENT_RUNNER_INSTANCE.descendingMap().entrySet()) {
            var k = entry.getKey();
            var v = entry.getValue();
            try {
                if (Driver.isShutModeDrop()) {
                    Thread thread = new Thread(v::shutWrapper);
                    thread.setDaemon(true);
                    thread.start();
                } else {
                    v.shutWrapper();
                }
            } catch (Exception exception) {
                this.logger.warning("关闭定时器发生异常" + printAnnotation(k) + ":" + hash(k) + " -> " + v.getClass().getName() + ":" + hash(v), exception);
            }
            this.logger.info("关闭定时器" + printAnnotation(k) + ":" + hash(k) + " -> " + v.getClass().getName() + ":" + hash(v));
        }

    }


    // =================================================================================================================
    //
    // 内部方法
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

    public List<EventHandlerChecker> getGlobalCheckerUsersPool() {
        return this.GLOBAL_CHECKER_USERS_POOL;
    }

    public List<EventHandlerChecker> getGlobalCheckerGroupPool() {
        return this.GLOBAL_CHECKER_GROUP_POOL;
    }

    public List<EventHandlerChecker> getCommandCheckerUsersPool(String command) {
        return this.COMMAND_CHECKER_USERS_POOL.get(command);
    }

    public List<EventHandlerChecker> getCommandCheckerGroupPool(String command) {
        return this.COMMAND_CHECKER_GROUP_POOL.get(command);
    }

    public Map<String, EventHandlerExecutor> getExecutorUsersPool() {
        return this.EXECUTOR_USERS_POOL;
    }


    public Map<String, EventHandlerExecutor> getExecutorGroupPool() {
        return this.EXECUTOR_GROUP_POOL;
    }


    private static int compare(Runner o1, Runner o2) {
        if (Objects.equals(o1, o2)) {
            return 0;
        } else {
            int i = o1.priority() - o2.priority();
            return i == 0 ? 1 : i;
        }
    }


    private static int compare(Filter o1, Filter o2) {
        if (Objects.equals(o1, o2)) {
            return 0;
        } else {
            int i = o1.priority() - o2.priority();
            return i == 0 ? 1 : i;
        }
    }


    private static int compare(Monitor o1, Monitor o2) {
        if (Objects.equals(o1, o2)) {
            return 0;
        } else {
            int i = o1.priority() - o2.priority();
            return i == 0 ? 1 : i;
        }
    }


    private static int compare(Checker o1, Checker o2) {
        if (Objects.equals(o1, o2)) {
            return 0;
        } else {
            int i = o1.priority() - o2.priority();
            return i == 0 ? 1 : i;
        }
    }


    private static int compare(Executor o1, Executor o2) {
        return CharSequence.compare(o1.command(), o2.command());
    }


    private Class<? extends AbstractEventHandler> getModuleClass(String name) {

        if (!this.MODULES.containsKey(name)) {
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

        if (!this.MODULES.containsKey(name)) {
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


    @SuppressWarnings("deprecation")
    private void makeRunner(Runner k, Class<? extends EventHandlerRunner> v) {
        this.logger.info("加载定时器" + k.value() + "[" + k.priority() + "] -> " + v.getName());
        EventHandlerRunner instance;
        try {
            instance = v.getConstructor().newInstance();
            instance.internalInit(k.value());
        } catch (InvocationTargetException | InstantiationException | IllegalAccessException | NoSuchMethodException e) {
            throw new LoadException("加载定时器失败 " + this.MODULE_PLUGIN_RELATION.get(k.value()) + ":" + k.value() + "[" + k.priority() + "] -> " + v.getName());
        }
        this.COMPONENT_RUNNER_INSTANCE.put(k, instance);
    }


    @SuppressWarnings("deprecation")
    private void makeExecutor(Executor k, Class<? extends EventHandlerExecutor> v) {
        this.logger.info("加载执行器" + k.value() + "[" + k.command() + "] -> " + v.getName());
        EventHandlerExecutor instance;
        try {
            instance = v.getConstructor().newInstance();
            instance.internalInit(k.value());
            instance.buildHelp(k);
        } catch (InvocationTargetException | InstantiationException | IllegalAccessException | NoSuchMethodException e) {
            throw new LoadException("加载执行器失败 " + this.MODULE_PLUGIN_RELATION.get(k.value()) + ":" + k.value() + " " + v.getName());
        }
        this.COMPONENT_EXECUTOR_INSTANCE.put(k, instance);
        if (k.users()) this.EXECUTOR_USERS_POOL.put(k.command(), instance);
        if (k.group()) this.EXECUTOR_GROUP_POOL.put(k.command(), instance);
    }


    @SuppressWarnings("deprecation")
    private void makeMonitor(Monitor k, Class<? extends EventHandlerMonitor> v) {
        this.logger.info("加载监听器" + k.value() + "[" + k.priority() + "] -> " + v.getName());
        EventHandlerMonitor instance;
        try {
            instance = v.getConstructor().newInstance();
            instance.internalInit(k.value());
        } catch (InvocationTargetException | InstantiationException | IllegalAccessException | NoSuchMethodException e) {
            throw new LoadException("加载监听器失败 " + this.MODULE_PLUGIN_RELATION.get(k.value()) + ":" + k.value() + " " + v.getName());
        }
        this.COMPONENT_MONITOR_INSTANCE.put(k, instance);
        if (k.users()) this.MONITOR_USERS_CHAIN.add(instance);
        if (k.group()) this.MONITOR_GROUP_CHAIN.add(instance);
    }


    @SuppressWarnings("deprecation")
    private void makeChecker(Checker k, Class<? extends EventHandlerChecker> v) {
        this.logger.info("加载检查器" + k.value() + "[" + k.priority() + "] -> " + v.getName());
        EventHandlerChecker instance;
        try {
            instance = v.getConstructor().newInstance();
            instance.internalInit(k.value());
        } catch (InvocationTargetException | InstantiationException | IllegalAccessException | NoSuchMethodException e) {
            throw new LoadException("加载检查器失败 " + this.MODULE_PLUGIN_RELATION.get(k.value()) + ":" + k.value() + " " + v.getName());
        }
        this.COMPONENT_CHECKER_INSTANCE.put(k, instance);
        if (k.command().equals("*")) {
            if (k.users()) this.GLOBAL_CHECKER_USERS_POOL.add(instance);
            if (k.group()) this.GLOBAL_CHECKER_GROUP_POOL.add(instance);
        } else {
            if (k.users()) {
                List<EventHandlerChecker> checkerList = this.COMMAND_CHECKER_USERS_POOL.computeIfAbsent(k.command(), k1 -> new CopyOnWriteArrayList<>());
                checkerList.add(instance);
                checkerList.sort((o1, o2) -> {
                    Checker o1Annotation = o1.getClass().getAnnotation(Checker.class);
                    Checker o2Annotation = o2.getClass().getAnnotation(Checker.class);
                    return o1Annotation.priority() - o2Annotation.priority();
                });
            }
            if (k.group()) {
                List<EventHandlerChecker> checkerList = this.COMMAND_CHECKER_GROUP_POOL.computeIfAbsent(k.command(), k1 -> new CopyOnWriteArrayList<>());
                checkerList.add(instance);
                checkerList.sort((o1, o2) -> {
                    Checker o1Annotation = o1.getClass().getAnnotation(Checker.class);
                    Checker o2Annotation = o2.getClass().getAnnotation(Checker.class);
                    return o1Annotation.priority() - o2Annotation.priority();
                });
            }
        }

    }


    @SuppressWarnings("deprecation")
    private void makeFilter(Filter k, Class<? extends EventHandlerFilter> v) {
        this.logger.info("加载过滤器" + k.value() + "[" + k.priority() + "] -> " + v.getName());
        EventHandlerFilter instance;
        try {
            instance = v.getConstructor().newInstance();
            instance.internalInit(k.value());
        } catch (InvocationTargetException | InstantiationException | IllegalAccessException | NoSuchMethodException e) {
            throw new LoadException("加载过滤器失败 " + this.MODULE_PLUGIN_RELATION.get(k.value()) + ":" + k.value() + " " + v.getName());
        }
        this.COMPONENT_FILTER_INSTANCE.put(k, instance);
        if (k.users()) this.FILTER_USERS_CHAIN.add(instance);
        if (k.group()) this.FILTER_GROUP_CHAIN.add(instance);
    }


    private void initRunner(Runner k, EventHandlerRunner v) {
        this.logger.info("预载定时器" + k.value() + "[" + k.priority() + "] -> " + v.getClass().getName());
        try {
            v.initWrapper();
        } catch (Exception exception) {
            throw new BootException("预载定时器失败 " + this.MODULE_PLUGIN_RELATION.get(k.value()) + ":" + k.value() + " -> " + v.getClass().getName(), exception);
        }
    }


    private void initFilter(Filter k, EventHandlerFilter v) {
        this.logger.info("预载过滤器" + k.value() + "[" + k.priority() + "] -> " + v.getClass().getName());
        try {
            v.initWrapper();
        } catch (Exception exception) {
            throw new BootException("预载过滤器失败 " + this.MODULE_PLUGIN_RELATION.get(k.value()) + ":" + k.value() + " -> " + v.getClass().getName(), exception);
        }
    }


    private void initMonitor(Monitor k, EventHandlerMonitor v) {
        this.logger.info("预载监听器" + k.value() + "[" + k.priority() + "] -> " + v.getClass().getName());
        try {
            v.initWrapper();
        } catch (Exception exception) {
            throw new BootException("预载监听器失败 " + this.MODULE_PLUGIN_RELATION.get(k.value()) + ":" + k.value() + " -> " + v.getClass().getName(), exception);
        }
    }


    private void initChecker(Checker k, EventHandlerChecker v) {
        this.logger.info("预载检查器" + k.value() + "[" + k.priority() + "] -> " + v.getClass().getName());
        try {
            v.initWrapper();
        } catch (Exception exception) {
            throw new BootException("预载检查器失败 " + this.MODULE_PLUGIN_RELATION.get(k.value()) + ":" + k.value() + " -> " + v.getClass().getName(), exception);
        }
    }


    private void initExecutor(Executor k, EventHandlerExecutor v) {
        this.logger.info("预载执行器" + k.value() + "[" + k.command() + "] -> " + v.getClass().getName());
        try {
            v.initWrapper();
        } catch (Exception exception) {
            throw new BootException("预载执行器失败 " + this.MODULE_PLUGIN_RELATION.get(k.value()) + ":" + k.value() + " -> " + v.getClass().getName(), exception);
        }
    }


    private void bootRunner(Runner k, EventHandlerRunner v) {
        this.logger.info("启动定时器" + k.value() + "[" + k.priority() + "] -> " + v.getClass().getName());
        try {
            v.bootWrapper();
        } catch (Exception exception) {
            throw new BootException("启动定时器失败 " + this.MODULE_PLUGIN_RELATION.get(k.value()) + ":" + k.value() + " -> " + v.getClass().getName(), exception);
        }
    }


    private void bootFilter(Filter k, EventHandlerFilter v) {
        this.logger.info("启动过滤器" + k.value() + "[" + k.priority() + "] -> " + v.getClass().getName());
        try {
            v.bootWrapper();
        } catch (Exception exception) {
            throw new BootException("启动过滤器失败 " + this.MODULE_PLUGIN_RELATION.get(k.value()) + ":" + k.value() + " -> " + v.getClass().getName(), exception);
        }
    }


    private void bootMonitor(Monitor k, EventHandlerMonitor v) {
        this.logger.info("启动监听器" + k.value() + "[" + k.priority() + "] -> " + v.getClass().getName());
        try {
            v.bootWrapper();
        } catch (Exception exception) {
            throw new BootException("启动监听器失败 " + this.MODULE_PLUGIN_RELATION.get(k.value()) + ":" + k.value() + " -> " + v.getClass().getName(), exception);
        }
    }


    private void bootChecker(Checker k, EventHandlerChecker v) {
        this.logger.info("启动检查器" + k.value() + "[" + k.priority() + "] -> " + v.getClass().getName());
        try {
            v.bootWrapper();
        } catch (Exception exception) {
            throw new BootException("启动检查器失败 " + this.MODULE_PLUGIN_RELATION.get(k.value()) + ":" + k.value() + " -> " + v.getClass().getName(), exception);
        }
    }


    private void bootExecutor(Executor k, EventHandlerExecutor v) {
        this.logger.info("启动执行器" + k.value() + "[" + k.command() + "] -> " + v.getClass().getName());
        try {
            v.bootWrapper();
        } catch (Exception exception) {
            throw new BootException("启动执行器失败 " + this.MODULE_PLUGIN_RELATION.get(k.value()) + ":" + k.value() + " -> " + v.getClass().getName(), exception);
        }
    }


    private void unloadRunnerInstance(Runner annotation) {
        EventHandlerRunner instance = this.COMPONENT_RUNNER_INSTANCE.remove(annotation);
        try {
            instance.shutWrapper();
        } catch (Exception exception) {
            this.logger.info("停止定时器失败 " + annotation.value() + " -> " + instance.getClass().getName(), exception);
        }
        this.logger.info("卸载定时器" + annotation.value() + " -> " + this.MODULE_PLUGIN_RELATION.get(annotation.value()) + ":" + instance.getClass().getName() + ":" + hash(instance));
    }


    private void unloadFilterInstance(Filter annotation) {
        EventHandlerFilter instance = this.COMPONENT_FILTER_INSTANCE.remove(annotation);
        try {
            instance.shutWrapper();
        } catch (Exception exception) {
            this.logger.info("停止过滤器失败 " + annotation.value() + " -> " + instance.getClass().getName(), exception);
        }
        if (annotation.users()) this.FILTER_USERS_CHAIN.remove(instance);
        if (annotation.group()) this.FILTER_GROUP_CHAIN.remove(instance);
        this.logger.info("卸载过滤器" + annotation.value() + " -> " + this.MODULE_PLUGIN_RELATION.get(annotation.value()) + ":" + instance.getClass().getName() + ":" + hash(instance));
    }


    private void unloadMonitorInstance(Monitor annotation) {
        EventHandlerMonitor instance = this.COMPONENT_MONITOR_INSTANCE.remove(annotation);
        try {
            instance.shutWrapper();
        } catch (Exception exception) {
            this.logger.info("停止监听器失败 " + annotation.value() + " -> " + instance.getClass().getName(), exception);
        }
        this.COMPONENT_MONITOR_INSTANCE.remove(annotation);
        if (annotation.users()) this.MONITOR_USERS_CHAIN.remove(instance);
        if (annotation.group()) this.MONITOR_GROUP_CHAIN.remove(instance);
        this.logger.info("卸载监听器" + annotation.value() + " -> " + this.MODULE_PLUGIN_RELATION.get(annotation.value()) + ":" + instance.getClass().getName() + ":" + hash(instance));
    }


    private void unloadCheckerInstance(Checker annotation) {
        EventHandlerChecker instance = this.COMPONENT_CHECKER_INSTANCE.remove(annotation);
        try {
            instance.shutWrapper();
        } catch (Exception exception) {
            this.logger.info("停止检查器失败 " + annotation.value() + " -> " + instance.getClass().getName(), exception);
        }
        if (annotation.command().equals("*")) {
            if (annotation.users()) this.GLOBAL_CHECKER_USERS_POOL.remove(instance);
            if (annotation.group()) this.GLOBAL_CHECKER_GROUP_POOL.remove(instance);
        } else {
            if (annotation.users()) {
                List<EventHandlerChecker> checkerList = this.COMMAND_CHECKER_USERS_POOL.get(annotation.command());
                if (checkerList == null) return;
                checkerList.remove(instance);
            }
            if (annotation.group()) {
                List<EventHandlerChecker> checkerList = this.COMMAND_CHECKER_GROUP_POOL.get(annotation.command());
                if (checkerList == null) return;
                checkerList.remove(instance);
            }
        }
        this.logger.info("卸载检查器" + annotation.value() + " -> " + this.MODULE_PLUGIN_RELATION.get(annotation.value()) + ":" + instance.getClass().getName() + ":" + hash(instance));
    }


    private void unloadExecutorInstance(Executor annotation) {
        EventHandlerExecutor instance = this.COMPONENT_EXECUTOR_INSTANCE.remove(annotation);
        try {
            instance.shutWrapper();
        } catch (Exception exception) {
            this.logger.info("停止执行器失败 " + annotation.value() + " -> " + instance.getClass().getName(), exception);
        }
        if (annotation.users()) this.EXECUTOR_USERS_POOL.remove(annotation.command());
        if (annotation.group()) this.EXECUTOR_GROUP_POOL.remove(annotation.command());
        this.logger.info("卸载执行器" + annotation.value() + " -> " + this.MODULE_PLUGIN_RELATION.get(annotation.value()) + ":" + instance.getClass().getName() + ":" + hash(instance));
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


    public void verboseStatus() {

        System.out.println(Color.LIGHT_PURPLE + ">> PLUGINS" + Color.RESET);

        for (Map.Entry<String, Plugin> entry : this.PLUGINS.entrySet()) {
            var k = entry.getKey();
            var v = entry.getValue();
            System.out.println(Color.GREEN + k + ":" + hash(v) + " " + v.getFile() + Color.RESET);
            for (Map.Entry<String, Class<? extends AbstractEventHandler>> classEntry : v.getModules().entrySet()) {
                var classK = classEntry.getKey();
                var classV = classEntry.getValue();
                System.out.println(classK + " -> " + classV.getName() + ":" + hash(classV));
            }
        }

        System.out.println(Color.LIGHT_PURPLE + ">> MODULES" + Color.RESET);

        for (Map.Entry<String, Class<? extends AbstractEventHandler>> entry : this.MODULES.entrySet()) {
            var k = entry.getKey();
            var v = entry.getValue();
            System.out.println(k + " -> " + v.getName() + ":" + hash(v));
        }

        System.out.println(Color.LIGHT_PURPLE + ">> MODULE_PLUGIN_RELATION" + Color.RESET);

        for (Map.Entry<String, String> entry : this.MODULE_PLUGIN_RELATION.entrySet()) {
            var k = entry.getKey();
            var v = entry.getValue();
            System.out.println(k + " -> " + v);
        }

        System.out.println(Color.LIGHT_BLUE + ">> COMPONENT_RUNNER_CLAZZ" + Color.RESET);

        for (Map.Entry<Runner, Class<? extends EventHandlerRunner>> entry : this.COMPONENT_RUNNER_CLAZZ.entrySet()) {
            var k = entry.getKey();
            var v = entry.getValue();
            System.out.println(printAnnotation(k) + ":" + hash(k) + " -> " + v.getName() + ":" + hash(v));
        }

        System.out.println(Color.LIGHT_BLUE + ">> COMPONENT_FILTER_CLAZZ" + Color.RESET);

        for (Map.Entry<Filter, Class<? extends EventHandlerFilter>> entry : this.COMPONENT_FILTER_CLAZZ.entrySet()) {
            var k = entry.getKey();
            var v = entry.getValue();
            System.out.println(printAnnotation(k) + ":" + hash(k) + " -> " + v.getName() + ":" + hash(v));
        }

        System.out.println(Color.LIGHT_BLUE + ">> COMPONENT_MONITOR_CLAZZ" + Color.RESET);

        for (Map.Entry<Monitor, Class<? extends EventHandlerMonitor>> entry : this.COMPONENT_MONITOR_CLAZZ.entrySet()) {
            var k = entry.getKey();
            var v = entry.getValue();
            System.out.println(printAnnotation(k) + ":" + hash(k) + " -> " + v.getName() + ":" + hash(v));
        }

        System.out.println(Color.LIGHT_BLUE + ">> COMPONENT_CHECKER_CLAZZ" + Color.RESET);

        for (Map.Entry<Checker, Class<? extends EventHandlerChecker>> entry : this.COMPONENT_CHECKER_CLAZZ.entrySet()) {
            var k = entry.getKey();
            var v = entry.getValue();
            System.out.println(printAnnotation(k) + ":" + hash(k) + " -> " + v.getName() + ":" + hash(v));
        }

        System.out.println(Color.LIGHT_BLUE + ">> COMPONENT_EXECUTOR_CLAZZ" + Color.RESET);

        for (Map.Entry<Executor, Class<? extends EventHandlerExecutor>> entry : this.COMPONENT_EXECUTOR_CLAZZ.entrySet()) {
            var k = entry.getKey();
            var v = entry.getValue();
            System.out.println(printAnnotation(k) + ":" + hash(k) + " -> " + v.getName() + ":" + hash(v));
        }

        System.out.println(Color.LIGHT_BLUE + ">> COMPONENT_RUNNER_INSTANCE" + Color.RESET);

        for (Map.Entry<Runner, EventHandlerRunner> entry : this.COMPONENT_RUNNER_INSTANCE.entrySet()) {
            var k = entry.getKey();
            var v = entry.getValue();
            System.out.println(printAnnotation(k) + ":" + hash(k) + " -> " + v.getClass().getName() + ":" + hash(v));
        }

        System.out.println(Color.LIGHT_BLUE + ">> COMPONENT_FILTER_INSTANCE" + Color.RESET);

        for (Map.Entry<Filter, EventHandlerFilter> entry : this.COMPONENT_FILTER_INSTANCE.entrySet()) {
            var k = entry.getKey();
            var v = entry.getValue();
            System.out.println(printAnnotation(k) + ":" + hash(k) + " -> " + v.getClass().getName() + ":" + hash(v));
        }

        System.out.println(Color.LIGHT_BLUE + ">> COMPONENT_MONITOR_INSTANCE" + Color.RESET);

        for (Map.Entry<Monitor, EventHandlerMonitor> entry : this.COMPONENT_MONITOR_INSTANCE.entrySet()) {
            var k = entry.getKey();
            var v = entry.getValue();
            System.out.println(printAnnotation(k) + ":" + hash(k) + " -> " + v.getClass().getName() + ":" + hash(v));
        }

        System.out.println(Color.LIGHT_BLUE + ">> COMPONENT_CHECKER_INSTANCE" + Color.RESET);

        for (Map.Entry<Checker, EventHandlerChecker> entry : this.COMPONENT_CHECKER_INSTANCE.entrySet()) {
            var k = entry.getKey();
            var v = entry.getValue();
            System.out.println(printAnnotation(k) + ":" + hash(k) + " -> " + v.getClass().getName() + ":" + hash(v));
        }

        System.out.println(Color.LIGHT_BLUE + ">> COMPONENT_EXECUTOR_INSTANCE" + Color.RESET);

        for (Map.Entry<Executor, EventHandlerExecutor> entry : this.COMPONENT_EXECUTOR_INSTANCE.entrySet()) {
            var k = entry.getKey();
            var v = entry.getValue();
            System.out.println(printAnnotation(k) + ":" + hash(k) + " -> " + v.getClass().getName() + ":" + hash(v));
        }

        System.out.println(Color.LIGHT_CYAN + ">> FILTER_USERS_CHAIN" + Color.RESET);

        for (EventHandlerFilter item : this.FILTER_USERS_CHAIN) {
            System.out.println(item.getClass().getName() + ":" + hash(item));
        }

        System.out.println(Color.LIGHT_CYAN + ">> FILTER_GROUP_CHAIN" + Color.RESET);

        for (EventHandlerFilter item : this.FILTER_GROUP_CHAIN) {
            System.out.println(item.getClass().getName() + ":" + hash(item));
        }

        System.out.println(Color.LIGHT_CYAN + ">> MONITOR_USERS_CHAIN" + Color.RESET);

        for (EventHandlerMonitor item : this.MONITOR_USERS_CHAIN) {
            System.out.println(item.getClass().getName() + ":" + hash(item));
        }

        System.out.println(Color.LIGHT_CYAN + ">> MONITOR_GROUP_CHAIN" + Color.RESET);

        for (EventHandlerMonitor item : this.MONITOR_GROUP_CHAIN) {
            System.out.println(item.getClass().getName() + ":" + hash(item));
        }

        System.out.println(Color.LIGHT_CYAN + ">> GLOBAL_CHECKER_USERS_POOL" + Color.RESET);

        for (EventHandlerChecker item : this.GLOBAL_CHECKER_USERS_POOL) {
            System.out.println(item.getClass().getName() + ":" + hash(item));
        }

        System.out.println(Color.LIGHT_CYAN + ">> GLOBAL_CHECKER_GROUP_POOL" + Color.RESET);

        for (EventHandlerChecker item : this.GLOBAL_CHECKER_GROUP_POOL) {
            System.out.println(item.getClass().getName() + ":" + hash(item));
        }

        System.out.println(Color.LIGHT_CYAN + ">> COMMAND_CHECKER_USERS_POOL" + Color.RESET);

        for (Map.Entry<String, CopyOnWriteArrayList<EventHandlerChecker>> entry : this.COMMAND_CHECKER_USERS_POOL.entrySet()) {
            var k = entry.getKey();
            var v = entry.getValue();
            System.out.println(k + " " + v.size());
            for (EventHandlerChecker checker : v) {
                System.out.println(checker.getClass().getName() + ":" + hash(checker));
            }
        }

        System.out.println(Color.LIGHT_CYAN + ">> COMMAND_CHECKER_GROUP_POOL" + Color.RESET);

        for (Map.Entry<String, CopyOnWriteArrayList<EventHandlerChecker>> entry : this.COMMAND_CHECKER_GROUP_POOL.entrySet()) {
            var k = entry.getKey();
            var v = entry.getValue();
            System.out.println(k + " " + v.size());
            for (EventHandlerChecker checker : v) {
                System.out.println(checker.getClass().getName() + ":" + hash(checker));
            }
        }

        System.out.println(Color.LIGHT_CYAN + ">> EXECUTOR_USERS_POOL" + Color.RESET);

        for (Map.Entry<String, EventHandlerExecutor> entry : this.EXECUTOR_USERS_POOL.entrySet()) {
            var k = entry.getKey();
            var v = entry.getValue();
            System.out.println(k + " -> " + v.getClass().getName() + ":" + hash(v));
        }

        System.out.println(Color.LIGHT_CYAN + ">> EXECUTOR_GROUP_POOL" + Color.RESET);

        for (Map.Entry<String, EventHandlerExecutor> entry : this.EXECUTOR_GROUP_POOL.entrySet()) {
            var k = entry.getKey();
            var v = entry.getValue();
            System.out.println(k + " -> " + v.getClass().getName() + ":" + hash(v));
        }

        System.out.println(Color.LIGHT_CYAN + ">> COMMAND_EXECUTOR_RELATION" + Color.RESET);

        for (Map.Entry<String, Executor> entry : this.COMMAND_EXECUTOR_RELATION.entrySet()) {
            var k = entry.getKey();
            var v = entry.getValue();
            System.out.println(Color.GREEN + k + Color.RESET + " -> " + v.value() + ":" + hash(v) + " {" + (v.users() ? "U" : "") + (v.group() ? "G" : "") + "} " + v.outline() + ":" + v.description());
            for (String temp : v.usage()) {
                System.out.println(temp);
            }
            for (String temp : v.privacy()) {
                System.out.println(temp);
            }
        }

    }

}