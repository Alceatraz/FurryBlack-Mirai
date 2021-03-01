package studio.blacktech.furryblackplus.core.exception.initlization;


import studio.blacktech.furryblackplus.core.annotation.Api;
import studio.blacktech.furryblackplus.core.exception.BotException;


@Api("初始化时的异常 只在init()阶段抛出")
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
