package studio.blacktech.furryblackplus.core.exception.moduels;


import studio.blacktech.furryblackplus.common.Api;
import studio.blacktech.furryblackplus.core.exception.BotException;


@Api("模块相关的异常")
public class ModuleException extends BotException {

    public ModuleException() {

    }

    public ModuleException(String message) {
        super(message);
    }

    public ModuleException(String message, Throwable cause) {
        super(message, cause);
    }

    public ModuleException(Throwable cause) {
        super(cause);
    }

}
