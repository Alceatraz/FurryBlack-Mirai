package studio.blacktech.furryblackplus.core.exception.initlization;


import studio.blacktech.furryblackplus.core.annotation.Api;


@Api("配置错误")
public class MisConfigException extends InitException {

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
