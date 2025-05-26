package dk.easv.belsign.BLL;

import dk.easv.belsign.BLL.Util.ExceptionExtender;

public class BLLExceptions extends ExceptionExtender {

    public BLLExceptions(String message) {
        super(message);
    }

    public BLLExceptions(String message, Throwable cause) {
        super(message, cause);
    }
}
