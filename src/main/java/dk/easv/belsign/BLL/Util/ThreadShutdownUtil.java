package dk.easv.belsign.BLL.Util;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

public class ThreadShutdownUtil {
    private static final ThreadShutdownUtil instance = new ThreadShutdownUtil();
    private final List<ExecutorService> executorServices = new ArrayList<>();

    private ThreadShutdownUtil() {
        // Keep the shutdown hook as a backup
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try {
                shutdownAll();
            } catch (Exception e) {
                ExceptionHandler.handleUnexpectedException(e);
            }
        }));
    }

    public static ThreadShutdownUtil getInstance() {
        return instance;
    }

    public void registerExecutorService(ExecutorService executorService) {
        if (executorService != null) {
            executorServices.add(executorService);
        }
    }

    public void shutdownAll() {
        ExceptionHandler.getLogger().info("Shutting down all executor services...");
        for (ExecutorService service : executorServices) {
            try {
                shutdownExecutorService(service);
            } catch (Exception e) {
                ExceptionHandler.handleUnexpectedException(e);
            }
        }
        executorServices.clear();
    }

    private void shutdownExecutorService(ExecutorService service) {
        try {
            service.shutdown();
            if (!service.awaitTermination(3, TimeUnit.SECONDS)) {
                service.shutdownNow();
                if (!service.awaitTermination(3, TimeUnit.SECONDS)) {
                    ExceptionHandler.getLogger().severe("ExecutorService did not terminate");
                }
            }
        } catch (InterruptedException e) {
            service.shutdownNow();
            Thread.currentThread().interrupt();
            ExceptionHandler.handleUnexpectedException(e);
        } catch (Exception e) {
            ExceptionHandler.handleUnexpectedException(e);
        }
    }
}