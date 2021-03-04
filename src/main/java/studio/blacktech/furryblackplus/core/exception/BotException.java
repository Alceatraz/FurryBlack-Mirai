package studio.blacktech.furryblackplus.core.exception;


import studio.blacktech.furryblackplus.core.annotation.Api;


@SuppressWarnings("unused")
@Api("基础异常")
public class BotException extends Exception {

    public BotException() {
        super();
    }

    public BotException(String message) {
        super(message);
    }

    public BotException(String message, Throwable cause) {
        super(message, cause);
    }

    public BotException(Throwable cause) {
        super(cause);
    }

}
