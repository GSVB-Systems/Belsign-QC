package dk.easv.belsign.DAL;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface IProductDAO<T> extends ICrudRepo<T> {
    CompletableFuture<List<T>> readAllByOrderId(int orderId) throws Exception;
}

