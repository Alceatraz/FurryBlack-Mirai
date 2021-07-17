package studio.blacktech.furryblackplus.core.define;


import studio.blacktech.furryblackplus.core.annotation.Component;
import studio.blacktech.furryblackplus.core.exception.BotException;
import studio.blacktech.furryblackplus.core.interfaces.AbstractEventHandler;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

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

    public Component getAnnotation() {
        return this.annotation;
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
            Method method = this.clazz.getMethod("instantiated", Component.class);
            instance = this.clazz.getConstructor().newInstance();
            method.invoke(instance, this.annotation);
        } catch (NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException exception) {
            throw new BotException("创建模块实例失败 -> " + exception);
        }
        return instance;
    }

}
