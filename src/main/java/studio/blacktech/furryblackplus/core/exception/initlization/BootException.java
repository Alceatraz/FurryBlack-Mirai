package studio.blacktech.furryblackplus.core.exception.initlization;


import studio.blacktech.furryblackplus.common.Api;
import studio.blacktech.furryblackplus.core.exception.BotException;


@SuppressWarnings("unused")

@Api("启动过程发生的异常")
public class BootException extends BotException {


    private static final long serialVersionUID = 1L;


    public BootException() {
        super();
    }


    public BootException(String message) {
        super(message);
    }


    public BootException(String message, Throwable cause) {
        super(message, cause);
    }


    public BootException(Throwable cause) {
        super(cause);
    }


}
