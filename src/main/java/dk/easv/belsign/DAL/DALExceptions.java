package dk.easv.belsign.DAL;

import dk.easv.belsign.BLL.Util.ExceptionExtender;

public class DALExceptions extends ExceptionExtender {

    public DALExceptions(String message) {
        super(message);
    }

    public DALExceptions(String message, Throwable cause) {
        super(message, cause);
    }
}
