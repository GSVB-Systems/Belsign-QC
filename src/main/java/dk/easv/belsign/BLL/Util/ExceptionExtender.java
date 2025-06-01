package dk.easv.belsign.BLL.Util;

public class ExceptionExtender extends RuntimeException{

    public ExceptionExtender(String message) {
        super(message);
    }

    public ExceptionExtender(String message, Throwable cause) {
        super(message, cause);
    }
}
