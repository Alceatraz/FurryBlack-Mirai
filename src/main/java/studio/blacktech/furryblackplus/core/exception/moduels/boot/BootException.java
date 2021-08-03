package studio.blacktech.furryblackplus.core.exception.moduels.boot;


import studio.blacktech.furryblackplus.common.Api;
import studio.blacktech.furryblackplus.core.exception.moduels.ModuleException;


@Api("启动过程发生的异常")
public class BootException extends ModuleException {

    public BootException() {

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
