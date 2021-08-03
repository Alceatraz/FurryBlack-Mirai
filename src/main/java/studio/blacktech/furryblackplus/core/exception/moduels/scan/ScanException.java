package studio.blacktech.furryblackplus.core.exception.moduels.scan;


import studio.blacktech.furryblackplus.common.Api;
import studio.blacktech.furryblackplus.core.exception.moduels.ModuleException;


@Api("启动过程发生的异常")
public class ScanException extends ModuleException {

    public ScanException() {

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
