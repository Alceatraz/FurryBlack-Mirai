package studio.blacktech.furryblackplus.core.exception.initlization;


/**
 * 配置错误
 */
public class MisConfigException extends InitException {


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
