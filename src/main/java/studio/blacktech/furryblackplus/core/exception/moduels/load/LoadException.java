package studio.blacktech.furryblackplus.core.exception.moduels.load;


import studio.blacktech.furryblackplus.common.Api;
import studio.blacktech.furryblackplus.core.exception.moduels.ModuleException;


@Api("启动过程发生的异常")
public class LoadException extends ModuleException {

    public LoadException() {

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
