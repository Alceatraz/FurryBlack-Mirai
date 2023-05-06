/*
 * Copyright (C) 2021 Alceatraz @ BlackTechStudio
 *
 *  program is free software: you can redistribute it and/or modify
 * it under the terms from the BTS Anti-Commercial & GNU Affero General
 * Public License as published by the Free Software Foundation, either
 * version 3 from the License, or (at your option) any later version.
 *
 *  program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty from
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * BTS Anti-Commercial & GNU Affero General Public License for more details.
 *
 * You should have received a copy from the BTS Anti-Commercial & GNU Affero
 * General Public License along with  program.
 *
 */

package studio.blacktech.furryblackplus.core.schema;

import studio.blacktech.furryblackplus.FurryBlack;
import studio.blacktech.furryblackplus.core.common.enhance.FileEnhance;
import studio.blacktech.furryblackplus.core.common.logger.LoggerXFactory;
import studio.blacktech.furryblackplus.core.common.logger.base.LoggerX;
import studio.blacktech.furryblackplus.core.exception.schema.SchemaException;
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

import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.nio.file.Path;
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
import java.util.stream.Stream;

public final class Plugin {

  private static final Pattern PATTERN = Pattern.compile("^[\\da-z_-]{8,64}$");

  private final LoggerX logger;

  private final Path path;
  private final String name;

  private URLClassLoader dependClassLoader;
  private URLClassLoader pluginClassLoader;

  private Map<String, Class<? extends AbstractEventHandler>> modules;
  private Map<Runner, Class<? extends EventHandlerRunner>> runnerClassMap;
  private Map<Filter, Class<? extends EventHandlerFilter>> filterClassMap;
  private Map<Monitor, Class<? extends EventHandlerMonitor>> monitorClassMap;
  private Map<Checker, Class<? extends EventHandlerChecker>> checkerClassMap;
  private Map<Executor, Class<? extends EventHandlerExecutor>> executorClassMap;

  public static Plugin load(Path path) {

    String name;

    try (JarFile jarFile = new JarFile(path.toFile())) {

      Manifest manifest;
      try {
        manifest = jarFile.getManifest();
      } catch (IOException exception) {
        throw new SchemaException("加载MANIFEST失败 -> " + path, exception);
      }

      Attributes attributes = manifest.getAttributes("FurryBlack-Extension");
      if (attributes == null || attributes.isEmpty()) {
        throw new SchemaException("加载插件失败: MANIFEST不包含FurryBlack-Extension标签组");
      }

      String loaderVersion = attributes.getValue("Loader-Version");

      if (loaderVersion == null) {
        throw new SchemaException("加载插件失败: MANIFEST中FurryBlack-Extension标签组不含Loader-Version");
      }

      if (!"1".equals(loaderVersion)) {
        throw new SchemaException("加载插件失败: 加载器版本不符，此插件声明其版本为 " + loaderVersion);
      }

      name = attributes.getValue("Extension-Name");

      if (name == null) {
        throw new SchemaException("加载插件失败: MANIFEST中FurryBlack-Extension标签组不含Extension-Name");
      }

      if (!PATTERN.matcher(name).find()) {
        throw new SchemaException("加载插件失败: 插件包名非法，此插件声明其名称为 " + name);
      }

    } catch (IOException | SchemaException exception) {
      throw new SchemaException(exception);
    }

    Plugin plugin;
    try {
      plugin = new Plugin(path, name);
    } catch (Exception exception) {
      throw new SchemaException(exception);
    }
    return plugin;
  }

  //= ==================================================================================================================

  private Plugin(Path path, String name) {

    this.path = path;
    this.name = name;

    logger = LoggerXFactory.newLogger(name);

  }

