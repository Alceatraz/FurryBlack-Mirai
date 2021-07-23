package studio.blacktech.furryblackplus.core.exception.moduels.shut;


import studio.blacktech.furryblackplus.common.Api;
import studio.blacktech.furryblackplus.core.exception.BotException;


@SuppressWarnings("unused")

@Api("启动过程发生的异常")
public class ShutException extends BotException {


    private static final long serialVersionUID = 1L;


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
