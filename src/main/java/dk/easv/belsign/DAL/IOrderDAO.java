package dk.easv.belsign.DAL;

import dk.easv.belsign.BE.Orders;

import java.sql.SQLException;
import java.util.concurrent.CompletableFuture;

public interface IOrderDAO<T> extends ICrudRepo<T> {
    CompletableFuture<Void> createOrderApproval(Orders order) throws SQLException;

}
