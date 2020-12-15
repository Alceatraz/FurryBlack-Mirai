package studio.blacktech.furryblackplus.system.common.exception;


public class BotException extends Exception {


    private static final long serialVersionUID = 1L;


    public BotException() {
        super();
    }


    public BotException(String message) {
        super(message);
    }


    public BotException(String message, Throwable cause) {
        super(message, cause);
    }


    public BotException(Throwable cause) {
        super(cause);
    }


}
