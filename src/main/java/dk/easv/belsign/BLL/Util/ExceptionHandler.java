package dk.easv.belsign.BLL.Util;

import dk.easv.belsign.BLL.BLLExceptions;
import dk.easv.belsign.DAL.DALExceptions;

import java.io.IOException;
import java.util.logging.*;

public class ExceptionHandler {

    private final static Logger logger = Logger.getLogger(ExceptionHandler.class.getName());

    static {
        try {
            Handler fileHandler = new FileHandler("application.log", true);
            fileHandler.setFormatter(new SimpleFormatter());
            logger.addHandler(fileHandler);
            logger.setLevel(Level.ALL);
            logger.setUseParentHandlers(false);
        } catch (IOException e) {
            System.out.println("Cant initialize logger: " + e.getMessage());
        }
    }

    public static void handleDALException(DALExceptions e) {
        logger.log(Level.SEVERE, "DAL Error: " + e.getMessage(),e);
    }

    public static void handleBLLException(BLLExceptions e) {
        logger.log(Level.WARNING,"Logic Error: " + e.getMessage(), e);
    }

    public static void handleUnexpectedException(Exception e) {
        logger.log(Level.SEVERE, "Unexpected Error: " + e.getMessage(), e);
    }

    public static Logger getLogger() {
        return logger;
    }
}
