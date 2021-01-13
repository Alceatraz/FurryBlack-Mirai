package studio.blacktech.furryblackplus.system.exception.working;


import studio.blacktech.furryblackplus.system.exception.BotException;

public class NotAFolderException extends BotException {

    private static final long serialVersionUID = 1L;

    public NotAFolderException() {

        super();

    }

    public NotAFolderException(String message) {

        super(message);

    }

    public NotAFolderException(String message, Throwable cause) {

        super(message, cause);

    }

    public NotAFolderException(Throwable cause) {

        super(cause);

    }

}
