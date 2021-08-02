package studio.blacktech.furryblackplus.core.define.schema;

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
import java.util.ArrayList;
import java.util.Collections;
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


    public Set<String> listAllPlugin() {
        return this.PLUGINS.keySet();
    }

    public void unloadPlugin(String name) {
        System.out.println("This is TODO " + name);
    }

    public void reloadPlugin(String name) {
        System.out.println("This is TODO " + name);
    }

    public Map<String, Boolean> listAllModule() {

        Map<String, Boolean> result = new LinkedHashMap<>();

        for (Runner annotation : this.COMPONENT_RUNNER_CLAZZ.keySet()) {
            if (this.COMPONENT_RUNNER_INSTANCE.containsKey(annotation)) {
                result.put(annotation.value(), true);
            } else {
                result.put(annotation.value(), false);
            }
        }

        for (Filter annotation : this.COMPONENT_FILTER_CLAZZ.keySet()) {
            if (this.COMPONENT_FILTER_CLAZZ.containsKey(annotation)) {
                result.put(annotation.value(), true);
            } else {
                result.put(annotation.value(), false);
            }
        }

        for (Monitor annotation : this.COMPONENT_MONITOR_CLAZZ.keySet()) {
            if (this.COMPONENT_MONITOR_INSTANCE.containsKey(annotation)) {
                result.put(annotation.value(), true);
            } else {
                result.put(annotation.value(), false);
            }
        }

        for (Executor annotation : this.COMPONENT_EXECUTOR_CLAZZ.keySet()) {
            if (this.COMPONENT_EXECUTOR_INSTANCE.containsKey(annotation)) {
                result.put(annotation.value(), true);
            } else {
                result.put(annotation.value(), false);
            }
        }

        return result;
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


    @SuppressWarnings("unchecked")
    public <T extends EventHandlerRunner> T getRunner(Class<T> clazz) {
        List<EventHandlerRunner> collect = this.COMPONENT_RUNNER_INSTANCE.values().stream().filter(clazz::isInstance).collect(Collectors.toUnmodifiableList());
        if (collect.size() == 1) {
            return (T) collect.get(0);
        } else {
            return null;
        }
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
                this.logger.info("预载执行器 -> " + clazz.getName() + ":" + newInstance.hashCode());
                newInstance.initWrapper();
                this.logger.info("关闭执行器 -> " + clazz.getName() + ":" + newInstance.hashCode());
                newInstance.bootWrapper();
                this.COMPONENT_EXECUTOR_INSTANCE.put(annotation, newInstance);
                return;
            }
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


    public String generateUsersExecutorList() {
        if (this.EXECUTOR_USERS_POOL.size() == 0) {
            return "没有任何已装载的命令";
        }
        StringBuilder builder = new StringBuilder();
        for (Executor executor : this.COMPONENT_EXECUTOR_INSTANCE.keySet()) {
            if (!this.EXECUTOR_USERS_POOL.containsKey(executor.command())) {
                continue;
            }
            builder.append(executor.command());
            builder.append(" ");
            builder.append(executor.value());
            builder.append(" ");
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
            builder.append(executor.command());
            builder.append(" ");
            builder.append(executor.value());
            builder.append(" ");
            builder.append(executor.description());
            builder.append("\r\n");
        }
        builder.setLength(builder.length() - 2);
        return builder.toString();
    }

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

        this.COMMAND_EXECUTOR_RELATION = new ConcurrentHashMap<>();
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

    }


    public void load() {

        this.logger.hint("扫描模块 " + this.PLUGINS.size());

        for (String name : this.PLUGINS.keySet()) {
            this.load(name);
        }
    }


    public void load(String pluginName) {

        Plugin pluginItem = this.PLUGINS.get(pluginName);

        if (pluginItem.getModules().isEmpty()) {
            this.logger.warning("插件包内不含任何模块 " + pluginName);
            return;
        }

        this.logger.info("模块冲突检查 -> " + pluginName);

        // =====================================================================================================
        // 检查冲突


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


        // =====================================================================================================
        // 归集模块


        this.logger.info("模块检查通过 -> " + pluginName);


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
                v.shutWrapper();
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
                v.shutWrapper();
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
                v.shutWrapper();
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
                v.shutWrapper();
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