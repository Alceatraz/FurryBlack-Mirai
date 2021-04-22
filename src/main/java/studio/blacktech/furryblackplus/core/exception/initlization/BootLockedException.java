package studio.blacktech.furryblackplus.core.exception.initlization;


import studio.blacktech.furryblackplus.core.annotation.Api;


@SuppressWarnings("unused")

@Api("防止多次初始化的锁")
public class BootLockedException extends BootException {

    public BootLockedException() {
        super();
    }

    public BootLockedException(String message) {
        super(message);
    }

    public BootLockedException(String message, Throwable cause) {
        super(message, cause);
    }

    public BootLockedException(Throwable cause) {
        super(cause);
    }

}
