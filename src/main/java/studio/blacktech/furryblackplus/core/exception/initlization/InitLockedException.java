package studio.blacktech.furryblackplus.core.exception.initlization;


/**
 * 禁止多次初始化
 */
public class InitLockedException extends InitException {


    private static final long serialVersionUID = 1L;


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
