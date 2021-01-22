package studio.blacktech.furryblackplus.core.exception;


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
