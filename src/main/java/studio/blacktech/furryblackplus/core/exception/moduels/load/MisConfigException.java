package studio.blacktech.furryblackplus.core.exception.moduels.load;


import studio.blacktech.furryblackplus.common.Api;


@SuppressWarnings("unused")
@Api("配置错误")
public class MisConfigException extends LoadException {

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
