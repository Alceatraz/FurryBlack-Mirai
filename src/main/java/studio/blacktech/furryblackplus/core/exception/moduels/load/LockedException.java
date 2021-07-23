package studio.blacktech.furryblackplus.core.exception.moduels.load;


import studio.blacktech.furryblackplus.common.Api;


@SuppressWarnings("unused")

@Api("防止多次初始化的锁")
public class BootLockedException extends LoadException {

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
