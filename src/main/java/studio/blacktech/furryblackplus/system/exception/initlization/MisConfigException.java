package studio.blacktech.furryblackplus.system.exception.initlization;


import studio.blacktech.furryblackplus.system.exception.BotException;


/**
 * 配置错误
 */
public class MisConfigException extends BotException {


    private static final long serialVersionUID = 1L;


    public MisConfigException() {
        super();
    }


    public MisConfigException(String message) {
        super(message);
    }


    public MisConfigException(String message, Throwable cause) {
        super(message, cause);
    }


    public MisConfigException(Throwable cause) {
        super(cause);
    }


}
