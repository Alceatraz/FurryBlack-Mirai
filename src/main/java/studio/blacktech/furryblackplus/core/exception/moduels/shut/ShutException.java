package studio.blacktech.furryblackplus.core.exception.moduels;


import studio.blacktech.furryblackplus.common.Api;
import studio.blacktech.furryblackplus.core.exception.BotException;


@SuppressWarnings("unused")

@Api("启动过程发生的异常")
public class LoadException extends BotException {


    private static final long serialVersionUID = 1L;


    public LoadException() {
        super();
    }


    public LoadException(String message) {
        super(message);
    }


    public LoadException(String message, Throwable cause) {
        super(message, cause);
    }


    public LoadException(Throwable cause) {
        super(cause);
    }


}
