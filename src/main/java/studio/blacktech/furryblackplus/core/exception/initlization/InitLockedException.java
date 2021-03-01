package studio.blacktech.furryblackplus.core.exception.initlization;


import studio.blacktech.furryblackplus.core.annotation.Api;


@Api("防止多次初始化的锁")
public class InitLockedException extends InitException {

    public InitLockedException() {
        super();
    }

    public InitLockedException(String message) {
        super(message);
    }

    public InitLockedException(String message, Throwable cause) {
        super(message, cause);
    }

    public InitLockedException(Throwable cause) {
        super(cause);
    }

}
