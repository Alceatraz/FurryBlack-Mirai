package studio.blacktech.furryblackplus.core.exception.initlization;


import studio.blacktech.furryblackplus.core.annotation.Api;


@SuppressWarnings("unused")
@Api("初次启动 不能以默认值运行时 打断启动过程")
public class FirstBootException extends InitException {

    public FirstBootException() {
        super();
    }

    public FirstBootException(String message) {
        super(message);
    }

    public FirstBootException(String message, Throwable cause) {
        super(message, cause);
    }

    public FirstBootException(Throwable cause) {
        super(cause);
    }

}
