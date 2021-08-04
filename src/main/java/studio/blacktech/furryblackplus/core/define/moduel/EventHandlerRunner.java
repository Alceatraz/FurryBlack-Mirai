package studio.blacktech.furryblackplus.core.define.moduel;


import studio.blacktech.furryblackplus.common.Api;
import studio.blacktech.furryblackplus.core.exception.BotException;


@Api("定时器父类")
public abstract class EventHandlerRunner extends AbstractEventHandler {

    private volatile boolean lock;

    @SuppressWarnings("deprecation")
    @Override
    @Deprecated
    public void internalInit(String name) {
        if (this.lock) {
            throw new BotException("Illegal access due to try invoke internalInit twice");
        }
        this.lock = true;
        super.internalInit(name);
    }
}
