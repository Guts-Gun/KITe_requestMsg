package gutsandgun.kite_requestmsg.exception;

public class CustomException extends RuntimeException{
    public static final String ERROR_DB = "ERROR_DB";

    public CustomException(String message) {
        super(message);
    }
}

