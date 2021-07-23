package studio.blacktech.furryblackplus.core.exception.moduels.scan;


import studio.blacktech.furryblackplus.common.Api;
import studio.blacktech.furryblackplus.core.exception.BotException;


@SuppressWarnings("unused")

@Api("启动过程发生的异常")
public class ScanException extends BotException {


    private static final long serialVersionUID = 1L;


    public ScanException() {
        super();
    }


    public ScanException(String message) {
        super(message);
    }


    public ScanException(String message, Throwable cause) {
        super(message, cause);
    }


    public ScanException(Throwable cause) {
        super(cause);
    }


}
