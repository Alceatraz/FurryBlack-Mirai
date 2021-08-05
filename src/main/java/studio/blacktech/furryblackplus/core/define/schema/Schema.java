/*
 * Copyright (C) 2021 Alceatraz @ BlackTechStudio
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 *
 */

package studio.blacktech.furryblackplus.core.define.schema;

import studio.blacktech.furryblackplus.Driver;
import studio.blacktech.furryblackplus.common.Api;
import studio.blacktech.furryblackplus.core.define.annotation.Executor;
import studio.blacktech.furryblackplus.core.define.annotation.Filter;
import studio.blacktech.furryblackplus.core.define.annotation.Monitor;
import studio.blacktech.furryblackplus.core.define.annotation.Runner;
import studio.blacktech.furryblackplus.core.define.moduel.AbstractEventHandler;
import studio.blacktech.furryblackplus.core.define.moduel.EventHandlerExecutor;
import studio.blacktech.furryblackplus.core.define.moduel.EventHandlerFilter;
import studio.blacktech.furryblackplus.core.define.moduel.EventHandlerMonitor;
import studio.blacktech.furryblackplus.core.define.moduel.EventHandlerRunner;
import studio.blacktech.furryblackplus.core.exception.BotException;
import studio.blacktech.furryblackplus.core.exception.moduels.boot.BootException;
import studio.blacktech.furryblackplus.core.exception.moduels.load.LoadException;
import studio.blacktech.furryblackplus.core.exception.moduels.scan.ScanException;
import studio.blacktech.furryblackplus.core.utilties.logger.LoggerX;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
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


@Api("插件与模块持有")
public class Schema {

    private final LoggerX logger = new LoggerX(Schema.class);

    private final File folder;

    private final Map<String, Plugin> PLUGINS;
    private final Map<String, Class<? extends AbstractEventHandler>> MODULES;

    private final Map<Runner, Class<? extends EventHandlerRunner>> COMPONENT_RUNNER_CLAZZ;
    private final Map<Filter, Class<? extends EventHandlerFilter>> COMPONENT_FILTER_CLAZZ;
    private final Map<Monitor, Class<? extends EventHandlerMonitor>> COMPONENT_MONITOR_CLAZZ;
    private final Map<Executor, Class<? extends EventHandlerExecutor>> COMPONENT_EXECUTOR_CLAZZ;

    private final Map<Runner, EventHandlerRunner> COMPONENT_RUNNER_INSTANCE;
    private final Map<Filter, EventHandlerFilter> COMPONENT_FILTER_INSTANCE;
    private final Map<Monitor, EventHandlerMonitor> COMPONENT_MONITOR_INSTANCE;
    private final Map<Executor, EventHandlerExecutor> COMPONENT_EXECUTOR_INSTANCE;

    private final Map<String, Executor> COMMAND_EXECUTOR_RELATION;
    private final Map<String, String> MODULE_PLUGIN_RELATION;

    private final List<EventHandlerFilter> FILTER_USERS_CHAIN;
    private final List<EventHandlerFilter> FILTER_GROUP_CHAIN;

    private final List<EventHandlerMonitor> MONITOR_USERS_CHAIN;
    private final List<EventHandlerMonitor> MONITOR_GROUP_CHAIN;

    private final Map<String, EventHandlerExecutor> EXECUTOR_USERS_POOL;
    private final Map<String, EventHandlerExecutor> EXECUTOR_GROUP_POOL;


