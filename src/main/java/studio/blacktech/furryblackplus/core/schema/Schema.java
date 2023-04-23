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

import static studio.blacktech.furryblack.core.enhance.Enhance.hexHash;

@SuppressWarnings("unused")

public final class Schema {

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

  //= ================================================================================================================
  //=
  //= 构造
  //=
  //= ================================================================================================================

  public Schema(File folder) {

    this.folder = folder;

    logger.hint("加载插件模型");

    plugins = new HashMap<>();
    modules = new HashMap<>();

    COMPONENT_RUNNER_CLAZZ = new HashMap<>();
    COMPONENT_FILTER_CLAZZ = new HashMap<>();
    COMPONENT_MONITOR_CLAZZ = new HashMap<>();
    COMPONENT_CHECKER_CLAZZ = new HashMap<>();
    COMPONENT_EXECUTOR_CLAZZ = new TreeMap<>(Schema::compare);

    SORTED_RUNNER = new LinkedList<>();
    SORTED_FILTER = new LinkedList<>();
    SORTED_MONITOR = new LinkedList<>();
    SORTED_CHECKER = new LinkedList<>();

    COMPONENT_RUNNER_INSTANCE = new ConcurrentHashMap<>();
    COMPONENT_FILTER_INSTANCE = new ConcurrentHashMap<>();
    COMPONENT_MONITOR_INSTANCE = new ConcurrentHashMap<>();
    COMPONENT_CHECKER_INSTANCE = new ConcurrentHashMap<>();
    COMPONENT_EXECUTOR_INSTANCE = new ConcurrentSkipListMap<>(Schema::compare);

    COMMAND_EXECUTOR_RELATION = new HashMap<>();
    MODULE_PLUGIN_RELATION = new HashMap<>();

    FILTER_USERS_CHAIN = new CopyOnWriteArrayList<>();
    FILTER_GROUP_CHAIN = new CopyOnWriteArrayList<>();

    MONITOR_USERS_CHAIN = new CopyOnWriteArrayList<>();
    MONITOR_GROUP_CHAIN = new CopyOnWriteArrayList<>();

    EXECUTOR_USERS_POOL = new ConcurrentHashMap<>();
    EXECUTOR_GROUP_POOL = new ConcurrentHashMap<>();

    GLOBAL_CHECKER_USERS_POOL = new CopyOnWriteArrayList<>();
    GLOBAL_CHECKER_GROUP_POOL = new CopyOnWriteArrayList<>();

    COMMAND_CHECKER_USERS_POOL = new ConcurrentHashMap<>();
    COMMAND_CHECKER_GROUP_POOL = new ConcurrentHashMap<>();

  }

  //= ================================================================================================================
  //=
  //= 核心功能
  //=
  //= ================================================================================================================

  //= ================================================================================================================
  //= 反转控制
  //= ================================================================================================================

  @SuppressWarnings("unchecked")
  public <T extends EventHandlerRunner> T getRunner(Class<T> clazz) {
    List<EventHandlerRunner> collect = COMPONENT_RUNNER_INSTANCE.values().stream().filter(clazz::isInstance).toList();
    if (collect.size() == 1) {
      return (T) collect.get(0);
    } else {
      return null;
    }
  }

  //= ================================================================================================================
  //= 生成信息
  //= ================================================================================================================

