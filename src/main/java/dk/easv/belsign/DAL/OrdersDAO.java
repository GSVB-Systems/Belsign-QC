package dk.easv.belsign.DAL;

import dk.easv.belsign.BE.Orders;
import dk.easv.belsign.BE.Photos;
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
    private ExecutorService executorService;
    private final ThreadShutdownUtil threadShutdownUtil;

    public OrdersDAO() throws IOException {
        this.dbConnector = DBConnector.getInstance();
        this.threadShutdownUtil = ThreadShutdownUtil.getInstance();
        this.executorService = Executors.newFixedThreadPool(4);
        threadShutdownUtil.registerExecutorService(executorService);
    }

    @Override
    public CompletableFuture<Void> update(Orders order) {
        return null;
    }

    @Override
    public CompletableFuture<Void> delete(int id) throws Exception {
        return CompletableFuture.runAsync(() -> {
            String sql = "DELETE FROM Orders WHERE orderNumber = ?";

            Connection conn = null;
            try {
                conn = dbConnector.getConnection();
                conn.setAutoCommit(false); // Start transaktion

                try (PreparedStatement statement = conn.prepareStatement(sql)) {
                    statement.setInt(1, id);
                    int affectedRows = statement.executeUpdate();

                    if (affectedRows == 0) {
                        conn.rollback(); // Rollback hvis intet blev slettet
                        throw new RuntimeException("No order found with ID: " + id);
                    }

                    conn.commit(); // Alt ok, commit
                }

                conn.setAutoCommit(true); // Valgfrit: gendan autocommit

            } catch (SQLException e) {
                if (conn != null) {
                    try {
                        conn.rollback(); // Rollback hvis fejl
                    } catch (SQLException rollbackEx) {
                        throw new RuntimeException("Rollback failed after delete error", rollbackEx);
                    }
                }
                throw new RuntimeException("Error deleting order from database", e);
            }
        }, executorService);
    }


    public void shutdown() {
        executorService.shutdown();
    }

    @Override
    public CompletableFuture<Void> create(Orders order) {
        return CompletableFuture.runAsync(() -> {
            String checkSql = "SELECT 1 FROM orderApproval WHERE orderId = ?";
            String insertSql = "INSERT INTO orderApproval (orderId, approvalDate, orderStatus) VALUES (?, ?, ?)";

            try (Connection conn = dbConnector.getConnection()) {
                conn.setAutoCommit(false);

                // Check if orderId already exists
                boolean orderExists = false;
                try (PreparedStatement checkStatement = conn.prepareStatement(checkSql)) {
                    checkStatement.setInt(1, order.getOrderId());
                    try (ResultSet rs = checkStatement.executeQuery()) {
                        orderExists = rs.next();
                    }
                }

                // Only insert if order doesn't exist
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
                throw new RuntimeException("Error creating order approval", e);
            }
        }, executorService);
    }



/*
    @Override
    public CompletableFuture<Void> create(Orders order) {
        return CompletableFuture.runAsync(() -> {
            String sql = "INSERT INTO OrderApproval (orderId, approvalDate, orderStatus) VALUES (?, ?, ?)";
            try (Connection conn = dbConnector.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, toString().valueOf(order.getOrderId()));
                stmt.setObject(2, order.getApprovalDate());
                stmt.setString(3, order.getApprovalStatus());
                stmt.executeUpdate();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }, executorService);
    }

 */


    @Override
    public CompletableFuture<Orders> read(int id) throws Exception {
        return CompletableFuture.supplyAsync(() -> {
            String sql = "SELECT * FROM Orders WHERE orderId = ?";
            try (Connection conn = dbConnector.getConnection();
                 PreparedStatement statement = conn.prepareStatement(sql)) {

                statement.setInt(1, id);
                ResultSet rs = statement.executeQuery();

                if (rs.next()) {
                    int orderId = rs.getInt("orderId");
                    return new Orders(orderId);
                } else {
                    return null; // or throw an exception if preferred
                }

            } catch (SQLException e) {
                throw new RuntimeException("Error reading order from database", e);
            }
        });
    }


    @Override
    public CompletableFuture<List<Orders>> readAll() {
        return CompletableFuture.supplyAsync(() -> {
            ArrayList<Orders> orders = new ArrayList<>();
            String sql = "SELECT * FROM Orders";

            try (Connection conn = dbConnector.getConnection();
                 PreparedStatement statement = conn.prepareStatement(sql);
                 ResultSet rs = statement.executeQuery()) {

                while (rs.next()) {
                    int orderId = rs.getInt("orderId");
                    Orders order = new Orders(orderId);
                    orders.add(order);
                }
                return orders;
            } catch (SQLException e) {
                throw new RuntimeException("Error fetching orders from database", e);
            }
        }, executorService);
    }
    }