    public Schema(File folder) {

        this.folder = folder;

        this.PLUGINS = new ConcurrentHashMap<>(); // 1
        this.MODULES = new ConcurrentHashMap<>(); // 2

        this.COMPONENT_RUNNER_CLAZZ = new ConcurrentSkipListMap<>(Schema::compare); // 3
        this.COMPONENT_FILTER_CLAZZ = new ConcurrentSkipListMap<>(Schema::compare); // 4
        this.COMPONENT_MONITOR_CLAZZ = new ConcurrentSkipListMap<>(Schema::compare); // 5
        this.COMPONENT_EXECUTOR_CLAZZ = new ConcurrentSkipListMap<>(Schema::compare); // 6

        this.COMPONENT_RUNNER_INSTANCE = new ConcurrentSkipListMap<>(Schema::compare); // 7
        this.COMPONENT_FILTER_INSTANCE = new ConcurrentSkipListMap<>(Schema::compare); // 8
        this.COMPONENT_MONITOR_INSTANCE = new ConcurrentSkipListMap<>(Schema::compare); // 9
        this.COMPONENT_EXECUTOR_INSTANCE = new ConcurrentSkipListMap<>(Schema::compare); // a

        this.COMMAND_EXECUTOR_RELATION = new ConcurrentHashMap<>(); // b
        this.MODULE_PLUGIN_RELATION = new ConcurrentHashMap<>(); // c

        this.FILTER_USERS_CHAIN = new CopyOnWriteArrayList<>(); // d
        this.FILTER_GROUP_CHAIN = new CopyOnWriteArrayList<>(); // f

        this.MONITOR_USERS_CHAIN = new CopyOnWriteArrayList<>(); // g
        this.MONITOR_GROUP_CHAIN = new CopyOnWriteArrayList<>(); // h

        this.EXECUTOR_USERS_POOL = new ConcurrentHashMap<>(); // i
        this.EXECUTOR_GROUP_POOL = new ConcurrentHashMap<>(); // j


        // 用了18个容器就解决了 多方便


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
            builder.append("] ");
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
            builder.append("] ");
            builder.append(executor.description());
            builder.append("\r\n");
        }
        builder.setLength(builder.length() - 2);
        return builder.toString();
    }


    // =================================================================================================================
    // 获取Runner


    @SuppressWarnings("unchecked")
    public <T extends EventHandlerRunner> T getRunner(Class<T> clazz) {
        List<EventHandlerRunner> collect = this.COMPONENT_RUNNER_INSTANCE.values().stream().filter(clazz::isInstance).collect(Collectors.toUnmodifiableList());
        if (collect.size() == 1) {
            return (T) collect.get(0);
        } else {
            return null;
        }
    }


    // =================================================================================================================
    // 插件操作


    public Set<String> listAllPlugin() {
        return this.PLUGINS.keySet();
    }


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


    public void unloadPlugin(String name) {

        Plugin plugin = this.PLUGINS.remove(name);

        if (plugin == null) {
            System.out.println("没有这个插件 " + name);
            return;
        }

        Set<Executor> pendingExecutors = plugin.getExecutorClassMap().keySet();

        ArrayList<Executor> executors = new ArrayList<>(this.COMPONENT_EXECUTOR_CLAZZ.keySet());
        Collections.reverse(executors);

        for (Executor annotation : executors) {
            if (!pendingExecutors.contains(annotation)) {
                continue;
            }
            EventHandlerExecutor instance = this.COMPONENT_EXECUTOR_INSTANCE.remove(annotation);
            String command = annotation.command();
            this.logger.info("卸载执行器 " + annotation.value() + "[" + command + "] -> " + instance.getClass().getName() + ":" + instance.hashCode());
            try {
                instance.shutWrapper();
            } catch (Exception exception) {
                this.logger.warning("关闭执行器失败 " + annotation.value() + " -> " + instance.getClass().getName(), exception);
            }
            this.MODULES.remove(annotation.value());
            this.MODULE_PLUGIN_RELATION.remove(annotation.value());
            this.EXECUTOR_USERS_POOL.remove(command);
            this.EXECUTOR_GROUP_POOL.remove(command);
            this.COMMAND_EXECUTOR_RELATION.remove(command);
        }

        for (Executor executor : pendingExecutors) {
            this.COMPONENT_EXECUTOR_CLAZZ.remove(executor);
        }


        Set<Monitor> pendingMonitors = plugin.getMonitorClassMap().keySet();

        ArrayList<Monitor> monitors = new ArrayList<>(this.COMPONENT_MONITOR_CLAZZ.keySet());
        Collections.reverse(monitors);

        for (Monitor annotation : monitors) {
            if (!pendingMonitors.contains(annotation)) {
                continue;
            }
            EventHandlerMonitor instance = this.COMPONENT_MONITOR_INSTANCE.remove(annotation);
            this.logger.info("卸载监听器 " + annotation.value() + "[" + annotation.priority() + "] -> " + instance.getClass().getName() + ":" + instance.hashCode());
            try {
                instance.shutWrapper();
            } catch (Exception exception) {
                this.logger.warning("关闭监听器失败 " + annotation.value() + " -> " + instance.getClass().getName(), exception);
            }
            this.MODULES.remove(annotation.value());
            this.MODULE_PLUGIN_RELATION.remove(annotation.value());
            this.MONITOR_USERS_CHAIN.remove(instance);
            this.MONITOR_GROUP_CHAIN.remove(instance);
        }


        for (Monitor annotation : pendingMonitors) {
            this.COMPONENT_MONITOR_CLAZZ.remove(annotation);
        }


        Set<Filter> pendingFilters = plugin.getFilterClassMap().keySet();

        ArrayList<Filter> filters = new ArrayList<>(this.COMPONENT_FILTER_CLAZZ.keySet());
        Collections.reverse(monitors);

        for (Filter annotation : filters) {
            if (!pendingFilters.contains(annotation)) {
                continue;
            }
            EventHandlerFilter instance = this.COMPONENT_FILTER_INSTANCE.remove(annotation);
            this.logger.info("卸载过滤器 " + annotation.value() + "[" + annotation.priority() + "] -> " + instance.getClass().getName() + ":" + instance.hashCode());
            try {
                instance.shutWrapper();
            } catch (Exception exception) {
                this.logger.warning("关闭过滤器失败 " + annotation.value() + " -> " + instance.getClass().getName(), exception);
            }
            this.MODULES.remove(annotation.value());
            this.MODULE_PLUGIN_RELATION.remove(annotation.value());
            this.FILTER_USERS_CHAIN.remove(instance);
            this.FILTER_GROUP_CHAIN.remove(instance);
        }


        for (Filter annotation : pendingFilters) {
            this.COMPONENT_FILTER_CLAZZ.remove(annotation);
        }


        Set<Runner> pendingRunners = new HashSet<>(plugin.getRunnerClassMap().keySet());

        ArrayList<Runner> runners = new ArrayList<>(this.COMPONENT_RUNNER_CLAZZ.keySet());
        Collections.reverse(runners);

        for (Runner annotation : runners) {
            if (!pendingRunners.contains(annotation)) {
                continue;
            }
            EventHandlerRunner instance = this.COMPONENT_RUNNER_INSTANCE.remove(annotation);
            this.logger.info("卸载定时器 " + annotation.value() + "[" + annotation.priority() + "] -> " + instance.getClass().getName() + ":" + instance.hashCode());
            try {
                instance.shutWrapper();
            } catch (Exception exception) {
                this.logger.warning("关闭定时器失败 " + annotation.value() + " -> " + instance.getClass().getName(), exception);
            }
            this.MODULES.remove(annotation.value());
            this.MODULE_PLUGIN_RELATION.remove(annotation.value());
        }


        for (Runner annotation : pendingRunners) {
            this.COMPONENT_RUNNER_CLAZZ.remove(annotation);
        }

    }


    // =================================================================================================================
    // 模块操作


    public Map<String, Boolean> listAllRunner() {
        Map<String, Boolean> result = new LinkedHashMap<>();
        for (Runner annotation : this.COMPONENT_RUNNER_CLAZZ.keySet()) {
            result.put(annotation.value(), this.COMPONENT_RUNNER_INSTANCE.containsKey(annotation));
        }
        return result;
    }

    public Map<String, Boolean> listAllFilter() {
        Map<String, Boolean> result = new LinkedHashMap<>();
        for (Filter annotation : this.COMPONENT_FILTER_CLAZZ.keySet()) {
            result.put(annotation.value(), this.COMPONENT_FILTER_CLAZZ.containsKey(annotation));
        }
        return result;
    }

    public Map<String, Boolean> listAllMonitor() {
        Map<String, Boolean> result = new LinkedHashMap<>();
        for (Monitor annotation : this.COMPONENT_MONITOR_CLAZZ.keySet()) {
            result.put(annotation.value(), this.COMPONENT_MONITOR_INSTANCE.containsKey(annotation));
        }
        return result;
    }

    public Map<String, Boolean> listAllExecutor() {
        Map<String, Boolean> result = new LinkedHashMap<>();
        for (Executor annotation : this.COMPONENT_EXECUTOR_CLAZZ.keySet()) {
            result.put(annotation.value(), this.COMPONENT_EXECUTOR_INSTANCE.containsKey(annotation));
        }
        return result;
    }


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

        for (Executor annotation : this.COMPONENT_EXECUTOR_CLAZZ.keySet()) {
            result.put(annotation.value(), this.COMPONENT_EXECUTOR_INSTANCE.containsKey(annotation));
        }

        return result;
    }


    public void shutModule(String name) {
        AbstractEventHandler moduleInstance = this.getModuleInstance(name);
        if (moduleInstance == null) {
            System.out.println("没有找到模块实例 -> " + name + " " + (this.getModuleClass(name) == null ? "不存在" : "未加载"));
            return;
        }
        String instanceName = moduleInstance.getClass().getName() + ":" + moduleInstance.hashCode();
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
        String instanceName = moduleInstance.getClass().getName() + ":" + moduleInstance.hashCode();
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
        String instanceName = moduleInstance.getClass().getName() + ":" + moduleInstance.hashCode();
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
        String instanceName = moduleInstance.getClass().getName() + ":" + moduleInstance.hashCode();
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

        for (Map.Entry<Runner, EventHandlerRunner> entry : this.COMPONENT_RUNNER_INSTANCE.entrySet()) {
            var k = entry.getKey();
            var v = entry.getValue();
            if (k.value().equals(name)) {
                this.logger.info("卸载定时器 -> " + name + " " + this.MODULE_PLUGIN_RELATION.get(k.value()) + ":" + v.getClass().getName() + ":" + v.hashCode());
                v.shutWrapper();
                this.COMPONENT_RUNNER_INSTANCE.remove(k);
                return;
            }
        }


        for (Map.Entry<Filter, EventHandlerFilter> entry : this.COMPONENT_FILTER_INSTANCE.entrySet()) {
            var k = entry.getKey();
            var v = entry.getValue();
            if (k.value().equals(name)) {
                this.logger.info("卸载过滤器 -> " + name + " " + this.MODULE_PLUGIN_RELATION.get(k.value()) + ":" + v.getClass().getName() + ":" + v.hashCode());
                v.shutWrapper();
                this.FILTER_USERS_CHAIN.remove(v);
                this.FILTER_GROUP_CHAIN.remove(v);
                this.COMPONENT_FILTER_INSTANCE.remove(k);
                return;
            }
        }


        for (Map.Entry<Monitor, EventHandlerMonitor> entry : this.COMPONENT_MONITOR_INSTANCE.entrySet()) {
            var k = entry.getKey();
            var v = entry.getValue();
            if (k.value().equals(name)) {
                this.logger.info("卸载监听器 -> " + name + " " + this.MODULE_PLUGIN_RELATION.get(k.value()) + ":" + v.getClass().getName() + ":" + v.hashCode());
                v.shutWrapper();
                this.MONITOR_USERS_CHAIN.remove(v);
                this.MONITOR_GROUP_CHAIN.remove(v);
                this.COMPONENT_MONITOR_INSTANCE.remove(k);
                return;
            }
        }


        for (Map.Entry<Executor, EventHandlerExecutor> entry : this.COMPONENT_EXECUTOR_INSTANCE.entrySet()) {
            var k = entry.getKey();
            var v = entry.getValue();
            if (k.value().equals(name)) {
                this.logger.info("卸载执行器 -> " + name + " " + this.MODULE_PLUGIN_RELATION.get(k.value()) + ":" + v.getClass().getName() + ":" + v.hashCode());
                v.shutWrapper();
                this.EXECUTOR_USERS_POOL.remove(k.value());
                this.EXECUTOR_GROUP_POOL.remove(k.value());
                this.COMPONENT_EXECUTOR_INSTANCE.remove(k);
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
                this.logger.info("关闭定时器 -> " + name + ":" + oldInstance.hashCode());
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
                this.logger.info("预载定时器 -> " + clazz.getName() + ":" + newInstance.hashCode());
                newInstance.initWrapper();
                this.logger.info("关闭定时器 -> " + clazz.getName() + ":" + newInstance.hashCode());
                newInstance.bootWrapper();
                this.COMPONENT_RUNNER_INSTANCE.put(annotation, newInstance);
                return;
            }
        }


        for (Map.Entry<Filter, EventHandlerFilter> entry : this.COMPONENT_FILTER_INSTANCE.entrySet()) {
            var annotation = entry.getKey();
            var oldInstance = entry.getValue();
            if (annotation.value().equals(name)) {
                this.logger.info("关闭过滤器 -> " + name + ":" + oldInstance.hashCode());
                oldInstance.shutWrapper();
                Class<? extends EventHandlerFilter> clazz = this.COMPONENT_FILTER_CLAZZ.get(annotation);
                this.logger.info("创建新实例 -> " + this.MODULE_PLUGIN_RELATION.get(annotation.value()) + ":" + clazz.getName());
                EventHandlerFilter newInstance;
                try {
                    newInstance = clazz.getConstructor().newInstance();
                } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException exception) {
                    throw new BotException("重载过滤器失败 " + clazz.getName(), exception);
                }
                newInstance.internalInit(annotation.value());
                this.logger.info("预载过滤器 -> " + clazz.getName() + ":" + newInstance.hashCode());
                newInstance.initWrapper();
                this.logger.info("关闭过滤器 -> " + clazz.getName() + ":" + newInstance.hashCode());
                newInstance.bootWrapper();
                this.COMPONENT_FILTER_INSTANCE.put(annotation, newInstance);
                return;
            }
        }


        for (Map.Entry<Monitor, EventHandlerMonitor> entry : this.COMPONENT_MONITOR_INSTANCE.entrySet()) {
            var annotation = entry.getKey();
            var oldInstance = entry.getValue();
            if (annotation.value().equals(name)) {
                this.logger.info("关闭监听器 -> " + name + ":" + oldInstance.hashCode());
                oldInstance.shutWrapper();
                Class<? extends EventHandlerMonitor> clazz = this.COMPONENT_MONITOR_CLAZZ.get(annotation);
                this.logger.info("创建新实例 -> " + this.MODULE_PLUGIN_RELATION.get(annotation.value()) + ":" + clazz.getName());
                EventHandlerMonitor newInstance;
                try {
                    newInstance = clazz.getConstructor().newInstance();
                } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException exception) {
                    throw new BotException("重载定时器失败 " + clazz.getName(), exception);
                }
                newInstance.internalInit(annotation.value());
                this.logger.info("预载监听器 -> " + clazz.getName() + ":" + newInstance.hashCode());
                newInstance.initWrapper();
                this.logger.info("关闭监听器 -> " + clazz.getName() + ":" + newInstance.hashCode());
                newInstance.bootWrapper();
                this.COMPONENT_MONITOR_INSTANCE.put(annotation, newInstance);
                return;
            }
        }


        for (Map.Entry<Executor, EventHandlerExecutor> entry : this.COMPONENT_EXECUTOR_INSTANCE.entrySet()) {
            var annotation = entry.getKey();
            var oldInstance = entry.getValue();
            if (annotation.value().equals(name)) {
                this.logger.info("关闭执行器 -> " + name + ":" + oldInstance.hashCode());
                oldInstance.shutWrapper();
                Class<? extends EventHandlerExecutor> clazz = this.COMPONENT_EXECUTOR_CLAZZ.get(annotation);
                this.logger.info("创建新实例 -> " + this.MODULE_PLUGIN_RELATION.get(annotation.value()) + ":" + clazz.getName());
                EventHandlerExecutor newInstance;
                try {
                    newInstance = clazz.getConstructor().newInstance();
                } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException exception) {
                    throw new BotException("重载定时器失败 " + clazz.getName(), exception);
                }
                newInstance.internalInit(annotation.value());
                this.logger.info("预载执行器 -> " + clazz.getName() + ":" + newInstance.hashCode());
                newInstance.initWrapper();
                this.logger.info("关闭执行器 -> " + clazz.getName() + ":" + newInstance.hashCode());
                newInstance.bootWrapper();
                this.COMPONENT_EXECUTOR_INSTANCE.put(annotation, newInstance);
                return;
            }
        }
    }


    // =================================================================================================================
    //
    // 模块管理
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


        // =====================================================================


        this.logger.info("模块冲突检查 -> " + pluginName);

        for (Map.Entry<Runner, Class<? extends EventHandlerRunner>> moduleEntry : pluginItem.getRunnerClassMap().entrySet()) {
            var k = moduleEntry.getKey();
            var v = moduleEntry.getValue();
            if (this.COMPONENT_RUNNER_CLAZZ.containsKey(k)) {
                Class<? extends AbstractEventHandler> exist = this.COMPONENT_RUNNER_CLAZZ.get(k);
                throw new ScanException("发现模块名冲突 " + pluginName + ":" + v.getName() + "与" + this.COMPONENT_RUNNER_CLAZZ.get(k) + ":" + exist.getName());
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


        // =====================================================================


        for (Map.Entry<Runner, Class<? extends EventHandlerRunner>> entry : pluginItem.getRunnerClassMap().entrySet()) {
            var k = entry.getKey();
            var v = entry.getValue();
            String moduleName = k.value();
            this.MODULES.put(moduleName, v);
            this.COMPONENT_RUNNER_CLAZZ.put(k, v);
            this.MODULE_PLUGIN_RELATION.put(moduleName, pluginName);
            this.logger.info("注册定时器 " + pluginName + ":" + moduleName + "[" + k.priority() + "] -> " + v.getName());
        }

        for (Map.Entry<Filter, Class<? extends EventHandlerFilter>> entry : pluginItem.getFilterClassMap().entrySet()) {
            var k = entry.getKey();
            var v = entry.getValue();
            String moduleName = k.value();
            this.MODULES.put(moduleName, v);
            this.COMPONENT_FILTER_CLAZZ.put(k, v);
            this.MODULE_PLUGIN_RELATION.put(moduleName, pluginName);
            this.logger.info("注册过滤器 " + pluginName + ":" + moduleName + "[" + k.priority() + "] -> " + v.getName());
        }

        for (Map.Entry<Monitor, Class<? extends EventHandlerMonitor>> entry : pluginItem.getMonitorClassMap().entrySet()) {
            var k = entry.getKey();
            var v = entry.getValue();
            String moduleName = k.value();
            this.MODULES.put(moduleName, v);
            this.COMPONENT_MONITOR_CLAZZ.put(k, v);
            this.MODULE_PLUGIN_RELATION.put(moduleName, pluginName);
            this.logger.info("注册监听器 " + pluginName + ":" + moduleName + "[" + k.priority() + "] -> " + v.getName());
        }

        for (Map.Entry<Executor, Class<? extends EventHandlerExecutor>> entry : pluginItem.getExecutorClassMap().entrySet()) {
            var k = entry.getKey();
            var v = entry.getValue();
            String moduleName = k.value();
            this.MODULES.put(moduleName, v);
            this.COMMAND_EXECUTOR_RELATION.put(k.command(), k);
            this.COMPONENT_EXECUTOR_CLAZZ.put(k, v);
            this.MODULE_PLUGIN_RELATION.put(moduleName, pluginName);
            this.logger.info("注册执行器 " + pluginName + ":" + moduleName + "[" + k.command() + "] -> " + v.getName());
        }
    }


    // =================================================================================================================
    // 实例化


    public void make() {

        this.logger.hint("加载定时器 " + this.COMPONENT_RUNNER_CLAZZ.size());
        this.COMPONENT_RUNNER_CLAZZ.forEach(this::makeRunner);

        this.logger.hint("加载过滤器 " + this.COMPONENT_FILTER_CLAZZ.size());
        this.COMPONENT_FILTER_CLAZZ.forEach(this::makeFilter);

        this.logger.hint("加载监听器 " + this.COMPONENT_MONITOR_CLAZZ.size());
        this.COMPONENT_MONITOR_CLAZZ.forEach(this::makeMonitor);

        this.logger.hint("加载执行器 " + this.COMPONENT_EXECUTOR_CLAZZ.size());
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

        this.logger.hint("加载定时器 " + runnerHashSet.size());

        for (Map.Entry<Runner, Class<? extends EventHandlerRunner>> entry : this.COMPONENT_RUNNER_CLAZZ.entrySet()) {
            var k = entry.getKey();
            var v = entry.getValue();
            if (!runnerHashSet.contains(k)) {
                continue;
            }
            this.makeRunner(k, v);
        }


        Set<Filter> filterHashSet = new HashSet<>(plugin.getFilterClassMap().keySet());

        this.logger.hint("加载过滤器 " + filterHashSet.size());

        for (Map.Entry<Filter, Class<? extends EventHandlerFilter>> entry : this.COMPONENT_FILTER_CLAZZ.entrySet()) {
            var k = entry.getKey();
            var v = entry.getValue();
            if (!filterHashSet.contains(k)) {
                continue;
            }
            this.makeFilter(k, v);
        }


        Set<Monitor> monitorHashSet = new HashSet<>(plugin.getMonitorClassMap().keySet());

        this.logger.hint("加载监听器 " + monitorHashSet.size());

        for (Map.Entry<Monitor, Class<? extends EventHandlerMonitor>> entry : this.COMPONENT_MONITOR_CLAZZ.entrySet()) {
            var k = entry.getKey();
            var v = entry.getValue();
            if (!monitorHashSet.contains(k)) {
                continue;
            }
            this.makeMonitor(k, v);
        }


        Set<Executor> executorHashSet = new HashSet<>(plugin.getExecutorClassMap().keySet());

        this.logger.hint("加载执行器 " + executorHashSet.size());

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

        ArrayList<Executor> executors = new ArrayList<>(this.COMPONENT_EXECUTOR_INSTANCE.keySet());
        Collections.reverse(executors);
        executors.forEach(this::shutExecutor);


        this.logger.hint("关闭监听器");

        ArrayList<Monitor> monitors = new ArrayList<>(this.COMPONENT_MONITOR_INSTANCE.keySet());
        Collections.reverse(monitors);
        monitors.forEach(this::shutMonitor);


        this.logger.hint("关闭过滤器");

        ArrayList<Filter> filters = new ArrayList<>(this.COMPONENT_FILTER_INSTANCE.keySet());
        Collections.reverse(monitors);
        filters.forEach(this::shutFilter);


        this.logger.hint("关闭定时器");

        ArrayList<Runner> runners = new ArrayList<>(this.COMPONENT_RUNNER_INSTANCE.keySet());
        Collections.reverse(runners);
        runners.forEach(this::shutRunner);

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


    public Map<String, EventHandlerExecutor> getExecutorUsersPool() {
        return this.EXECUTOR_USERS_POOL;
    }


    public Map<String, EventHandlerExecutor> getExecutorGroupPool() {
        return this.EXECUTOR_GROUP_POOL;
    }


    private static int compare(Runner o1, Runner o2) {
        if (o1 == o2 || Objects.equals(o1, o2)) {
            return 0;
        } else {
            int i = o1.priority() - o2.priority();
            return i == 0 ? 1 : i;
        }
    }


    private static int compare(Filter o1, Filter o2) {
        if (o1 == o2 || Objects.equals(o1, o2)) {
            return 0;
        } else {
            int i = o1.priority() - o2.priority();
            return i == 0 ? 1 : i;
        }
    }


    private static int compare(Monitor o1, Monitor o2) {
        if (o1 == o2 || Objects.equals(o1, o2)) {
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

        for (Map.Entry<Executor, EventHandlerExecutor> entry : this.COMPONENT_EXECUTOR_INSTANCE.entrySet()) {
            if (entry.getKey().value().equals(name)) {
                return entry.getValue();
            }
        }

        return null;
    }


    @SuppressWarnings("deprecation")
    private void makeRunner(Runner k, Class<? extends EventHandlerRunner> v) {
        this.logger.info("加载定时器 " + k.value() + "[" + k.priority() + "] -> " + v.getName());
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
        this.logger.info("加载执行器 " + k.value() + "[" + k.command() + "] -> " + v.getName());
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
        this.logger.info("加载监听器 " + k.value() + "[" + k.priority() + "] -> " + v.getName());
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
    private void makeFilter(Filter k, Class<? extends EventHandlerFilter> v) {
        this.logger.info("加载过滤器 " + k.value() + "[" + k.priority() + "] -> " + v.getName());
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
        this.logger.info("预载定时器 " + k.value() + "[" + k.priority() + "] -> " + v.getClass().getName());
        try {
            v.initWrapper();
        } catch (Exception exception) {
            throw new BootException("预载定时器失败 " + this.MODULE_PLUGIN_RELATION.get(k.value()) + ":" + k.value() + " -> " + v.getClass().getName(), exception);
        }
    }


    private void initFilter(Filter k, EventHandlerFilter v) {
        this.logger.info("预载过滤器 " + k.value() + "[" + k.priority() + "] -> " + v.getClass().getName());
        try {
            v.initWrapper();
        } catch (Exception exception) {
            throw new BootException("预载过滤器失败 " + this.MODULE_PLUGIN_RELATION.get(k.value()) + ":" + k.value() + " -> " + v.getClass().getName(), exception);
        }
    }


    private void initMonitor(Monitor k, EventHandlerMonitor v) {
        this.logger.info("预载监听器 " + k.value() + "[" + k.priority() + "] -> " + v.getClass().getName());
        try {
            v.initWrapper();
        } catch (Exception exception) {
            throw new BootException("预载监听器失败 " + this.MODULE_PLUGIN_RELATION.get(k.value()) + ":" + k.value() + " -> " + v.getClass().getName(), exception);
        }
    }


    private void initExecutor(Executor k, EventHandlerExecutor v) {
        this.logger.info("预载执行器 " + k.value() + "[" + k.command() + "] -> " + v.getClass().getName());
        try {
            v.initWrapper();
        } catch (Exception exception) {
            throw new BootException("预载执行器失败 " + this.MODULE_PLUGIN_RELATION.get(k.value()) + ":" + k.value() + " -> " + v.getClass().getName(), exception);
        }
    }


    private void bootRunner(Runner k, EventHandlerRunner v) {
        this.logger.info("启动定时器 " + k.value() + "[" + k.priority() + "] -> " + v.getClass().getName());
        try {
            v.bootWrapper();
        } catch (Exception exception) {
            throw new BootException("启动定时器失败 " + this.MODULE_PLUGIN_RELATION.get(k.value()) + ":" + k.value() + " -> " + v.getClass().getName(), exception);
        }
    }


    private void bootFilter(Filter k, EventHandlerFilter v) {
        this.logger.info("启动过滤器 " + k.value() + "[" + k.priority() + "] -> " + v.getClass().getName());
        try {
            v.bootWrapper();
        } catch (Exception exception) {
            throw new BootException("启动过滤器失败 " + this.MODULE_PLUGIN_RELATION.get(k.value()) + ":" + k.value() + " -> " + v.getClass().getName(), exception);
        }
    }


    private void bootMonitor(Monitor k, EventHandlerMonitor v) {
        this.logger.info("启动监听器 " + k.value() + "[" + k.priority() + "] -> " + v.getClass().getName());
        try {
            v.bootWrapper();
        } catch (Exception exception) {
            throw new BootException("启动监听器失败 " + this.MODULE_PLUGIN_RELATION.get(k.value()) + ":" + k.value() + " -> " + v.getClass().getName(), exception);
        }
    }


    private void bootExecutor(Executor k, EventHandlerExecutor v) {
        this.logger.info("启动执行器 " + k.value() + "[" + k.command() + "] -> " + v.getClass().getName());
        try {
            v.bootWrapper();
        } catch (Exception exception) {
            throw new BootException("启动执行器失败 " + this.MODULE_PLUGIN_RELATION.get(k.value()) + ":" + k.value() + " -> " + v.getClass().getName(), exception);
        }
    }

    private void shutRunner(Runner annotation) {
        EventHandlerRunner instance = this.COMPONENT_RUNNER_INSTANCE.get(annotation);
        this.logger.info("关闭定时器 " + annotation.value() + "[" + annotation.priority() + "] -> " + instance.getClass().getName());
        try {
            if (Driver.isShutModeDrop()) {
                new Thread(instance::shutWrapper).start();
            } else {
                instance.shutWrapper();
            }
        } catch (Exception exception) {
            this.logger.warning("关闭定时器失败 " + annotation.value() + " -> " + instance.getClass().getName(), exception);
        }
    }


    private void shutFilter(Filter annotation) {
        EventHandlerFilter instance = this.COMPONENT_FILTER_INSTANCE.get(annotation);
        this.logger.info("关闭过滤器 " + annotation.value() + "[" + annotation.priority() + "] -> " + instance.getClass().getName());
        try {
            if (Driver.isShutModeDrop()) {
                new Thread(instance::shutWrapper).start();
            } else {
                instance.shutWrapper();
            }
        } catch (Exception exception) {
            this.logger.warning("关闭过滤器失败 " + annotation.value() + " -> " + instance.getClass().getName(), exception);
        }
    }


    private void shutMonitor(Monitor annotation) {
        EventHandlerMonitor instance = this.COMPONENT_MONITOR_INSTANCE.get(annotation);
        this.logger.info("关闭监听器 " + annotation.value() + "[" + annotation.priority() + "] -> " + instance.getClass().getName());
        try {
            if (Driver.isShutModeDrop()) {
                new Thread(instance::shutWrapper).start();
            } else {
                instance.shutWrapper();
            }
        } catch (Exception exception) {
            this.logger.warning("关闭监听器失败 " + annotation.value() + " -> " + instance.getClass().getName(), exception);
        }
    }


    private void shutExecutor(Executor annotation) {
        EventHandlerExecutor instance = this.COMPONENT_EXECUTOR_INSTANCE.get(annotation);
        this.logger.info("关闭执行器 " + annotation.value() + "[" + annotation.command() + "] -> " + instance.getClass().getName());
        try {
            if (Driver.isShutModeDrop()) {
                new Thread(instance::shutWrapper).start();
            } else {
                instance.shutWrapper();
            }
        } catch (Exception exception) {
            this.logger.warning("关闭执行器失败 " + annotation.value() + " -> " + instance.getClass().getName(), exception);
        }
    }


    public void debug() {

        System.out.println(">> PLUGINS");

        for (Map.Entry<String, Plugin> entry : this.PLUGINS.entrySet()) {
            var k = entry.getKey();
            var v = entry.getValue();
            System.out.println(k + " -> " + v);
        }

        System.out.println(">> MODULES");

        for (Map.Entry<String, Class<? extends AbstractEventHandler>> entry : this.MODULES.entrySet()) {
            var k = entry.getKey();
            var v = entry.getValue();
            System.out.println(k + " -> " + v.getName() + ":" + v.hashCode());
        }

        System.out.println(">> COMPONENT_RUNNER_CLAZZ");

        for (Map.Entry<Runner, Class<? extends EventHandlerRunner>> entry : this.COMPONENT_RUNNER_CLAZZ.entrySet()) {
            var k = entry.getKey();
            var v = entry.getValue();
            System.out.println(k + ":" + k.hashCode() + " -> " + v.getName() + ":" + v.hashCode());
        }

        System.out.println(">> COMPONENT_FILTER_CLAZZ");

        for (Map.Entry<Filter, Class<? extends EventHandlerFilter>> entry : this.COMPONENT_FILTER_CLAZZ.entrySet()) {
            var k = entry.getKey();
            var v = entry.getValue();
            System.out.println(k + ":" + k.hashCode() + " -> " + v.getName() + ":" + v.hashCode());
        }

        System.out.println(">> COMPONENT_MONITOR_CLAZZ");

        for (Map.Entry<Monitor, Class<? extends EventHandlerMonitor>> entry : this.COMPONENT_MONITOR_CLAZZ.entrySet()) {
            var k = entry.getKey();
            var v = entry.getValue();
            System.out.println(k + ":" + k.hashCode() + " -> " + v.getName() + ":" + v.hashCode());
        }

        System.out.println(">> COMPONENT_EXECUTOR_CLAZZ");

        for (Map.Entry<Executor, Class<? extends EventHandlerExecutor>> entry : this.COMPONENT_EXECUTOR_CLAZZ.entrySet()) {
            var k = entry.getKey();
            var v = entry.getValue();
            System.out.println(k + ":" + k.hashCode() + " -> " + v.getName() + ":" + v.hashCode());
        }

        System.out.println(">> COMPONENT_RUNNER_INSTANCE");

        for (Map.Entry<Runner, EventHandlerRunner> entry : this.COMPONENT_RUNNER_INSTANCE.entrySet()) {
            var k = entry.getKey();
            var v = entry.getValue();
            System.out.println(k + ":" + k.hashCode() + " -> " + v.getClass().getName() + ":" + v.hashCode());
        }

        System.out.println(">> COMPONENT_FILTER_INSTANCE");

        for (Map.Entry<Filter, EventHandlerFilter> entry : this.COMPONENT_FILTER_INSTANCE.entrySet()) {
            var k = entry.getKey();
            var v = entry.getValue();
            System.out.println(k + ":" + k.hashCode() + " -> " + v.getClass().getName() + ":" + v.hashCode());
        }

        System.out.println(">> COMPONENT_MONITOR_INSTANCE");

        for (Map.Entry<Monitor, EventHandlerMonitor> entry : this.COMPONENT_MONITOR_INSTANCE.entrySet()) {
            var k = entry.getKey();
            var v = entry.getValue();
            System.out.println(k + ":" + k.hashCode() + " -> " + v.getClass().getName() + ":" + v.hashCode());
        }

        System.out.println(">> COMPONENT_EXECUTOR_INSTANCE");

        for (Map.Entry<Executor, EventHandlerExecutor> entry : this.COMPONENT_EXECUTOR_INSTANCE.entrySet()) {
            var k = entry.getKey();
            var v = entry.getValue();
            System.out.println(k + ":" + k.hashCode() + " -> " + v.getClass().getName() + ":" + v.hashCode());
        }

        System.out.println(">> COMMAND_EXECUTOR_RELATION");

        for (Map.Entry<String, Executor> entry : this.COMMAND_EXECUTOR_RELATION.entrySet()) {
            var k = entry.getKey();
            var v = entry.getValue();
            System.out.println(k + " -> " + v);
        }

        System.out.println(">> MODULE_PLUGIN_RELATION");

        for (Map.Entry<String, String> entry : this.MODULE_PLUGIN_RELATION.entrySet()) {
            var k = entry.getKey();
            var v = entry.getValue();
            System.out.println(k + " -> " + v);
        }

        System.out.println(">> FILTER_USERS_CHAIN");

        for (EventHandlerFilter item : this.FILTER_USERS_CHAIN) {
            System.out.println(item.getClass().getName() + ":" + item.hashCode());
        }

        System.out.println(">> FILTER_GROUP_CHAIN");

        for (EventHandlerFilter item : this.FILTER_GROUP_CHAIN) {
            System.out.println(item.getClass().getName() + ":" + item.hashCode());
        }

        System.out.println(">> MONITOR_USERS_CHAIN");

        for (EventHandlerMonitor item : this.MONITOR_USERS_CHAIN) {
            System.out.println(item.getClass().getName() + ":" + item.hashCode());
        }

        System.out.println(">> MONITOR_GROUP_CHAIN");

        for (EventHandlerMonitor item : this.MONITOR_GROUP_CHAIN) {
            System.out.println(item.getClass().getName() + ":" + item.hashCode());
        }

        System.out.println(">> EXECUTOR_USERS_POOL");

        for (Map.Entry<String, EventHandlerExecutor> entry : this.EXECUTOR_USERS_POOL.entrySet()) {
            var k = entry.getKey();
            var v = entry.getValue();
            System.out.println(k + " -> " + v.getClass().getName() + ":" + v.hashCode());
        }

        System.out.println(">> EXECUTOR_GROUP_POOL");

        for (Map.Entry<String, EventHandlerExecutor> entry : this.EXECUTOR_GROUP_POOL.entrySet()) {
            var k = entry.getKey();
            var v = entry.getValue();
            System.out.println(k + " -> " + v.getClass().getName() + ":" + v.hashCode());
        }

    }


}