  public String generateUsersExecutorList() {
    if (EXECUTOR_USERS_POOL.size() == 0) {
      return "没有任何已装载的命令";
    }
    StringBuilder builder = new StringBuilder();
    for (Executor executor : COMPONENT_EXECUTOR_INSTANCE.keySet()) {
      if (!EXECUTOR_USERS_POOL.containsKey(executor.command())) {
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
    if (EXECUTOR_GROUP_POOL.size() == 0) {
      return "没有任何已装载的命令";
    }
    StringBuilder builder = new StringBuilder();
    for (Executor executor : COMPONENT_EXECUTOR_INSTANCE.keySet()) {
      if (!EXECUTOR_GROUP_POOL.containsKey(executor.command())) {
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

  //= ================================================================================================================
  //= 处理系统
  //= ================================================================================================================

  public List<EventHandlerFilter> getFilterUsersChain() {
    return FILTER_USERS_CHAIN;
  }

  public List<EventHandlerFilter> getFilterGroupChain() {
    return FILTER_GROUP_CHAIN;
  }

  public List<EventHandlerMonitor> getMonitorUsersChain() {
    return MONITOR_USERS_CHAIN;
  }

  public List<EventHandlerMonitor> getMonitorGroupChain() {
    return MONITOR_GROUP_CHAIN;
  }

  public Map<String, EventHandlerExecutor> getExecutorUsersPool() {
    return EXECUTOR_USERS_POOL;
  }

  public Map<String, EventHandlerExecutor> getExecutorGroupPool() {
    return EXECUTOR_GROUP_POOL;
  }

  public List<EventHandlerChecker> getGlobalCheckerUsersPool() {
    return GLOBAL_CHECKER_USERS_POOL;
  }

  public List<EventHandlerChecker> getGlobalCheckerGroupPool() {
    return GLOBAL_CHECKER_GROUP_POOL;
  }

  public List<EventHandlerChecker> getCommandCheckerUsersPool(String name) {
    return COMMAND_CHECKER_USERS_POOL.get(name);
  }

  public List<EventHandlerChecker> getCommandCheckerGroupPool(String name) {
    return COMMAND_CHECKER_GROUP_POOL.get(name);
  }

  //= ================================================================================================================
  //=
  //= 模块承载
  //=
  //= ================================================================================================================

  //= ================================================================================================================
  //=  扫描插件
  //= ================================================================================================================

  public void scanPlugin() {

    logger.hint("扫描插件目录");

    File[] listFiles = folder.listFiles();

    if (listFiles == null) {
      throw new ScanException("无法扫描模块");
    }

    if (listFiles.length == 0) {
      logger.warning("插件目录为空");
    }

    logger.seek("发现[" + listFiles.length + "]个文件");
    for (File file : listFiles) {
      logger.info("尝试加载 -> " + file.getName());
      Plugin plugin = Plugin.load(file);
      String name = plugin.getName();
      if (plugins.containsKey(name)) {
        Plugin exist = plugins.get(name);
        throw new ScanException("发现插件名称冲突 " + plugin.getFile().getAbsolutePath() + "名称" + name + "已被注册" + exist.getFile().getAbsolutePath());
      }
      plugins.put(name, plugin);
    }

    logger.seek("发现[" + plugins.size() + "]个插件");
    for (Plugin plugin : plugins.values()) {
      logger.info(plugin.getFile().getName() + " -> " + plugin.getName());
    }
  }

  //= ================================================================================================================
  //=  扫描模块
  //= ================================================================================================================

  public void scanModule() {
    logger.hint("扫描插件包内容");
    plugins.values().forEach(Plugin::scan);
  }

  //= ================================================================================================================
  //=  注册模块
  //= ================================================================================================================

  public void loadModule() {

    logger.hint("向插件模型注册模块");

    for (Map.Entry<String, Plugin> pluginEntry : plugins.entrySet()) {

      var pluginName = pluginEntry.getKey();
      var pluginPackage = pluginEntry.getValue();

      logger.seek("尝试注册插件 -> " + pluginName);

      if (pluginPackage.getModules().isEmpty()) {
        logger.warning("插件包内不含任何模块 " + pluginName);
        return;
      }

      logger.info("模块冲突检查 -> " + pluginName);

      for (Map.Entry<Runner, Class<? extends EventHandlerRunner>> moduleEntry : pluginPackage.getRunnerClassMap().entrySet()) {
        var k = moduleEntry.getKey();
        var v = moduleEntry.getValue();
        if (COMPONENT_RUNNER_CLAZZ.containsKey(k)) {
          Class<? extends AbstractEventHandler> exist = COMPONENT_RUNNER_CLAZZ.get(k);
          throw new ScanException("发现模块名冲突 " + pluginName + ":" + v.getName() + "与" + COMPONENT_RUNNER_CLAZZ.get(k) + ":" + exist.getName());
        }
      }

      for (Map.Entry<Filter, Class<? extends EventHandlerFilter>> moduleEntry : pluginPackage.getFilterClassMap().entrySet()) {
        var k = moduleEntry.getKey();
        var v = moduleEntry.getValue();
        if (COMPONENT_FILTER_CLAZZ.containsKey(k)) {
          Class<? extends AbstractEventHandler> exist = COMPONENT_FILTER_CLAZZ.get(k);
          throw new ScanException("发现模块名冲突 " + pluginName + ":" + v.getName() + "与" + COMPONENT_FILTER_CLAZZ.get(k) + ":" + exist.getName());
        }
      }

      for (Map.Entry<Monitor, Class<? extends EventHandlerMonitor>> moduleEntry : pluginPackage.getMonitorClassMap().entrySet()) {
        var k = moduleEntry.getKey();
        var v = moduleEntry.getValue();
        if (COMPONENT_MONITOR_CLAZZ.containsKey(k)) {
          Class<? extends AbstractEventHandler> exist = COMPONENT_MONITOR_CLAZZ.get(k);
          throw new ScanException("发现模块名冲突 " + pluginName + ":" + v.getName() + "与" + COMPONENT_MONITOR_CLAZZ.get(k) + ":" + exist.getName());
        }
      }

      for (Map.Entry<Checker, Class<? extends EventHandlerChecker>> moduleEntry : pluginPackage.getCheckerClassMap().entrySet()) {
        var k = moduleEntry.getKey();
        var v = moduleEntry.getValue();
        if (COMPONENT_CHECKER_CLAZZ.containsKey(k)) {
          Class<? extends AbstractEventHandler> exist = COMPONENT_CHECKER_CLAZZ.get(k);
          throw new ScanException("发现模块名冲突 " + pluginName + ":" + v.getName() + "与" + COMPONENT_CHECKER_CLAZZ.get(k) + ":" + exist.getName());
        }
      }

      for (Map.Entry<Executor, Class<? extends EventHandlerExecutor>> moduleEntry : pluginPackage.getExecutorClassMap().entrySet()) {
        var k = moduleEntry.getKey();
        var v = moduleEntry.getValue();
        if (COMPONENT_EXECUTOR_CLAZZ.containsKey(k)) {
          Class<? extends AbstractEventHandler> exist = COMPONENT_EXECUTOR_CLAZZ.get(k);
          throw new ScanException("发现模块名冲突 " + pluginName + ":" + v.getName() + "与" + COMPONENT_EXECUTOR_CLAZZ.get(k) + ":" + exist.getName());
        }
      }

      for (Map.Entry<Executor, Class<? extends EventHandlerExecutor>> entry : pluginPackage.getExecutorClassMap().entrySet()) {
        var k = entry.getKey();
        var v = entry.getValue();
        String command = k.command();
        if (COMMAND_EXECUTOR_RELATION.containsKey(command)) {
          Executor annotation = COMMAND_EXECUTOR_RELATION.get(command);
          Class<? extends EventHandlerExecutor> exist = COMPONENT_EXECUTOR_CLAZZ.get(annotation);
          String existPluginName = MODULE_PLUGIN_RELATION.get(annotation.value());
          throw new ScanException("发现命令冲突 " + command + " - " + pluginName + ":" + v.getName() + "已注册为" + existPluginName + ":" + exist.getName());
        }
      }

      logger.info("冲突检查通过 -> " + pluginName);

      for (Map.Entry<Runner, Class<? extends EventHandlerRunner>> entry : pluginPackage.getRunnerClassMap().entrySet()) {
        var k = entry.getKey();
        var v = entry.getValue();
        String moduleName = k.value();
        modules.put(moduleName, v);
        SORTED_RUNNER.add(k);
        COMPONENT_RUNNER_CLAZZ.put(k, v);
        MODULE_PLUGIN_RELATION.put(moduleName, pluginName);
        logger.info("注册定时器" + pluginName + ":" + moduleName + "[" + k.priority() + "] -> " + v.getName());
      }

      for (Map.Entry<Filter, Class<? extends EventHandlerFilter>> entry : pluginPackage.getFilterClassMap().entrySet()) {
        var k = entry.getKey();
        var v = entry.getValue();
        String moduleName = k.value();
        modules.put(moduleName, v);
        SORTED_FILTER.add(k);
        COMPONENT_FILTER_CLAZZ.put(k, v);
        MODULE_PLUGIN_RELATION.put(moduleName, pluginName);
        logger.info("注册过滤器" + pluginName + ":" + moduleName + "[" + k.priority() + "] -> " + v.getName());
      }

      for (Map.Entry<Monitor, Class<? extends EventHandlerMonitor>> entry : pluginPackage.getMonitorClassMap().entrySet()) {
        var k = entry.getKey();
        var v = entry.getValue();
        String moduleName = k.value();
        modules.put(moduleName, v);
        SORTED_MONITOR.add(k);
        COMPONENT_MONITOR_CLAZZ.put(k, v);
        MODULE_PLUGIN_RELATION.put(moduleName, pluginName);
        logger.info("注册监听器" + pluginName + ":" + moduleName + "[" + k.priority() + "] -> " + v.getName());
      }

      for (Map.Entry<Checker, Class<? extends EventHandlerChecker>> entry : pluginPackage.getCheckerClassMap().entrySet()) {
        var k = entry.getKey();
        var v = entry.getValue();
        String moduleName = k.value();
        modules.put(moduleName, v);
        SORTED_CHECKER.add(k);
        COMPONENT_CHECKER_CLAZZ.put(k, v);
        MODULE_PLUGIN_RELATION.put(moduleName, pluginName);
        logger.info("注册检查器" + pluginName + ":" + moduleName + "[" + k.priority() + "] -> " + v.getName());
      }

      for (Map.Entry<Executor, Class<? extends EventHandlerExecutor>> entry : pluginPackage.getExecutorClassMap().entrySet()) {
        var k = entry.getKey();
        var v = entry.getValue();
        String moduleName = k.value();
        modules.put(moduleName, v);
        COMMAND_EXECUTOR_RELATION.put(k.command(), k);
        COMPONENT_EXECUTOR_CLAZZ.put(k, v);
        MODULE_PLUGIN_RELATION.put(moduleName, pluginName);
        logger.info("注册执行器" + pluginName + ":" + moduleName + "[" + k.command() + "] -> " + v.getName());
      }
    }

    SORTED_RUNNER.sort(Schema::compare);
    SORTED_FILTER.sort(Schema::compare);
    SORTED_MONITOR.sort(Schema::compare);
    SORTED_CHECKER.sort(Schema::compare);

  }

  //= ================================================================================================================
  //=  创建模块
  //= ================================================================================================================

  public void makeModule() {

    logger.hint("加载定时器 " + COMPONENT_RUNNER_CLAZZ.size());

    for (Runner annotation : SORTED_RUNNER) {
      Class<? extends EventHandlerRunner> clazz = COMPONENT_RUNNER_CLAZZ.get(annotation);
      String moduleName = annotation.value();
      String pluginName = MODULE_PLUGIN_RELATION.get(moduleName);
      Plugin plugin = plugins.get(pluginName);
      URLClassLoader dependClassLoader = plugin.getDependClassLoader();
      logger.info("加载定时器" + pluginName + ":" + moduleName + "[" + annotation.priority() + "] -> " + clazz.getName());
      EventHandlerRunner instance;
      try {
        instance = clazz.getConstructor().newInstance();
        instance.internalInit(pluginName, moduleName, dependClassLoader);
      } catch (InvocationTargetException | InstantiationException | IllegalAccessException | NoSuchMethodException e) {
        throw new LoadException("加载定时器失败 " + pluginName + ":" + moduleName + "[" + annotation.priority() + "] -> " + clazz.getName());
      }

      COMPONENT_RUNNER_INSTANCE.put(annotation, instance);
    }

    logger.hint("加载过滤器 " + COMPONENT_FILTER_CLAZZ.size());

    for (Filter annotation : SORTED_FILTER) {
      Class<? extends EventHandlerFilter> clazz = COMPONENT_FILTER_CLAZZ.get(annotation);
      String moduleName = annotation.value();
      String pluginName = MODULE_PLUGIN_RELATION.get(moduleName);
      Plugin plugin = plugins.get(pluginName);
      URLClassLoader dependClassLoader = plugin.getDependClassLoader();
      logger.info("加载过滤器" + pluginName + ":" + moduleName + "[" + annotation.priority() + "] -> " + clazz.getName());
      EventHandlerFilter instance;
      try {
        instance = clazz.getConstructor().newInstance();
        instance.internalInit(pluginName, moduleName, dependClassLoader);
      } catch (InvocationTargetException | InstantiationException | IllegalAccessException | NoSuchMethodException e) {
        throw new LoadException("加载过滤器失败 " + MODULE_PLUGIN_RELATION.get(moduleName) + ":" + moduleName + " " + clazz.getName());
      }
      COMPONENT_FILTER_INSTANCE.put(annotation, instance);
      if (annotation.users()) FILTER_USERS_CHAIN.add(instance);
      if (annotation.group()) FILTER_GROUP_CHAIN.add(instance);
    }

    logger.hint("加载监听器 " + COMPONENT_MONITOR_CLAZZ.size());

    for (Monitor annotation : SORTED_MONITOR) {
      Class<? extends EventHandlerMonitor> clazz = COMPONENT_MONITOR_CLAZZ.get(annotation);
      String moduleName = annotation.value();
      String pluginName = MODULE_PLUGIN_RELATION.get(moduleName);
      Plugin plugin = plugins.get(pluginName);
      URLClassLoader dependClassLoader = plugin.getDependClassLoader();
      logger.info("加载监听器" + pluginName + ":" + moduleName + "[" + annotation.priority() + "] -> " + clazz.getName());
      EventHandlerMonitor instance;
      try {
        instance = clazz.getConstructor().newInstance();
        instance.internalInit(pluginName, moduleName, dependClassLoader);
      } catch (InvocationTargetException | InstantiationException | IllegalAccessException | NoSuchMethodException e) {
        throw new LoadException("加载监听器失败 " + MODULE_PLUGIN_RELATION.get(moduleName) + ":" + moduleName + " " + clazz.getName());
      }
      COMPONENT_MONITOR_INSTANCE.put(annotation, instance);
      if (annotation.users()) MONITOR_USERS_CHAIN.add(instance);
      if (annotation.group()) MONITOR_GROUP_CHAIN.add(instance);
    }

    logger.hint("加载检查器 " + COMPONENT_CHECKER_CLAZZ.size());

    for (Checker annotation : SORTED_CHECKER) {
      Class<? extends EventHandlerChecker> clazz = COMPONENT_CHECKER_CLAZZ.get(annotation);
      String moduleName = annotation.value();
      String pluginName = MODULE_PLUGIN_RELATION.get(moduleName);
      Plugin plugin = plugins.get(pluginName);
      URLClassLoader dependClassLoader = plugin.getDependClassLoader();
      logger.info("加载检查器" + pluginName + ":" + moduleName + "[" + annotation.priority() + "] -> " + clazz.getName());
      EventHandlerChecker instance;
      try {
        instance = clazz.getConstructor().newInstance();
        instance.internalInit(pluginName, moduleName, dependClassLoader);
      } catch (InvocationTargetException | InstantiationException | IllegalAccessException | NoSuchMethodException e) {
        throw new LoadException("加载检查器失败 " + MODULE_PLUGIN_RELATION.get(moduleName) + ":" + moduleName + " " + clazz.getName());
      }
      COMPONENT_CHECKER_INSTANCE.put(annotation, instance);
      if (annotation.command().equals("*")) {
        if (annotation.users()) GLOBAL_CHECKER_USERS_POOL.add(instance);
        if (annotation.group()) GLOBAL_CHECKER_GROUP_POOL.add(instance);
      } else {
        if (annotation.users()) {
          List<EventHandlerChecker> checkerList = COMMAND_CHECKER_USERS_POOL.computeIfAbsent(annotation.command(), k1 -> new CopyOnWriteArrayList<>());
          checkerList.add(instance);
          checkerList.sort((o1, o2) -> {
            Checker o1Annotation = o1.getClass().getAnnotation(Checker.class);
            Checker o2Annotation = o2.getClass().getAnnotation(Checker.class);
            return o1Annotation.priority() - o2Annotation.priority();
          });
        }
        if (annotation.group()) {
          List<EventHandlerChecker> checkerList = COMMAND_CHECKER_GROUP_POOL.computeIfAbsent(annotation.command(), k1 -> new CopyOnWriteArrayList<>());
          checkerList.add(instance);
          checkerList.sort((o1, o2) -> {
            Checker o1Annotation = o1.getClass().getAnnotation(Checker.class);
            Checker o2Annotation = o2.getClass().getAnnotation(Checker.class);
            return o1Annotation.priority() - o2Annotation.priority();
          });
        }
      }
    }

    logger.hint("加载执行器 " + COMPONENT_EXECUTOR_CLAZZ.size());

    for (Map.Entry<Executor, Class<? extends EventHandlerExecutor>> entry : COMPONENT_EXECUTOR_CLAZZ.entrySet()) {
      Executor annotation = entry.getKey();
      Class<? extends EventHandlerExecutor> clazz = entry.getValue();
      String moduleName = annotation.value();
      String pluginName = MODULE_PLUGIN_RELATION.get(moduleName);
      Plugin plugin = plugins.get(pluginName);
      URLClassLoader dependClassLoader = plugin.getDependClassLoader();
      logger.info("加载执行器" + pluginName + ":" + moduleName + "[" + annotation.command() + "] -> " + clazz.getName());
      EventHandlerExecutor instance;
      try {
        instance = clazz.getConstructor().newInstance();
        instance.internalInit(pluginName, moduleName, dependClassLoader);
        instance.buildHelp(annotation);
      } catch (InvocationTargetException | InstantiationException | IllegalAccessException | NoSuchMethodException e) {
        throw new LoadException("加载执行器失败 " + MODULE_PLUGIN_RELATION.get(moduleName) + ":" + moduleName + " " + clazz.getName());
      }
      COMPONENT_EXECUTOR_INSTANCE.put(annotation, instance);
      if (annotation.users()) EXECUTOR_USERS_POOL.put(annotation.command(), instance);
      if (annotation.group()) EXECUTOR_GROUP_POOL.put(annotation.command(), instance);
    }

  }

  //= ================================================================================================================
  //=  预载模块
  //= ================================================================================================================

  public void initModule() {

    logger.hint("预载定时器");

    for (Runner annotation : SORTED_RUNNER) {
      EventHandlerRunner instance = COMPONENT_RUNNER_INSTANCE.get(annotation);
      logger.info("预载定时器" + annotation.value() + "[" + annotation.priority() + "] -> " + instance.getClass().getName());
      try {
        instance.initWrapper();
      } catch (Exception exception) {
        throw new BootException("预载定时器失败 " + MODULE_PLUGIN_RELATION.get(annotation.value()) + ":" + annotation.value() + " -> " + instance.getClass().getName(), exception);
      }
    }

    logger.hint("预载过滤器");

    for (Filter annotation : SORTED_FILTER) {
      EventHandlerFilter instance = COMPONENT_FILTER_INSTANCE.get(annotation);
      logger.info("预载过滤器" + annotation.value() + "[" + annotation.priority() + "] -> " + instance.getClass().getName());
      try {
        instance.initWrapper();
      } catch (Exception exception) {
        throw new BootException("预载过滤器失败 " + MODULE_PLUGIN_RELATION.get(annotation.value()) + ":" + annotation.value() + " -> " + instance.getClass().getName(), exception);
      }
    }

    logger.hint("预载监听器");

    for (Monitor annotation : SORTED_MONITOR) {
      EventHandlerMonitor instance = COMPONENT_MONITOR_INSTANCE.get(annotation);
      logger.info("预载监听器" + annotation.value() + "[" + annotation.priority() + "] -> " + instance.getClass().getName());
      try {
        instance.initWrapper();
      } catch (Exception exception) {
        throw new BootException("预载监听器失败 " + MODULE_PLUGIN_RELATION.get(annotation.value()) + ":" + annotation.value() + " -> " + instance.getClass().getName(), exception);
      }
    }

    logger.hint("预载检查器");

    for (Checker annotation : SORTED_CHECKER) {
      EventHandlerChecker instance = COMPONENT_CHECKER_INSTANCE.get(annotation);
      logger.info("预载检查器" + annotation.value() + "[" + annotation.priority() + "] -> " + instance.getClass().getName());
      try {
        instance.initWrapper();
      } catch (Exception exception) {
        throw new BootException("预载检查器失败 " + MODULE_PLUGIN_RELATION.get(annotation.value()) + ":" + annotation.value() + " -> " + instance.getClass().getName(), exception);
      }
    }

    logger.hint("预载执行器");

    for (Map.Entry<Executor, EventHandlerExecutor> entry : COMPONENT_EXECUTOR_INSTANCE.entrySet()) {
      Executor annotation = entry.getKey();
      EventHandlerExecutor instance = entry.getValue();
      logger.info("预载执行器" + annotation.value() + "[" + annotation.command() + "] -> " + instance.getClass().getName());
      try {
        instance.initWrapper();
      } catch (Exception exception) {
        throw new BootException("预载执行器失败 " + MODULE_PLUGIN_RELATION.get(annotation.value()) + ":" + annotation.value() + " -> " + instance.getClass().getName(), exception);
      }
    }
  }

  //= ================================================================================================================
  //=  启动模块
  //= ================================================================================================================

  public void bootModule() {

    logger.hint("启动定时器");

    for (Runner annotation : SORTED_RUNNER) {
      EventHandlerRunner clazz = COMPONENT_RUNNER_INSTANCE.get(annotation);
      logger.info("启动定时器" + annotation.value() + "[" + annotation.priority() + "] -> " + clazz.getClass().getName());
      try {
        clazz.bootWrapper();
      } catch (Exception exception) {
        throw new BootException("启动定时器失败 " + MODULE_PLUGIN_RELATION.get(annotation.value()) + ":" + annotation.value() + " -> " + clazz.getClass().getName(), exception);
      }
    }

    logger.hint("启动过滤器");

    for (Filter annotation : SORTED_FILTER) {
      EventHandlerFilter clazz = COMPONENT_FILTER_INSTANCE.get(annotation);
      logger.info("启动过滤器" + annotation.value() + "[" + annotation.priority() + "] -> " + clazz.getClass().getName());
      try {
        clazz.bootWrapper();
      } catch (Exception exception) {
        throw new BootException("启动过滤器失败 " + MODULE_PLUGIN_RELATION.get(annotation.value()) + ":" + annotation.value() + " -> " + clazz.getClass().getName(), exception);
      }
    }

    logger.hint("启动监听器");

    for (Monitor annotation : SORTED_MONITOR) {
      EventHandlerMonitor clazz = COMPONENT_MONITOR_INSTANCE.get(annotation);
      logger.info("启动监听器" + annotation.value() + "[" + annotation.priority() + "] -> " + clazz.getClass().getName());
      try {
        clazz.bootWrapper();
      } catch (Exception exception) {
        throw new BootException("启动监听器失败 " + MODULE_PLUGIN_RELATION.get(annotation.value()) + ":" + annotation.value() + " -> " + clazz.getClass().getName(), exception);
      }
    }

    logger.hint("启动检查器");

    for (Checker annotation : SORTED_CHECKER) {
      EventHandlerChecker clazz = COMPONENT_CHECKER_INSTANCE.get(annotation);
      logger.info("启动检查器" + annotation.value() + "[" + annotation.priority() + "] -> " + clazz.getClass().getName());
      try {
        clazz.bootWrapper();
      } catch (Exception exception) {
        throw new BootException("启动检查器失败 " + MODULE_PLUGIN_RELATION.get(annotation.value()) + ":" + annotation.value() + " -> " + clazz.getClass().getName(), exception);
      }
    }

    logger.hint("启动执行器");

    for (Map.Entry<Executor, EventHandlerExecutor> entry : COMPONENT_EXECUTOR_INSTANCE.entrySet()) {
      Executor annotation = entry.getKey();
      EventHandlerExecutor clazz = entry.getValue();
      logger.info("启动执行器" + annotation.value() + "[" + annotation.command() + "] -> " + clazz.getClass().getName());
      try {
        clazz.bootWrapper();
      } catch (Exception exception) {
        throw new BootException("启动执行器失败 " + MODULE_PLUGIN_RELATION.get(annotation.value()) + ":" + annotation.value() + " -> " + clazz.getClass().getName(), exception);
      }
    }
  }

  //= ================================================================================================================
  //=  关闭模块
  //= ================================================================================================================

  public void shutModule() {

    logger.hint("关闭执行器");

    for (Map.Entry<Executor, EventHandlerExecutor> entry : COMPONENT_EXECUTOR_INSTANCE.entrySet()) {
      Executor annotation = entry.getKey();
      EventHandlerExecutor instance = entry.getValue();
      try {
        if (FurryBlack.isShutModeDrop()) {
          logger.info("丢弃执行器" + annotation.value() + "[" + annotation.command() + "] -> " + instance.getClass().getName());
          Thread thread = new Thread(instance::shutWrapper);
          thread.setDaemon(true);
          thread.start();
        } else {
          logger.info("关闭执行器" + annotation.value() + "[" + annotation.command() + "] -> " + instance.getClass().getName());
          instance.shutWrapper();
        }
      } catch (Exception exception) {
        logger.warning("关闭执行器异常" + annotation.value() + "[" + annotation.command() + "] -> " + instance.getClass().getName(), exception);
      }
    }

    logger.hint("关闭检查器");

    List<Checker> checkers = new ArrayList<>(SORTED_CHECKER);
    Collections.reverse(checkers);
    for (Checker annotation : checkers) {
      EventHandlerChecker instance = COMPONENT_CHECKER_INSTANCE.get(annotation);
      try {
        if (FurryBlack.isShutModeDrop()) {
          logger.info("丢弃检查器" + annotation.value() + "[" + annotation.command() + "/" + annotation.priority() + "] -> " + instance.getClass().getName());
          Thread thread = new Thread(instance::shutWrapper);
          thread.setDaemon(true);
          thread.start();
        } else {
          logger.info("关闭检查器" + annotation.value() + "[" + annotation.command() + "/" + annotation.priority() + "] -> " + instance.getClass().getName());
          instance.shutWrapper();
        }
      } catch (Exception exception) {
        logger.warning("关闭检查器异常" + annotation.value() + "[" + annotation.command() + "/" + annotation.priority() + "] -> " + instance.getClass().getName(), exception);
      }
    }

    logger.hint("关闭监听器");

    List<Monitor> monitors = new ArrayList<>(SORTED_MONITOR);
    Collections.reverse(monitors);
    for (Monitor annotation : monitors) {
      EventHandlerMonitor instance = COMPONENT_MONITOR_INSTANCE.get(annotation);
      try {
        if (FurryBlack.isShutModeDrop()) {
          logger.info("丢弃检查器" + annotation.value() + "[" + annotation.priority() + "] -> " + instance.getClass().getName());
          Thread thread = new Thread(instance::shutWrapper);
          thread.setDaemon(true);
          thread.start();
        } else {
          logger.info("关闭检查器" + annotation.value() + "[" + annotation.priority() + "] -> " + instance.getClass().getName());
          instance.shutWrapper();
        }
      } catch (Exception exception) {
        logger.warning("关闭检查器异常" + annotation.value() + "[" + annotation.priority() + "] -> " + instance.getClass().getName(), exception);
      }
    }

    logger.hint("关闭过滤器");

    List<Filter> filters = new ArrayList<>(SORTED_FILTER);
    Collections.reverse(filters);
    for (Filter annotation : filters) {
      EventHandlerFilter instance = COMPONENT_FILTER_INSTANCE.get(annotation);
      try {
        if (FurryBlack.isShutModeDrop()) {
          logger.info("丢弃过滤器" + annotation.value() + "[" + annotation.priority() + "] -> " + instance.getClass().getName());
          Thread thread = new Thread(instance::shutWrapper);
          thread.setDaemon(true);
          thread.start();
        } else {
          logger.info("关闭过滤器" + annotation.value() + "[" + annotation.priority() + "] -> " + instance.getClass().getName());
          instance.shutWrapper();
        }
      } catch (Exception exception) {
        logger.warning("关闭过滤器异常" + annotation.value() + "[" + annotation.priority() + "] -> " + instance.getClass().getName(), exception);
      }
    }

    logger.hint("关闭定时器");

    List<Runner> runners = new ArrayList<>(SORTED_RUNNER);
    Collections.reverse(runners);
    for (Runner annotation : runners) {
      EventHandlerRunner instance = COMPONENT_RUNNER_INSTANCE.get(annotation);
      try {
        if (FurryBlack.isShutModeDrop()) {
          logger.info("丢弃定时器" + annotation.value() + "[" + annotation.priority() + "] -> " + instance.getClass().getName());
          Thread thread = new Thread(instance::shutWrapper);
          thread.setDaemon(true);
          thread.start();
        } else {
          logger.info("关闭定时器" + annotation.value() + "[" + annotation.priority() + "] -> " + instance.getClass().getName());
          instance.shutWrapper();
        }
      } catch (Exception exception) {
        logger.warning("关闭定时器异常" + annotation.value() + "[" + annotation.priority() + "] -> " + instance.getClass().getName(), exception);
      }
    }

  }

  //= ================================================================================================================
  //=
  //= 模块管理
  //=
  //= ================================================================================================================

  //= ================================================================================================================
  //=  查询模块
  //= ================================================================================================================

  public Set<Map.Entry<String, Plugin>> getAllPlugin() {
    return plugins.entrySet();
  }

  public Map<String, Boolean> listAllModule() {

    Map<String, Boolean> result = new LinkedHashMap<>();

    for (Runner annotation : SORTED_RUNNER) {
      result.put(annotation.value(), COMPONENT_RUNNER_INSTANCE.containsKey(annotation));
    }

    for (Filter annotation : SORTED_FILTER) {
      result.put(annotation.value(), COMPONENT_FILTER_CLAZZ.containsKey(annotation));
    }

    for (Monitor annotation : SORTED_MONITOR) {
      result.put(annotation.value(), COMPONENT_MONITOR_INSTANCE.containsKey(annotation));
    }

    for (Checker annotation : SORTED_CHECKER) {
      result.put(annotation.value(), COMPONENT_CHECKER_CLAZZ.containsKey(annotation));
    }

    for (Executor annotation : COMPONENT_EXECUTOR_CLAZZ.keySet()) {
      result.put(annotation.value(), COMPONENT_EXECUTOR_INSTANCE.containsKey(annotation));
    }

    return result;
  }

  public Map<Runner, Boolean> listRunner() {
    Map<Runner, Boolean> result = new LinkedHashMap<>();
    for (Runner annotation : COMPONENT_RUNNER_CLAZZ.keySet()) {
      result.put(annotation, COMPONENT_RUNNER_INSTANCE.containsKey(annotation));
    }
    return result;
  }

  public Map<Filter, Boolean> listFilter() {
    Map<Filter, Boolean> result = new LinkedHashMap<>();
    for (Filter annotation : COMPONENT_FILTER_CLAZZ.keySet()) {
      result.put(annotation, COMPONENT_FILTER_INSTANCE.containsKey(annotation));
    }
    return result;
  }

  public Map<Monitor, Boolean> listMonitor() {
    Map<Monitor, Boolean> result = new LinkedHashMap<>();
    for (Monitor annotation : COMPONENT_MONITOR_CLAZZ.keySet()) {
      result.put(annotation, COMPONENT_MONITOR_INSTANCE.containsKey(annotation));
    }
    return result;
  }

  public Map<Checker, Boolean> listChecker() {
    Map<Checker, Boolean> result = new LinkedHashMap<>();
    for (Checker annotation : COMPONENT_CHECKER_CLAZZ.keySet()) {
      result.put(annotation, COMPONENT_CHECKER_INSTANCE.containsKey(annotation));
    }
    return result;
  }

  public Map<Executor, Boolean> listAllExecutor() {
    Map<Executor, Boolean> result = new LinkedHashMap<>();
    for (Executor annotation : COMPONENT_EXECUTOR_CLAZZ.keySet()) {
      result.put(annotation, COMPONENT_EXECUTOR_INSTANCE.containsKey(annotation));
    }
    return result;
  }

  public List<Checker> listGlobalUsersChecker() {
    return GLOBAL_CHECKER_USERS_POOL.stream().map(item -> item.getClass().getAnnotation(Checker.class)).toList();
  }

  public List<Checker> listGlobalGroupChecker() {
    return GLOBAL_CHECKER_GROUP_POOL.stream().map(item -> item.getClass().getAnnotation(Checker.class)).toList();
  }

  public Map<String, List<Checker>> listCommandsUsersChecker() {
    Map<String, List<Checker>> result = new LinkedHashMap<>();
    for (Map.Entry<String, List<EventHandlerChecker>> entry : COMMAND_CHECKER_USERS_POOL.entrySet()) {
      var k = entry.getKey();
      var v = entry.getValue();
      List<Checker> collect = v.stream().map(item -> item.getClass().getAnnotation(Checker.class)).toList();
      result.put(k, collect);
    }
    return result;
  }

  public Map<String, List<Checker>> listCommandsGroupChecker() {
    Map<String, List<Checker>> result = new LinkedHashMap<>();
    for (Map.Entry<String, List<EventHandlerChecker>> entry : COMMAND_CHECKER_GROUP_POOL.entrySet()) {
      var k = entry.getKey();
      var v = entry.getValue();
      List<Checker> collect = v.stream().map(item -> item.getClass().getAnnotation(Checker.class)).toList();
      result.put(k, collect);
    }
    return result;
  }

  //= ================================================================================================================
  //=  预载模块模板
  //= ================================================================================================================

  private Class<? extends AbstractEventHandler> getModuleClassEnsure(String name) {
    Class<? extends AbstractEventHandler> instance = getModuleClass(name);
    if (instance == null) {
      logger.info("没有找到模块模板 -> " + name + " " + (getModuleClass(name) == null ? "不存在" : "未加载"));
    }
    return instance;
  }

  private Class<? extends AbstractEventHandler> getModuleClass(String name) {

    if (!modules.containsKey(name)) {
      return null;
    }

    for (Map.Entry<Runner, Class<? extends EventHandlerRunner>> entry : COMPONENT_RUNNER_CLAZZ.entrySet()) {
      if (entry.getKey().value().equals(name)) {
        return entry.getValue();
      }
    }

    for (Map.Entry<Filter, Class<? extends EventHandlerFilter>> entry : COMPONENT_FILTER_CLAZZ.entrySet()) {
      if (entry.getKey().value().equals(name)) {
        return entry.getValue();
      }
    }

    for (Map.Entry<Monitor, Class<? extends EventHandlerMonitor>> entry : COMPONENT_MONITOR_CLAZZ.entrySet()) {
      if (entry.getKey().value().equals(name)) {
        return entry.getValue();
      }
    }

    for (Map.Entry<Checker, Class<? extends EventHandlerChecker>> entry : COMPONENT_CHECKER_CLAZZ.entrySet()) {
      if (entry.getKey().value().equals(name)) {
        return entry.getValue();
      }
    }

    for (Map.Entry<Executor, Class<? extends EventHandlerExecutor>> entry : COMPONENT_EXECUTOR_CLAZZ.entrySet()) {
      if (entry.getKey().value().equals(name)) {
        return entry.getValue();
      }
    }

    return null;
  }

  //= ================================================================================================================
  //=  获取模块实例
  //= ================================================================================================================

  private AbstractEventHandler getModuleInstanceEnsure(String name) {
    AbstractEventHandler instance = getModuleInstance(name);
    if (instance == null) {
      logger.info("没有找到模块实例 -> " + name + " " + (getModuleClass(name) == null ? "不存在" : "未加载"));
    }
    return instance;
  }

  private AbstractEventHandler getModuleInstance(String name) {

    if (!modules.containsKey(name)) {
      return null;
    }

    for (Map.Entry<Runner, EventHandlerRunner> entry : COMPONENT_RUNNER_INSTANCE.entrySet()) {
      if (entry.getKey().value().equals(name)) {
        return entry.getValue();
      }
    }

    for (Map.Entry<Filter, EventHandlerFilter> entry : COMPONENT_FILTER_INSTANCE.entrySet()) {
      if (entry.getKey().value().equals(name)) {
        return entry.getValue();
      }
    }

    for (Map.Entry<Monitor, EventHandlerMonitor> entry : COMPONENT_MONITOR_INSTANCE.entrySet()) {
      if (entry.getKey().value().equals(name)) {
        return entry.getValue();
      }
    }

    for (Map.Entry<Checker, EventHandlerChecker> entry : COMPONENT_CHECKER_INSTANCE.entrySet()) {
      if (entry.getKey().value().equals(name)) {
        return entry.getValue();
      }
    }

    for (Map.Entry<Executor, EventHandlerExecutor> entry : COMPONENT_EXECUTOR_INSTANCE.entrySet()) {
      if (entry.getKey().value().equals(name)) {
        return entry.getValue();
      }
    }

    return null;
  }

  //= ================================================================================================================
  //=  预载模块
  //= ================================================================================================================

  public void initModule(String name) {
    AbstractEventHandler moduleInstance = getModuleInstanceEnsure(name);
    if (moduleInstance == null) return;
    String instanceName = moduleInstance.getClass().getName();
    logger.info("预载模块 " + name + " -> " + instanceName);
    try {
      moduleInstance.initWrapper();
    } catch (Exception exception) {
      logger.warning("预载模块发生错误 " + name + " " + instanceName, exception);
    }
  }

  //= ================================================================================================================
  //=  启动模块
  //= ================================================================================================================

  public void bootModule(String name) {
    AbstractEventHandler moduleInstance = getModuleInstanceEnsure(name);
    if (moduleInstance == null) return;
    String instanceName = moduleInstance.getClass().getName();
    logger.info("启动模块 " + name + " -> " + instanceName);
    try {
      moduleInstance.bootWrapper();
    } catch (Exception exception) {
      logger.warning("启动模块发生错误 " + name + " " + instanceName, exception);
    }
  }

  //= ================================================================================================================
  //=  关闭模块
  //= ================================================================================================================

  public void shutModule(String name) {
    AbstractEventHandler moduleInstance = getModuleInstanceEnsure(name);
    if (moduleInstance == null) return;
    String instanceName = moduleInstance.getClass().getName();
    logger.info("关闭模块 " + name + " -> " + instanceName);
    try {
      moduleInstance.shutWrapper();
    } catch (Exception exception) {
      logger.warning("关闭模块发生错误 " + name + " " + instanceName, exception);
    }
  }

  //= ================================================================================================================
  //=  重启模块
  //= ================================================================================================================

  public void rebootModule(String name) {
    AbstractEventHandler moduleInstance = getModuleInstanceEnsure(name);
    if (moduleInstance == null) return;
    String instanceName = moduleInstance.getClass().getName();
    logger.info("重启模块 " + name + " -> " + instanceName);
    try {
      moduleInstance.shutWrapper();
      moduleInstance.initWrapper();
      moduleInstance.bootWrapper();
    } catch (Exception exception) {
      logger.warning("重启模块发生错误 " + name + " " + instanceName, exception);
    }
  }

  //= ================================================================================================================
  //=  卸载模块
  //= ================================================================================================================

  public void unloadModule(String name) {

    Class<? extends AbstractEventHandler> clazz = modules.get(name);

    if (clazz == null) {
      logger.warning("不存在此名称的模块 -> " + name);
      return;
    }

    for (Map.Entry<Runner, EventHandlerRunner> entry : COMPONENT_RUNNER_INSTANCE.entrySet()) {
      if (entry.getKey().value().equals(name)) {
        unloadModule(entry.getKey());
        return;
      }
    }

    for (Map.Entry<Filter, EventHandlerFilter> entry : COMPONENT_FILTER_INSTANCE.entrySet()) {
      if (entry.getKey().value().equals(name)) {
        unloadModule(entry.getKey());
        return;
      }
    }

    for (Map.Entry<Monitor, EventHandlerMonitor> entry : COMPONENT_MONITOR_INSTANCE.entrySet()) {
      if (entry.getKey().value().equals(name)) {
        unloadModule(entry.getKey());
        return;
      }
    }

    for (Map.Entry<Checker, EventHandlerChecker> entry : COMPONENT_CHECKER_INSTANCE.entrySet()) {
      if (entry.getKey().value().equals(name)) {
        unloadModule(entry.getKey());
        return;
      }
    }

    for (Map.Entry<Executor, EventHandlerExecutor> entry : COMPONENT_EXECUTOR_INSTANCE.entrySet()) {
      if (entry.getKey().value().equals(name)) {
        unloadModule(entry.getKey());
        return;
      }
    }

    logger.warning("此名称的模块未加载 -> " + name);

  }

  private void unloadModule(Runner annotation) {
    EventHandlerRunner instance = COMPONENT_RUNNER_INSTANCE.remove(annotation);
    instance.setEnable(false);
    instance.shutWrapper();
    logger.info("定时器已卸载 -> " + printAnnotation(annotation));
  }

  private void unloadModule(Filter annotation) {
    EventHandlerFilter instance = COMPONENT_FILTER_INSTANCE.remove(annotation);
    instance.setEnable(false);
    if (annotation.users()) FILTER_USERS_CHAIN.remove(instance);
    if (annotation.group()) FILTER_GROUP_CHAIN.remove(instance);
    instance.shutWrapper();
    logger.info("过滤器已卸载 -> " + printAnnotation(annotation));
  }

  private void unloadModule(Monitor annotation) {
    EventHandlerMonitor instance = COMPONENT_MONITOR_INSTANCE.remove(annotation);
    instance.setEnable(false);
    if (annotation.users()) MONITOR_USERS_CHAIN.remove(instance);
    if (annotation.group()) MONITOR_GROUP_CHAIN.remove(instance);
    instance.shutWrapper();
    logger.info("监听器已卸载 -> " + printAnnotation(annotation));
  }

  private void unloadModule(Checker annotation) {
    EventHandlerChecker instance = COMPONENT_CHECKER_INSTANCE.remove(annotation);
    instance.setEnable(false);
    if (annotation.users()) {
      if ("*".equals(annotation.command())) {
        GLOBAL_CHECKER_USERS_POOL.remove(instance);
      } else {
        COMMAND_CHECKER_USERS_POOL.get(annotation.command()).remove(instance);
      }
    }
    if (annotation.group()) {
      if ("*".equals(annotation.command())) {
        GLOBAL_CHECKER_GROUP_POOL.remove(instance);
      } else {
        COMMAND_CHECKER_GROUP_POOL.get(annotation.command()).remove(instance);
      }
    }
    instance.shutWrapper();
    logger.info("检查器已卸载 -> " + printAnnotation(annotation));
  }

  private void unloadModule(Executor annotation) {
    EventHandlerExecutor instance = COMPONENT_EXECUTOR_INSTANCE.remove(annotation);
    instance.setEnable(false);
    if (annotation.users()) EXECUTOR_USERS_POOL.remove(annotation.command());
    if (annotation.group()) EXECUTOR_GROUP_POOL.remove(annotation.command());
    COMMAND_EXECUTOR_RELATION.remove(annotation.command());
    instance.shutWrapper();
    logger.info("执行器已卸载 -> " + printAnnotation(annotation));
  }

  //= ================================================================================================================
  //=
  //= 内部功能
  //=
  //= ================================================================================================================

  //= ================================================================================================================
  //=  顺序机制
  //= ================================================================================================================

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

  //= ================================================================================================================
  //= 友好打印
  //= ================================================================================================================

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

  //= ==================================================================================================================
  //
  //
  //
  //= ==================================================================================================================

  @SuppressWarnings("StringConcatenationInsideStringBufferAppend")
  public String verboseStatus() {

    StringBuilder builder = new StringBuilder();

    builder
      .append(Color.BRIGHT_MAGENTA + ">> PLUGINS" + Color.RESET)
      .append(FurryBlack.LINE);

    for (Map.Entry<String, Plugin> entry : plugins.entrySet()) {
      var k = entry.getKey();
      var v = entry.getValue();
      builder
        .append(Color.BRIGHT_CYAN)
        .append(k)
        .append(":")
        .append(hexHash(v))
        .append(" ")
        .append(v.getFile())
        .append(Color.RESET)
        .append(FurryBlack.LINE);
      for (Map.Entry<String, Class<? extends AbstractEventHandler>> classEntry : v.getModules().entrySet()) {
        var classK = classEntry.getKey();
        var classV = classEntry.getValue();
        builder
          .append(classK)
          .append(" -> ")
          .append(classV.getName())
          .append(":")
          .append(hexHash(classV))
          .append(FurryBlack.LINE);
      }
    }

    builder
      .append(Color.BRIGHT_MAGENTA + ">> MODULES" + Color.RESET)
      .append(FurryBlack.LINE);

    for (Map.Entry<String, Class<? extends AbstractEventHandler>> entry : modules.entrySet()) {
      var k = entry.getKey();
      var v = entry.getValue();
      builder
        .append(k)
        .append(" -> ")
        .append(v.getName())
        .append(":")
        .append(hexHash(v))
        .append(FurryBlack.LINE);
    }

    builder
      .append(Color.BRIGHT_MAGENTA + ">> MODULE_PLUGIN_RELATION" + Color.RESET)
      .append(FurryBlack.LINE);

    for (Map.Entry<String, String> entry : MODULE_PLUGIN_RELATION.entrySet()) {
      var k = entry.getKey();
      var v = entry.getValue();
      builder
        .append(k)
        .append(" -> ")
        .append(v)
        .append(FurryBlack.LINE);
    }

    builder
      .append(Color.BRIGHT_CYAN + ">> COMPONENT_RUNNER_CLAZZ" + Color.RESET)
      .append(FurryBlack.LINE);

    for (Map.Entry<Runner, Class<? extends EventHandlerRunner>> entry : COMPONENT_RUNNER_CLAZZ.entrySet()) {
      var k = entry.getKey();
      var v = entry.getValue();
      builder
        .append(printAnnotation(k))
        .append(":")
        .append(hexHash(k))
        .append(" -> ")
        .append(v.getName())
        .append(":")
        .append(hexHash(v))
        .append(FurryBlack.LINE);
    }

    builder
      .append(Color.BRIGHT_CYAN + ">> COMPONENT_FILTER_CLAZZ" + Color.RESET)
      .append(FurryBlack.LINE);

    for (Map.Entry<Filter, Class<? extends EventHandlerFilter>> entry : COMPONENT_FILTER_CLAZZ.entrySet()) {
      var k = entry.getKey();
      var v = entry.getValue();
      builder
        .append(printAnnotation(k))
        .append(":")
        .append(hexHash(k))
        .append(" -> ")
        .append(v.getName())
        .append(":")
        .append(hexHash(v))
        .append(FurryBlack.LINE);
    }

    builder
      .append(Color.BRIGHT_CYAN + ">> COMPONENT_MONITOR_CLAZZ" + Color.RESET)
      .append(FurryBlack.LINE);

    for (Map.Entry<Monitor, Class<? extends EventHandlerMonitor>> entry : COMPONENT_MONITOR_CLAZZ.entrySet()) {
      var k = entry.getKey();
      var v = entry.getValue();
      builder
        .append(printAnnotation(k))
        .append(":")
        .append(hexHash(k))
        .append(" -> ")
        .append(v.getName())
        .append(":")
        .append(hexHash(v))
        .append(FurryBlack.LINE);
    }

    builder
      .append(Color.BRIGHT_CYAN + ">> COMPONENT_CHECKER_CLAZZ" + Color.RESET)
      .append(FurryBlack.LINE);

    for (Map.Entry<Checker, Class<? extends EventHandlerChecker>> entry : COMPONENT_CHECKER_CLAZZ.entrySet()) {
      var k = entry.getKey();
      var v = entry.getValue();
      builder
        .append(printAnnotation(k))
        .append(":")
        .append(hexHash(k))
        .append(" -> ")
        .append(v.getName())
        .append(":")
        .append(hexHash(v))
        .append(FurryBlack.LINE);
    }

    builder
      .append(Color.BRIGHT_CYAN + ">> COMPONENT_EXECUTOR_CLAZZ" + Color.RESET)
      .append(FurryBlack.LINE);

    for (Map.Entry<Executor, Class<? extends EventHandlerExecutor>> entry : COMPONENT_EXECUTOR_CLAZZ.entrySet()) {
      var k = entry.getKey();
      var v = entry.getValue();
      builder
        .append(printAnnotation(k))
        .append(":")
        .append(hexHash(k))
        .append(" -> ")
        .append(v.getName())
        .append(":")
        .append(hexHash(v))
        .append(FurryBlack.LINE);
    }

    builder
      .append(Color.BRIGHT_CYAN + ">> SORTED_RUNNER" + Color.RESET)
      .append(FurryBlack.LINE);

    for (Runner entry : SORTED_RUNNER) {
      builder
        .append(printAnnotation(entry))
        .append(":")
        .append(hexHash(entry))
        .append(FurryBlack.LINE);
    }

    builder
      .append(Color.BRIGHT_CYAN + ">> SORTED_FILTER" + Color.RESET)
      .append(FurryBlack.LINE);

    for (Filter entry : SORTED_FILTER) {
      builder
        .append(printAnnotation(entry))
        .append(":")
        .append(hexHash(entry))
        .append(FurryBlack.LINE);
    }

    builder
      .append(Color.BRIGHT_CYAN + ">> SORTED_MONITOR" + Color.RESET)
      .append(FurryBlack.LINE);

    for (Monitor entry : SORTED_MONITOR) {
      builder
        .append(printAnnotation(entry))
        .append(":")
        .append(hexHash(entry))
        .append(FurryBlack.LINE);
    }

    builder
      .append(Color.BRIGHT_CYAN + ">> SORTED_CHECKER" + Color.RESET)
      .append(FurryBlack.LINE);

    for (Checker entry : SORTED_CHECKER) {
      builder
        .append(printAnnotation(entry))
        .append(":")
        .append(hexHash(entry))
        .append(FurryBlack.LINE);
    }

    builder
      .append(Color.BRIGHT_CYAN + ">> COMPONENT_RUNNER_INSTANCE" + Color.RESET)
      .append(FurryBlack.LINE);

    for (Map.Entry<Runner, EventHandlerRunner> entry : COMPONENT_RUNNER_INSTANCE.entrySet()) {
      var k = entry.getKey();
      var v = entry.getValue();
      builder
        .append(printAnnotation(k))
        .append(":")
        .append(hexHash(k))
        .append(" -> ")
        .append(v.getClass().getName())
        .append(":")
        .append(hexHash(v))
        .append(FurryBlack.LINE);
    }

    builder
      .append(Color.BRIGHT_CYAN + ">> COMPONENT_FILTER_INSTANCE" + Color.RESET)
      .append(FurryBlack.LINE);

    for (Map.Entry<Filter, EventHandlerFilter> entry : COMPONENT_FILTER_INSTANCE.entrySet()) {
      var k = entry.getKey();
      var v = entry.getValue();
      builder
        .append(printAnnotation(k))
        .append(":")
        .append(hexHash(k))
        .append(" -> ")
        .append(v.getClass().getName())
        .append(":")
        .append(hexHash(v))
        .append(FurryBlack.LINE);
    }

    builder
      .append(Color.BRIGHT_CYAN + ">> COMPONENT_MONITOR_INSTANCE" + Color.RESET)
      .append(FurryBlack.LINE);

    for (Map.Entry<Monitor, EventHandlerMonitor> entry : COMPONENT_MONITOR_INSTANCE.entrySet()) {
      var k = entry.getKey();
      var v = entry.getValue();
      builder
        .append(printAnnotation(k))
        .append(":")
        .append(hexHash(k))
        .append(" -> ")
        .append(v.getClass().getName())
        .append(":")
        .append(hexHash(v))
        .append(FurryBlack.LINE);
    }

    builder
      .append(Color.BRIGHT_CYAN + ">> COMPONENT_CHECKER_INSTANCE" + Color.RESET)
      .append(FurryBlack.LINE);

    for (Map.Entry<Checker, EventHandlerChecker> entry : COMPONENT_CHECKER_INSTANCE.entrySet()) {
      var k = entry.getKey();
      var v = entry.getValue();
      builder
        .append(printAnnotation(k))
        .append(":")
        .append(hexHash(k))
        .append(" -> ")
        .append(v.getClass().getName())
        .append(":")
        .append(hexHash(v))
        .append(FurryBlack.LINE);
    }

    builder
      .append(Color.BRIGHT_CYAN + ">> COMPONENT_EXECUTOR_INSTANCE" + Color.RESET)
      .append(FurryBlack.LINE);

    for (Map.Entry<Executor, EventHandlerExecutor> entry : COMPONENT_EXECUTOR_INSTANCE.entrySet()) {
      var k = entry.getKey();
      var v = entry.getValue();
      builder
        .append(printAnnotation(k))
        .append(":")
        .append(hexHash(k))
        .append(" -> ")
        .append(v.getClass().getName())
        .append(":")
        .append(hexHash(v))
        .append(FurryBlack.LINE);
    }

    builder
      .append(Color.BRIGHT_CYAN + ">> FILTER_USERS_CHAIN" + Color.RESET)
      .append(FurryBlack.LINE);

    for (EventHandlerFilter item : FILTER_USERS_CHAIN) {
      builder
        .append(item.getClass().getName())
        .append(":")
        .append(hexHash(item))
        .append(FurryBlack.LINE);
    }

    builder
      .append(Color.BRIGHT_CYAN + ">> FILTER_GROUP_CHAIN" + Color.RESET)
      .append(FurryBlack.LINE);

    for (EventHandlerFilter item : FILTER_GROUP_CHAIN) {
      builder
        .append(item.getClass().getName())
        .append(":")
        .append(hexHash(item))
        .append(FurryBlack.LINE);
    }

    builder
      .append(Color.BRIGHT_CYAN + ">> MONITOR_USERS_CHAIN" + Color.RESET)
      .append(FurryBlack.LINE);

    for (EventHandlerMonitor item : MONITOR_USERS_CHAIN) {
      builder
        .append(item.getClass().getName())
        .append(":")
        .append(hexHash(item))
        .append(FurryBlack.LINE);
    }

    builder
      .append(Color.BRIGHT_CYAN + ">> MONITOR_GROUP_CHAIN" + Color.RESET)
      .append(FurryBlack.LINE);

    for (EventHandlerMonitor item : MONITOR_GROUP_CHAIN) {
      builder
        .append(item.getClass().getName())
        .append(":")
        .append(hexHash(item))
        .append(FurryBlack.LINE);
    }

    builder
      .append(Color.BRIGHT_CYAN + ">> GLOBAL_CHECKER_USERS_POOL" + Color.RESET)
      .append(FurryBlack.LINE);

    for (EventHandlerChecker item : GLOBAL_CHECKER_USERS_POOL) {
      builder
        .append(item.getClass().getName())
        .append(":")
        .append(hexHash(item))
        .append(FurryBlack.LINE);
    }

    builder
      .append(Color.BRIGHT_CYAN + ">> GLOBAL_CHECKER_GROUP_POOL" + Color.RESET)
      .append(FurryBlack.LINE);

    for (EventHandlerChecker item : GLOBAL_CHECKER_GROUP_POOL) {
      builder
        .append(item.getClass().getName())
        .append(":")
        .append(hexHash(item))
        .append(FurryBlack.LINE);
    }

    builder
      .append(Color.BRIGHT_CYAN + ">> COMMAND_CHECKER_USERS_POOL" + Color.RESET)
      .append(FurryBlack.LINE);

    for (Map.Entry<String, List<EventHandlerChecker>> entry : COMMAND_CHECKER_USERS_POOL.entrySet()) {
      var k = entry.getKey();
      var v = entry.getValue();
      builder
        .append(Color.CYAN)
        .append(k)
        .append(Color.RESET)
        .append(" ")
        .append(v.size())
        .append(FurryBlack.LINE);
      for (EventHandlerChecker checker : v) {
        builder
          .append(checker.getClass().getName())
          .append(":")
          .append(hexHash(checker))
          .append(FurryBlack.LINE);
      }
    }

    builder
      .append(Color.BRIGHT_CYAN + ">> COMMAND_CHECKER_GROUP_POOL" + Color.RESET)
      .append(FurryBlack.LINE);

    for (Map.Entry<String, List<EventHandlerChecker>> entry : COMMAND_CHECKER_GROUP_POOL.entrySet()) {
      var k = entry.getKey();
      var v = entry.getValue();
      builder
        .append(Color.CYAN)
        .append(k)
        .append(Color.RESET)
        .append(" ")
        .append(v.size())
        .append(FurryBlack.LINE);
      for (EventHandlerChecker checker : v) {
        builder
          .append(checker.getClass().getName())
          .append(":")
          .append(hexHash(checker))
          .append(FurryBlack.LINE);
      }
    }

    builder
      .append(Color.BRIGHT_CYAN + ">> EXECUTOR_USERS_POOL" + Color.RESET)
      .append(FurryBlack.LINE);

    for (Map.Entry<String, EventHandlerExecutor> entry : EXECUTOR_USERS_POOL.entrySet()) {
      var k = entry.getKey();
      var v = entry.getValue();
      builder
        .append(k)
        .append(" -> ")
        .append(v.getClass().getName())
        .append(":")
        .append(hexHash(v))
        .append(FurryBlack.LINE);
    }

    builder
      .append(Color.BRIGHT_CYAN + ">> EXECUTOR_GROUP_POOL" + Color.RESET)
      .append(FurryBlack.LINE);

    for (Map.Entry<String, EventHandlerExecutor> entry : EXECUTOR_GROUP_POOL.entrySet()) {
      var k = entry.getKey();
      var v = entry.getValue();
      builder
        .append(k + " -> " + v.getClass().getName() + ":" + hexHash(v))
        .append(FurryBlack.LINE);
    }

    builder
      .append(Color.BRIGHT_CYAN + ">> COMMAND_EXECUTOR_RELATION" + Color.RESET)
      .append(FurryBlack.LINE);

    for (Map.Entry<String, Executor> entry : COMMAND_EXECUTOR_RELATION.entrySet()) {
      var k = entry.getKey();
      var v = entry.getValue();
      builder
        .append(Color.CYAN)
        .append(k)
        .append(Color.RESET)
        .append(" -> ")
        .append(v.value())
        .append(":")
        .append(hexHash(v))
        .append(" {")
        .append(v.users() ? "U" : "")
        .append(v.group() ? "G" : "")
        .append("} ")
        .append(v.outline())
        .append(":")
        .append(v.description())
        .append(FurryBlack.LINE);
      for (String temp : v.usage()) {
        builder
          .append(temp)
          .append(FurryBlack.LINE);
      }
      for (String temp : v.privacy()) {
        builder
          .append(temp)
          .append(FurryBlack.LINE);
      }
    }

    return builder.toString();

  }
}
