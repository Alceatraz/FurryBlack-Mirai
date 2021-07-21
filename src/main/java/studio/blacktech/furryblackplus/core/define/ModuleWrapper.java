package studio.blacktech.furryblackplus.core.define;


import studio.blacktech.furryblackplus.core.annotation.Component;
import studio.blacktech.furryblackplus.core.exception.BotException;
import studio.blacktech.furryblackplus.core.interfaces.AbstractEventHandler;

import java.lang.reflect.InvocationTargetException;


public class ModuleWrapper<T extends AbstractEventHandler> {


    private final String pluginName;
    private final Class<T> clazz;
    private final Component annotation;


    public ModuleWrapper(String pluginName, Class<T> clazz) {
        this.pluginName = pluginName;
        this.clazz = clazz;
        this.annotation = clazz.getAnnotation(Component.class);
    }

    public String getPluginName() {
        return this.pluginName;
    }

    public Component annotation() {
        return this.annotation;
    }

    public String artificial() {
        return this.annotation.artificial();
    }

    public String name() {
        return this.annotation.name();
    }

    public int priority() {
        return this.annotation.priority();
    }

    public Class<T> getClazz() {
        return this.clazz;
    }

    public String getClassName() {
        return this.clazz.getName();
    }


    public T newInstance() {
        T instance;
        try {
            instance = this.clazz.getConstructor().newInstance();
        } catch (NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException exception) {
            throw new BotException("创建模块实例失败 -> " + exception);
        }
        return instance;
    }


    public static int comparePriority(ModuleWrapper<? extends AbstractEventHandler> o1, ModuleWrapper<? extends AbstractEventHandler> o2) {
        Component o1Annotation = o1.annotation();
        Component o2Annotation = o2.annotation();
        return o1Annotation.priority() - o2Annotation.priority();
    }

    public boolean users() {
        return this.annotation.users();
    }

    public boolean group() {
        return this.annotation.group();
    }

    public String command() {
        return this.annotation.command();
    }

}
