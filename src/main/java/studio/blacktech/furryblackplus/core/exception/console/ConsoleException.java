package studio.blacktech.furryblackplus.core.exception.console;

import studio.blacktech.furryblackplus.core.exception.BotException;


public class ConsoleException extends BotException {


    public ConsoleException() {
        super();
    }

    public ConsoleException(String message) {
        super(message);
    }

    public ConsoleException(String message, Throwable cause) {
        super(message, cause);
    }

    public ConsoleException(Throwable cause) {
        super(cause);
    }
}
