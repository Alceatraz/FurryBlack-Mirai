package studio.blacktech.furryblackplus.core.define.schema;

import studio.blacktech.furryblackplus.common.Api;
import studio.blacktech.furryblackplus.core.annotation.Executor;
import studio.blacktech.furryblackplus.core.annotation.Filter;
import studio.blacktech.furryblackplus.core.annotation.Monitor;
import studio.blacktech.furryblackplus.core.annotation.Runner;
import studio.blacktech.furryblackplus.core.define.moduel.AbstractEventHandler;
import studio.blacktech.furryblackplus.core.define.moduel.EventHandlerExecutor;
import studio.blacktech.furryblackplus.core.define.moduel.EventHandlerFilter;
import studio.blacktech.furryblackplus.core.define.moduel.EventHandlerMonitor;
import studio.blacktech.furryblackplus.core.define.moduel.EventHandlerRunner;
import studio.blacktech.furryblackplus.core.exception.moduels.boot.BootException;
import studio.blacktech.furryblackplus.core.exception.moduels.load.LoadException;
import studio.blacktech.furryblackplus.core.exception.moduels.scan.ScanException;
import studio.blacktech.furryblackplus.core.utilties.LoggerX;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.CopyOnWriteArrayList;


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

    private final Map<String, String> MODULE_PLUGIN_RELATION;

    private final List<EventHandlerFilter> FILTER_USERS_CHAIN;
    private final List<EventHandlerFilter> FILTER_GROUP_CHAIN;
    private final List<EventHandlerMonitor> MONITOR_USERS_CHAIN;
    private final List<EventHandlerMonitor> MONITOR_GROUP_CHAIN;
    private final Map<String, EventHandlerExecutor> EXECUTOR_USERS_POOL;
    private final Map<String, EventHandlerExecutor> EXECUTOR_GROUP_POOL;

    public Map<String, Plugin> getPLUGINS() {
        return this.PLUGINS;
    }

    public Map<Runner, Class<? extends EventHandlerRunner>> getCOMPONENT_RUNNER_CLAZZ() {
        return this.COMPONENT_RUNNER_CLAZZ;
    }

    public Map<Filter, Class<? extends EventHandlerFilter>> getCOMPONENT_FILTER_CLAZZ() {
        return this.COMPONENT_FILTER_CLAZZ;
    }

    public Map<Monitor, Class<? extends EventHandlerMonitor>> getCOMPONENT_MONITOR_CLAZZ() {
        return this.COMPONENT_MONITOR_CLAZZ;
    }

    public Map<Executor, Class<? extends EventHandlerExecutor>> getCOMPONENT_EXECUTOR_CLAZZ() {
        return this.COMPONENT_EXECUTOR_CLAZZ;
    }

    public Map<Runner, EventHandlerRunner> getCOMPONENT_RUNNER_INSTANCE() {
        return this.COMPONENT_RUNNER_INSTANCE;
    }

    public Map<Filter, EventHandlerFilter> getCOMPONENT_FILTER_INSTANCE() {
        return this.COMPONENT_FILTER_INSTANCE;
    }

    public Map<Monitor, EventHandlerMonitor> getCOMPONENT_MONITOR_INSTANCE() {
        return this.COMPONENT_MONITOR_INSTANCE;
    }

    public Map<Executor, EventHandlerExecutor> getCOMPONENT_EXECUTOR_INSTANCE() {
        return this.COMPONENT_EXECUTOR_INSTANCE;
    }

    public List<EventHandlerFilter> getFILTER_USERS_CHAIN() {
        return this.FILTER_USERS_CHAIN;
    }

    public List<EventHandlerFilter> getFILTER_GROUP_CHAIN() {
        return this.FILTER_GROUP_CHAIN;
    }

    public List<EventHandlerMonitor> getMONITOR_USERS_CHAIN() {
        return this.MONITOR_USERS_CHAIN;
    }

    public List<EventHandlerMonitor> getMONITOR_GROUP_CHAIN() {
        return this.MONITOR_GROUP_CHAIN;
    }

    public Map<String, EventHandlerExecutor> getEXECUTOR_USERS_POOL() {
        return this.EXECUTOR_USERS_POOL;
    }

    public Map<String, EventHandlerExecutor> getEXECUTOR_GROUP_POOL() {
        return this.EXECUTOR_GROUP_POOL;
    }

    public Schema(File folder) {

        this.folder = folder;

        this.PLUGINS = new ConcurrentHashMap<>();
        this.MODULES = new ConcurrentHashMap<>();

        this.COMPONENT_RUNNER_CLAZZ = new ConcurrentSkipListMap<>(Schema::compare);
        this.COMPONENT_FILTER_CLAZZ = new ConcurrentSkipListMap<>(Schema::compare);
        this.COMPONENT_MONITOR_CLAZZ = new ConcurrentSkipListMap<>(Schema::compare);
        this.COMPONENT_EXECUTOR_CLAZZ = new ConcurrentSkipListMap<>(Schema::compare);

        this.COMPONENT_RUNNER_INSTANCE = new ConcurrentSkipListMap<>(Schema::compare);
        this.COMPONENT_FILTER_INSTANCE = new ConcurrentSkipListMap<>(Schema::compare);
        this.COMPONENT_MONITOR_INSTANCE = new ConcurrentSkipListMap<>(Schema::compare);
        this.COMPONENT_EXECUTOR_INSTANCE = new ConcurrentSkipListMap<>(Schema::compare);

        this.MODULE_PLUGIN_RELATION = new ConcurrentHashMap<>();

        this.FILTER_USERS_CHAIN = new CopyOnWriteArrayList<>();
        this.FILTER_GROUP_CHAIN = new CopyOnWriteArrayList<>();

        this.MONITOR_USERS_CHAIN = new CopyOnWriteArrayList<>();
        this.MONITOR_GROUP_CHAIN = new CopyOnWriteArrayList<>();

        this.EXECUTOR_USERS_POOL = new ConcurrentHashMap<>();
        this.EXECUTOR_GROUP_POOL = new ConcurrentHashMap<>();
    }


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

        this.logger.info("扫描到" + this.PLUGINS.size() + "个插件");

    }


    public void load() {

        this.logger.hint("扫描模块 " + this.PLUGINS.size());

        // module name -> plugin name
        Map<String, String> pluginNameCache = new HashMap<>();

        // command -> plugin name
        Map<String, String> commandCache = new HashMap<>();

        // command -> executor class
        Map<String, Class<? extends EventHandlerExecutor>> commandClassCache = new HashMap<>();


        for (Map.Entry<String, Plugin> pluginEntry : this.PLUGINS.entrySet()) {

            var pluginName = pluginEntry.getKey();
            var pluginItem = pluginEntry.getValue();

            if (pluginItem.getModules().isEmpty()) {
                this.logger.warning("插件包内不含任何模块 " + pluginName);
                continue;
            }


            // =====================================================================================================
            // 检查冲突


            for (Map.Entry<String, Class<? extends AbstractEventHandler>> moduleEntry : pluginItem.getModules().entrySet()) {
                var moduleName = moduleEntry.getKey();
                var moduleItem = moduleEntry.getValue();
                if (this.MODULES.containsKey(moduleName)) {
                    Class<? extends AbstractEventHandler> exist = this.MODULES.get(moduleName);
                    throw new ScanException("发现模块名冲突 " + pluginName + ":" + moduleItem.getName() + "与" + pluginNameCache.get(moduleName) + ":" + exist.getName());
                }
                pluginNameCache.put(moduleName, pluginName);
                this.MODULES.put(moduleName, moduleItem);
            }

            for (Map.Entry<Executor, Class<? extends EventHandlerExecutor>> entry : pluginItem.getExecutorClassMap().entrySet()) {
                var k = entry.getKey();
                var v = entry.getValue();
                String command = k.command();
                if (commandCache.containsKey(command)) {
                    String existPluginName = commandCache.get(command);
                    Class<? extends EventHandlerExecutor> exist = commandClassCache.get(command);
                    throw new ScanException("发现命令冲突 " + command + " - " + pluginName + ":" + v.getName() + "已注册为" + existPluginName + ":" + exist.getName());
                }
                commandCache.put(command, pluginName);
                commandClassCache.put(command, v);
            }


            // =====================================================================================================
            // 归集模块


            for (Map.Entry<Runner, Class<? extends EventHandlerRunner>> entry : pluginItem.getRunnerClassMap().entrySet()) {
                var k = entry.getKey();
                var v = entry.getValue();
                this.COMPONENT_RUNNER_CLAZZ.put(k, v);
                this.MODULE_PLUGIN_RELATION.put(k.value(), pluginName);
                this.logger.info("注册定时器 " + pluginName + ":" + k.value() + "[" + k.priority() + "] -> " + v.getName());
            }

            for (Map.Entry<Filter, Class<? extends EventHandlerFilter>> entry : pluginItem.getFilterClassMap().entrySet()) {
                var k = entry.getKey();
                var v = entry.getValue();
                this.COMPONENT_FILTER_CLAZZ.put(k, v);
                this.MODULE_PLUGIN_RELATION.put(k.value(), pluginName);
                this.logger.info("注册过滤器 " + pluginName + ":" + k.value() + "[" + k.priority() + "] -> " + v.getName());
            }

            for (Map.Entry<Monitor, Class<? extends EventHandlerMonitor>> entry : pluginItem.getMonitorClassMap().entrySet()) {
                var k = entry.getKey();
                var v = entry.getValue();
                this.COMPONENT_MONITOR_CLAZZ.put(k, v);
                this.MODULE_PLUGIN_RELATION.put(k.value(), pluginName);
                this.logger.info("注册监听器 " + pluginName + ":" + k.value() + "[" + k.priority() + "] -> " + v.getName());
            }

            for (Map.Entry<Executor, Class<? extends EventHandlerExecutor>> entry : pluginItem.getExecutorClassMap().entrySet()) {
                var k = entry.getKey();
                var v = entry.getValue();
                this.COMPONENT_EXECUTOR_CLAZZ.put(k, v);
                this.MODULE_PLUGIN_RELATION.put(k.value(), pluginName);
                this.logger.info("注册执行器 " + pluginName + ":" + k.value() + "[" + k.command() + "] -> " + v.getName());
            }
        }
    }


    @SuppressWarnings("deprecation")
    public void make() {

        this.logger.hint("加载定时器 " + this.COMPONENT_RUNNER_CLAZZ.size());

        for (Map.Entry<Runner, Class<? extends EventHandlerRunner>> entry : this.COMPONENT_RUNNER_CLAZZ.entrySet()) {
            var k = entry.getKey();
            var v = entry.getValue();
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

        this.logger.hint("加载过滤器 " + this.COMPONENT_FILTER_CLAZZ.size());

        for (Map.Entry<Filter, Class<? extends EventHandlerFilter>> entry : this.COMPONENT_FILTER_CLAZZ.entrySet()) {
            var k = entry.getKey();
            var v = entry.getValue();
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

        this.logger.hint("加载监听器 " + this.COMPONENT_MONITOR_CLAZZ.size());

        for (Map.Entry<Monitor, Class<? extends EventHandlerMonitor>> entry : this.COMPONENT_MONITOR_CLAZZ.entrySet()) {
            var k = entry.getKey();
            var v = entry.getValue();
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

        this.logger.hint("加载执行器 " + this.COMPONENT_EXECUTOR_CLAZZ.size());

        for (Map.Entry<Executor, Class<? extends EventHandlerExecutor>> entry : this.COMPONENT_EXECUTOR_CLAZZ.entrySet()) {
            var k = entry.getKey();
            var v = entry.getValue();
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

    }


    public void init() {

        this.logger.hint("预载定时器");

        for (Map.Entry<Runner, EventHandlerRunner> entry : this.COMPONENT_RUNNER_INSTANCE.entrySet()) {
            var k = entry.getKey();
            var v = entry.getValue();
            this.logger.info("预载定时器 " + k.value() + "[" + k.priority() + "] -> " + v.getClass().getName());
            try {
                v.initWrapper();
            } catch (Exception exception) {
                throw new BootException("预载定时器失败 " + this.MODULE_PLUGIN_RELATION.get(k.value()) + ":" + k.value() + " -> " + v.getClass().getName(), exception);
            }
        }

        this.logger.hint("预载过滤器");

        for (Map.Entry<Filter, EventHandlerFilter> entry : this.COMPONENT_FILTER_INSTANCE.entrySet()) {
            var k = entry.getKey();
            var v = entry.getValue();
            this.logger.info("预载过滤器 " + k.value() + "[" + k.priority() + "] -> " + v.getClass().getName());
            try {
                v.initWrapper();
            } catch (Exception exception) {
                throw new BootException("预载过滤器失败 " + this.MODULE_PLUGIN_RELATION.get(k.value()) + ":" + k.value() + " -> " + v.getClass().getName(), exception);
            }
        }

        this.logger.hint("预载监听器");

        for (Map.Entry<Monitor, EventHandlerMonitor> entry : this.COMPONENT_MONITOR_INSTANCE.entrySet()) {
            var k = entry.getKey();
            var v = entry.getValue();
            this.logger.info("预载监听器 " + k.value() + "[" + k.priority() + "] -> " + v.getClass().getName());
            try {
                v.initWrapper();
            } catch (Exception exception) {
                throw new BootException("预载监听器失败 " + this.MODULE_PLUGIN_RELATION.get(k.value()) + ":" + k.value() + " -> " + v.getClass().getName(), exception);
            }
        }

        this.logger.hint("预载执行器");

        for (Map.Entry<Executor, EventHandlerExecutor> entry : this.COMPONENT_EXECUTOR_INSTANCE.entrySet()) {
            var k = entry.getKey();
            var v = entry.getValue();
            this.logger.info("预载执行器 " + k.value() + "[" + k.command() + "] -> " + v.getClass().getName());
            try {
                v.initWrapper();
            } catch (Exception exception) {
                throw new BootException("预载执行器失败 " + this.MODULE_PLUGIN_RELATION.get(k.value()) + ":" + k.value() + " -> " + v.getClass().getName(), exception);
            }
        }

    }

    public void boot() {

        this.logger.hint("启动定时器");

        for (Map.Entry<Runner, EventHandlerRunner> entry : this.COMPONENT_RUNNER_INSTANCE.entrySet()) {
            var k = entry.getKey();
            var v = entry.getValue();
            this.logger.info("启动定时器 " + k.value() + "[" + k.priority() + "] -> " + v.getClass().getName());
            try {
                v.bootWrapper();
            } catch (Exception exception) {
                throw new BootException("启动定时器失败 " + this.MODULE_PLUGIN_RELATION.get(k.value()) + ":" + k.value() + " -> " + v.getClass().getName(), exception);
            }
        }

        this.logger.hint("启动过滤器");

        for (Map.Entry<Filter, EventHandlerFilter> entry : this.COMPONENT_FILTER_INSTANCE.entrySet()) {
            var k = entry.getKey();
            var v = entry.getValue();
            this.logger.info("启动过滤器 " + k.value() + "[" + k.priority() + "] -> " + v.getClass().getName());
            try {
                v.bootWrapper();
            } catch (Exception exception) {
                throw new BootException("启动过滤器失败 " + this.MODULE_PLUGIN_RELATION.get(k.value()) + ":" + k.value() + " -> " + v.getClass().getName(), exception);
            }
        }

        this.logger.hint("启动监听器");

        for (Map.Entry<Monitor, EventHandlerMonitor> entry : this.COMPONENT_MONITOR_INSTANCE.entrySet()) {
            var k = entry.getKey();
            var v = entry.getValue();
            this.logger.info("启动监听器 " + k.value() + "[" + k.priority() + "] -> " + v.getClass().getName());
            try {
                v.bootWrapper();
            } catch (Exception exception) {
                throw new BootException("启动监听器失败 " + this.MODULE_PLUGIN_RELATION.get(k.value()) + ":" + k.value() + " -> " + v.getClass().getName(), exception);
            }
        }

        this.logger.hint("启动执行器");

        for (Map.Entry<Executor, EventHandlerExecutor> entry : this.COMPONENT_EXECUTOR_INSTANCE.entrySet()) {
            var k = entry.getKey();
            var v = entry.getValue();
            this.logger.info("启动执行器 " + k.value() + "[" + k.command() + "] -> " + v.getClass().getName());
            try {
                v.bootWrapper();
            } catch (Exception exception) {
                throw new BootException("启动执行器失败 " + this.MODULE_PLUGIN_RELATION.get(k.value()) + ":" + k.value() + " -> " + v.getClass().getName(), exception);
            }
        }
    }

    public void shut() {

        this.logger.hint("关闭执行器");

        ArrayList<Executor> executors = new ArrayList<>(this.COMPONENT_EXECUTOR_INSTANCE.keySet());
        Collections.reverse(executors);
        for (Executor k : executors) {
            EventHandlerExecutor v = this.COMPONENT_EXECUTOR_INSTANCE.get(k);
            this.logger.info("关闭执行器 " + k.value() + "[" + k.command() + "] -> " + v.getClass().getName());
            try {
                v.bootWrapper();
            } catch (Exception exception) {
                this.logger.warning("关闭执行器失败 " + k.value() + " -> " + v.getClass().getName(), exception);
            }
        }

        this.logger.hint("关闭监听器");

        ArrayList<Monitor> monitors = new ArrayList<>(this.COMPONENT_MONITOR_INSTANCE.keySet());
        Collections.reverse(monitors);
        for (Monitor k : monitors) {
            EventHandlerMonitor v = this.COMPONENT_MONITOR_INSTANCE.get(k);
            this.logger.info("关闭监听器 " + k.value() + "[" + k.priority() + "] -> " + v.getClass().getName());
            try {
                v.bootWrapper();
            } catch (Exception exception) {
                this.logger.warning("关闭监听器失败 " + k.value() + " -> " + v.getClass().getName(), exception);
            }
        }

        this.logger.hint("关闭过滤器");

        ArrayList<Filter> filters = new ArrayList<>(this.COMPONENT_FILTER_INSTANCE.keySet());
        Collections.reverse(monitors);
        for (Filter k : filters) {
            EventHandlerFilter v = this.COMPONENT_FILTER_INSTANCE.get(k);
            this.logger.info("关闭过滤器 " + k.value() + "[" + k.priority() + "] -> " + v.getClass().getName());
            try {
                v.bootWrapper();
            } catch (Exception exception) {
                this.logger.warning("关闭过滤器失败 " + k.value() + " -> " + v.getClass().getName(), exception);
            }
        }

        this.logger.hint("关闭定时器");

        ArrayList<Runner> runners = new ArrayList<>(this.COMPONENT_RUNNER_INSTANCE.keySet());
        Collections.reverse(runners);
        for (Runner k : runners) {
            EventHandlerRunner v = this.COMPONENT_RUNNER_INSTANCE.get(k);
            this.logger.info("关闭定时器 " + k.value() + "[" + k.priority() + "] -> " + v.getClass().getName());
            try {
                v.bootWrapper();
            } catch (Exception exception) {
                this.logger.warning("关闭定时器失败 " + k.value() + " -> " + v.getClass().getName(), exception);
            }
        }
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

    private static int compare(Executor o1, Executor o2) {
        return CharSequence.compare(o1.command(), o2.command());
    }


}