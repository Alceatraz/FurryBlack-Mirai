package studio.blacktech.furryblackplus.core.exception.moduels.shut;


import studio.blacktech.furryblackplus.common.Api;
import studio.blacktech.furryblackplus.core.exception.moduels.ModuleException;


@Api("启动过程发生的异常")
public class ShutException extends ModuleException {

    public ShutException() {

    }

    public ShutException(String message) {
        super(message);
    }

    public ShutException(String message, Throwable cause) {
        super(message, cause);
    }

    public ShutException(Throwable cause) {
        super(cause);
    }

}
