package studio.blacktech.furryblackplus.core.exception.moduels.load;


import studio.blacktech.furryblackplus.common.Api;


@Api("初次启动 不能以默认值运行时 打断启动过程")
public class FirstBootException extends LoadException {

    public FirstBootException() {

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
