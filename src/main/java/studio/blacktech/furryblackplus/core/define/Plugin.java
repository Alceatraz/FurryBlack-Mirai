package studio.blacktech.furryblackplus.core.define;

import studio.blacktech.furryblackplus.core.annotation.Component;
import studio.blacktech.furryblackplus.core.exception.BotException;
import studio.blacktech.furryblackplus.core.exception.initlization.BootException;
import studio.blacktech.furryblackplus.core.interfaces.AbstractEventHandler;
import studio.blacktech.furryblackplus.core.interfaces.EventHandlerExecutor;
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
import java.util.LinkedList;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class Plugin {


    private final URL url;
    private final File file;
    private final List<Class<?>> classes;
    private final LoggerX logger;


    public Plugin(File file) {
        this.file = file;
        this.classes = new LinkedList<>();
        try {
            this.url = file.toURI().toURL();
        } catch (MalformedURLException exception) {
            throw new BootException(exception);
        }
        this.logger = new LoggerX(file.getName());
    }

    @SuppressWarnings("unchecked")
    public void load() {

        JarFile jarFile;

        try {
            jarFile = new JarFile(this.file);
        } catch (IOException exception) {
            throw new BotException("IOException when new JarFile", exception);
        }

        this.classes.clear();

        ClassLoader systemClassLoader = ClassLoader.getSystemClassLoader();
        URLClassLoader classLoader = new URLClassLoader(new URL[]{this.url}, systemClassLoader);

        Enumeration<JarEntry> entries = jarFile.entries();

        while (entries.hasMoreElements()) {

            JarEntry jarEntry = entries.nextElement();

            if (jarEntry.getSize() == 0) continue;

            String entryName = jarEntry.getName();

            if (!entryName.endsWith(".class")) continue;

            String className = entryName.substring(0, entryName.length() - 6).replace("/", ".");

            Class<?> clazz;

            try {
                clazz = Class.forName(className, false, classLoader);
            } catch (ClassNotFoundException exception) {
                throw new BotException("ClassNotFoundException when Class.forName " + className, exception);
            }

            if (!clazz.isAnnotationPresent(Component.class)) continue;

            Component annotation = clazz.getAnnotation(Component.class);

            AbstractEventHandler.ModuleInfo moduleInfo = new AbstractEventHandler.ModuleInfo(annotation);

            Class<?> superclass = clazz.getSuperclass();

            if (superclass == AbstractEventHandler.class) {
                this.logger.warning("发现错误继承的模块 " + clazz.getName());

            } else if (superclass == EventHandlerRunner.class) {
                Class<? extends EventHandlerRunner> runner = (Class<? extends EventHandlerRunner>) clazz;

            } else if (superclass == EventHandlerMonitor.class) {
                Class<? extends EventHandlerMonitor> monitor = (Class<? extends EventHandlerMonitor>) clazz;

            } else if (superclass == EventHandlerFilter.class) {
                Class<? extends EventHandlerFilter> filter = (Class<? extends EventHandlerFilter>) clazz;

            } else if (superclass == EventHandlerExecutor.class) {
                Class<? extends EventHandlerExecutor> executor = (Class<? extends EventHandlerExecutor>) clazz;

            } else {
                continue;
            }

            this.classes.add(clazz);

        }
    }
}