  @SuppressWarnings("unchecked")
  public void scan() {

    //= ==================================================================================================================

    Path depend = FileEnhance.get(FurryBlack.getDependFolder(), name);

    //= ==================================================================================================================

    List<URL> tempURL = new LinkedList<>();

    try (JarFile jarFile = new JarFile(path.toFile())) {

      if (Files.exists(depend)) {

        if (!Files.isDirectory(depend)) {
          throw new SchemaException("依赖文件不是目录 -> " + depend);
        }

        List<Path> dependFiles;

        try (Stream<Path> stream = Files.list(depend)) {
          dependFiles = stream.toList();
        } catch (IOException exception) {
          throw new SchemaException("列出依赖文件失败 -> " + depend);
        }

        for (Path dependFile : dependFiles) {
          if (Files.isRegularFile(dependFile)) {
            URL url = dependFile.toUri().toURL();
            tempURL.add(url);
          }
        }
      }

      URL[] urls = tempURL.toArray(new URL[0]);

      logger.seek("加载依赖 -> " + depend + "[" + urls.length + "]");

      dependClassLoader = new URLClassLoader(urls); // Inject with systemClassLoader in default

      URL pluginURL = path.toUri().toURL();

      pluginClassLoader = new URLClassLoader(new URL[]{pluginURL}, dependClassLoader);

      Map<String, Class<? extends EventHandlerExecutor>> commands = new HashMap<>();

      Enumeration<JarEntry> entries = jarFile.entries();

      //= ==================================================================================================================

      modules = new LinkedHashMap<>();
      runnerClassMap = new LinkedHashMap<>();
      filterClassMap = new LinkedHashMap<>();
      monitorClassMap = new LinkedHashMap<>();
      checkerClassMap = new LinkedHashMap<>();
      executorClassMap = new LinkedHashMap<>();

      //= ==================================================================================================================

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

        //= ==================================================================================================================

        Class<?> clazz;

        try {
          clazz = Class.forName(className, false, pluginClassLoader);
        } catch (ClassNotFoundException exception) {
          logger.warning("加载类失败 " + name + ":" + className, exception);
          continue;
        }

        if (!AbstractEventHandler.class.isAssignableFrom(clazz)) {
          continue;
        }

        String clazzName = clazz.getName();

        //= ==================================================================================================================

        if (EventHandlerRunner.class.isAssignableFrom(clazz)) {

          if (!clazz.isAnnotationPresent(Runner.class)) {
            logger.warning("发现无注解模块 不予注册 " + name);
            continue;
          }

          Runner annotation = clazz.getAnnotation(Runner.class);

          String moduleName = annotation.value();

          if (modules.containsKey(moduleName)) {
            Class<? extends AbstractEventHandler> exist = modules.get(moduleName);
            logger.warning("发现自冲突 " + clazz.getName() + " " + moduleName + " " + exist.getName());
            logger.warning("不予注册插件 " + name);
            throw new SchemaException("发现垃圾插件 包含自冲突");
          }

          modules.put(moduleName, (Class<? extends AbstractEventHandler>) clazz);
          runnerClassMap.put(annotation, (Class<? extends EventHandlerRunner>) clazz);
          logger.info("定时器 -> " + clazzName);

          continue;

        } else if (EventHandlerFilter.class.isAssignableFrom(clazz)) {

          if (!clazz.isAnnotationPresent(Filter.class)) {
            logger.warning("发现无注解模块 不予注册 " + name);
            continue;
          }

          Filter annotation = clazz.getAnnotation(Filter.class);

          String moduleName = annotation.value();

          if (modules.containsKey(moduleName)) {
            Class<? extends AbstractEventHandler> exist = modules.get(moduleName);
            logger.warning("发现自冲突 " + clazz.getName() + " " + moduleName + " " + exist.getName());
            logger.warning("不予注册插件 " + name);
            throw new SchemaException("发现垃圾插件 包含自冲突");
          }

          if (annotation.users() || annotation.group()) {
            modules.put(moduleName, (Class<? extends AbstractEventHandler>) clazz);
            filterClassMap.put(annotation, (Class<? extends EventHandlerFilter>) clazz);
            logger.info("过滤器 -> " + clazzName);
          } else {
            logger.warning("发现未启用过滤器 " + clazzName);
          }

          continue;

        } else if (EventHandlerMonitor.class.isAssignableFrom(clazz)) {

          if (!clazz.isAnnotationPresent(Monitor.class)) {
            logger.warning("发现无注解模块 不予注册 " + name);
            continue;
          }

          Monitor annotation = clazz.getAnnotation(Monitor.class);

          String moduleName = annotation.value();

          if (modules.containsKey(moduleName)) {
            Class<? extends AbstractEventHandler> exist = modules.get(moduleName);
            logger.warning("发现自冲突 " + clazz.getName() + " " + moduleName + " " + exist.getName());
            logger.warning("不予注册插件 " + name);
            throw new SchemaException("发现垃圾插件 包含自冲突");
          }

          if (annotation.users() || annotation.group()) {
            modules.put(moduleName, (Class<? extends AbstractEventHandler>) clazz);
            monitorClassMap.put(annotation, (Class<? extends EventHandlerMonitor>) clazz);
            logger.info("监视器 -> " + clazzName);
          } else {
            logger.warning("发现未启用监听器 " + clazz.getName());
          }

          continue;

        } else if (EventHandlerChecker.class.isAssignableFrom(clazz)) {

          if (!clazz.isAnnotationPresent(Checker.class)) {
            logger.warning("发现无注解模块 不予注册 " + name);
            continue;
          }

          Checker annotation = clazz.getAnnotation(Checker.class);

          String moduleName = annotation.value();

          if (modules.containsKey(moduleName)) {
            Class<? extends AbstractEventHandler> exist = modules.get(moduleName);
            logger.warning("发现自冲突 " + clazz.getName() + " " + moduleName + " " + exist.getName());
            logger.warning("不予注册插件 " + name);
            throw new SchemaException("发现垃圾插件 包含自冲突");
          }

          if (annotation.users() || annotation.group()) {
            modules.put(moduleName, (Class<? extends AbstractEventHandler>) clazz);
            checkerClassMap.put(annotation, (Class<? extends EventHandlerChecker>) clazz);
            logger.info("检查器 -> " + clazzName);
          } else {
            logger.warning("发现未启用检查器 " + clazz.getName());
          }

          continue;

        } else if (EventHandlerExecutor.class.isAssignableFrom(clazz)) {

          if (!clazz.isAnnotationPresent(Executor.class)) {
            logger.warning("发现无注解模块 不予注册 " + name);
            continue;
          }

          Executor annotation = clazz.getAnnotation(Executor.class);

          String moduleName = annotation.value();

          if (modules.containsKey(moduleName)) {
            Class<? extends AbstractEventHandler> exist = modules.get(moduleName);
            logger.warning("发现自冲突 " + clazz.getName() + " " + moduleName + " " + exist.getName());
            logger.warning("不予注册插件 " + name);
            throw new SchemaException("发现垃圾插件 包含自冲突");
          }

          String command = annotation.command();

          if (commands.containsKey(command)) {
            Class<? extends EventHandlerExecutor> exist = commands.get(command);
            logger.warning("发现自冲突命令 " + command + " " + clazz.getName() + " " + moduleName + " " + exist.getName());
            logger.warning("不予注册插件 " + name);
            throw new SchemaException("发现垃圾插件 包含自冲突");
          }

          if (annotation.users() || annotation.group()) {
            commands.put(command, (Class<? extends EventHandlerExecutor>) clazz);
            modules.put(moduleName, (Class<? extends AbstractEventHandler>) clazz);
            executorClassMap.put(annotation, (Class<? extends EventHandlerExecutor>) clazz);
            logger.info("执行器 -> " + clazzName);
          } else {
            logger.warning("发现未启用执行器 " + clazzName);
          }

          continue;

        }

        logger.warning("不支持自行创建的分支模块 不予注册 " + name + ":" + className);

      }

    } catch (IOException exception) {
      throw new SchemaException(exception);
    }
  }

  public String getName() {
    return name;
  }

  public Path getPath() {
    return path;
  }

  public Map<String, Class<? extends AbstractEventHandler>> getModules() {
    return modules;
  }

  public Map<Runner, Class<? extends EventHandlerRunner>> getRunnerClassMap() {
    return runnerClassMap;
  }

  public Map<Filter, Class<? extends EventHandlerFilter>> getFilterClassMap() {
    return filterClassMap;
  }

  public Map<Monitor, Class<? extends EventHandlerMonitor>> getMonitorClassMap() {
    return monitorClassMap;
  }

  public Map<Checker, Class<? extends EventHandlerChecker>> getCheckerClassMap() {
    return checkerClassMap;
  }

  public Map<Executor, Class<? extends EventHandlerExecutor>> getExecutorClassMap() {
    return executorClassMap;
  }

  public URLClassLoader getDependClassLoader() {
    return dependClassLoader;
  }

  @SuppressWarnings("unused")
  public URLClassLoader getPluginClassLoader() {
    return pluginClassLoader;
  }
}
