package studio.blacktech.furryblackplus.core.define;

import studio.blacktech.furryblackplus.core.interfaces.AbstractEventHandler;

public class EventHandlerWrapper<T extends AbstractEventHandler> {

    private final PluginPackage belong;

    public EventHandlerWrapper(PluginPackage belong, Class<? extends AbstractEventHandler> clazz) {
        this.belong = belong;
    }

}
