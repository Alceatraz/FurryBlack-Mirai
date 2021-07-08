package studio.blacktech.furryblackplus.core.exception;


import studio.blacktech.furryblackplus.common.Api;


@SuppressWarnings("unused")
@Api("基础异常 用于静默异常 避免到处写异常处理，由框架的某个操作做高层处理")
public class BotException extends RuntimeException {

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
