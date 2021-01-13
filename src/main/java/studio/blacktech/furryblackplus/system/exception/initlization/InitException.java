package studio.blacktech.furryblackplus.system.exception.initlization;


import studio.blacktech.furryblackplus.system.exception.BotException;


/**
 * 初始化时的异常
 */
public class InitException extends BotException {


    private static final long serialVersionUID = 1L;


    public InitException() {
        super();
    }


    public InitException(String message) {
        super(message);
    }


    public InitException(String message, Throwable cause) {
        super(message, cause);
    }


    public InitException(Throwable cause) {
        super(cause);
    }


}
