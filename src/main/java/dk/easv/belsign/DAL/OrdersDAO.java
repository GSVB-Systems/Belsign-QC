package dk.easv.belsign.DAL;

import dk.easv.belsign.BE.Orders;
import dk.easv.belsign.BLL.Util.ExceptionHandler;
import dk.easv.belsign.BLL.Util.ThreadShutdownUtil;

import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class OrdersDAO implements ICrudRepo<Orders> {

    private final DBConnector dbConnector;
    private final ExecutorService executorService;
    private final ThreadShutdownUtil threadShutdownUtil;

    public OrdersDAO() throws IOException {
        this.dbConnector = DBConnector.getInstance();
        this.threadShutdownUtil = ThreadShutdownUtil.getInstance();
        this.executorService = Executors.newFixedThreadPool(4);
        threadShutdownUtil.registerExecutorService(executorService);
    }

    @Override
    public CompletableFuture<Void> update(Orders order) {
        return null; // Not implemented yet
    }

    @Override
    public CompletableFuture<Void> delete(int id) {
        return CompletableFuture.runAsync(() -> {
            String sql = "DELETE FROM Orders WHERE orderNumber = ?";
            Connection conn = null;

            try {
                conn = dbConnector.getConnection();
                conn.setAutoCommit(false);

                try (PreparedStatement statement = conn.prepareStatement(sql)) {
                    statement.setInt(1, id);
                    int affectedRows = statement.executeUpdate();

                    if (affectedRows == 0) {
                        conn.rollback();
                        throw new DALExceptions("No order with matching ID: " + id);
                    }

                    conn.commit();
                }
            } catch (SQLException e) {
                try {
                    if (conn != null) conn.rollback();
                } catch (SQLException rollbackEx) {
                    DALExceptions rollbackError = new DALExceptions("Order deletion rollback failed: ", rollbackEx);
                    ExceptionHandler.handleDALException(rollbackError);
                    throw rollbackError;
                }

                DALExceptions ex = new DALExceptions("Failed to delete order from DB: ", e);
                ExceptionHandler.handleDALException(ex);
                throw ex;
            } finally {
                try {
                    if (conn != null) conn.setAutoCommit(true);
                } catch (SQLException e) {
                    DALExceptions ex = new DALExceptions("autocommit reset failed: ", e);
                    ExceptionHandler.handleDALException(ex);
                    throw ex;
                }
            }
        }, executorService);
    }

    @Override
    public CompletableFuture<Void> create(Orders order) {
        return CompletableFuture.runAsync(() -> {
            String checkSql = "SELECT 1 FROM orderApproval WHERE orderId = ?";
            String insertSql = "INSERT INTO orderApproval (orderId, approvalDate, orderStatus) VALUES (?, ?, ?)";

            try (Connection conn = dbConnector.getConnection()) {
                conn.setAutoCommit(false);

                boolean orderExists;
                try (PreparedStatement checkStatement = conn.prepareStatement(checkSql)) {
                    checkStatement.setInt(1, order.getOrderId());
                    try (ResultSet rs = checkStatement.executeQuery()) {
                        orderExists = rs.next();
                    }
                }

                if (!orderExists) {
                    try (PreparedStatement insertStatement = conn.prepareStatement(insertSql)) {
                        insertStatement.setInt(1, order.getOrderId());
                        insertStatement.setObject(2, order.getApprovalDate());
                        insertStatement.setString(3, order.getApprovalStatus());
                        insertStatement.executeUpdate();
                    }
                }

                conn.commit();
            } catch (SQLException e) {
                DALExceptions ex = new DALExceptions("Failed to create Approval: ", e);
                ExceptionHandler.handleDALException(ex);
                throw ex;
            }
        }, executorService);
    }

    @Override
    public CompletableFuture<Orders> read(int id) {
        return CompletableFuture.supplyAsync(() -> {
            String sql = "SELECT * FROM Orders WHERE orderId = ?";

            try (Connection conn = dbConnector.getConnection();
                 PreparedStatement statement = conn.prepareStatement(sql)) {

                statement.setInt(1, id);
                try (ResultSet rs = statement.executeQuery()) {
                    if (rs.next()) {
                        return new Orders(rs.getInt("orderId"));
                    } else {
                        return null;
                    }
                }

            } catch (SQLException e) {
                DALExceptions ex = new DALExceptions("Failed to read order from DB: ", e);
                ExceptionHandler.handleDALException(ex);
                throw ex;
            }
        }, executorService);
    }

    @Override
    public CompletableFuture<List<Orders>> readAll() {
        return CompletableFuture.supplyAsync(() -> {
            List<Orders> orders = new ArrayList<>();
            String sql = "SELECT * FROM Orders";

            try (Connection conn = dbConnector.getConnection();
                 PreparedStatement statement = conn.prepareStatement(sql);
                 ResultSet rs = statement.executeQuery()) {

                while (rs.next()) {
                    orders.add(new Orders(rs.getInt("orderId")));
                }

                return orders;

            } catch (SQLException e) {
                DALExceptions ex = new DALExceptions("Failed to get all orders from DB: ", e);
                ExceptionHandler.handleDALException(ex);
                throw ex;
            }
        }, executorService);
    }

    public void shutdown() {
        executorService.shutdown();
    }
}