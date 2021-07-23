package studio.blacktech.furryblackplus.core.exception.moduels;


import studio.blacktech.furryblackplus.common.Api;
import studio.blacktech.furryblackplus.core.exception.BotException;


@SuppressWarnings("unused")

@Api("启动过程发生的异常")
public class ShutException extends BotException {

    public ShutException() {
        super();
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
