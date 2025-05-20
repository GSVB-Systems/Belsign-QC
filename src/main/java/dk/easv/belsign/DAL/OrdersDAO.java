package dk.easv.belsign.DAL;

import dk.easv.belsign.BE.Orders;
import dk.easv.belsign.BLL.Util.ThreadShutdownUtil;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
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
        return CompletableFuture.runAsync(() -> {
            String sql = "UPDATE orders SET productQuantity = ? WHERE orderId = ?";

            try (Connection conn = dbConnector.getConnection()) {
                conn.setAutoCommit(false);

                try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                    stmt.setInt(1, order.getProductQuantity());
                    stmt.setInt(2, order.getOrderId());

                    stmt.executeUpdate();
                    conn.commit();
                } catch (SQLException e) {
                    try {
                        conn.rollback();
                    } catch (SQLException ex) {
                        throw new RuntimeException("Error rolling back transaction", ex);
                    }
                    throw new RuntimeException("Error updating order in database", e);
                }
            } catch (SQLException e) {
                throw new RuntimeException("Database connection error", e);
            }
        }, executorService);
    }

    @Override
    public CompletableFuture<Void> delete(int id) throws Exception {
        return CompletableFuture.runAsync(() -> {
            String sql = "DELETE FROM orders WHERE orderNumber = ?";

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
    public CompletableFuture<Void> create(Orders order) throws Exception {
        return CompletableFuture.runAsync(() -> {
            String sql = "INSERT INTO orders (orderId, productQuantity) VALUES (?, ?)";
            Connection conn = null;

            try {
                conn = dbConnector.getConnection();
                conn.setAutoCommit(false); // Start transaktion

                try (PreparedStatement statement = conn.prepareStatement(sql)) {
                    statement.setInt(1, order.getOrderId());
                    statement.setInt(2, order.getProductQuantity());

                    statement.executeUpdate(); // Udfør INSERT
                }

                conn.commit(); // Commit hvis alt lykkes
                conn.setAutoCommit(true); // (Valgfrit) gendan autocommit

            } catch (SQLException e) {
                if (conn != null) {
                    try {
                        conn.rollback(); // Rollback hvis fejl
                    } catch (SQLException rollbackEx) {
                        throw new RuntimeException("Rollback failed after insert error", rollbackEx);
                    }
                }
                throw new RuntimeException("Error inserting order into database", e);
            }
        }, executorService);
    }


    @Override
    public CompletableFuture<Orders> read(int id) throws Exception {
        return CompletableFuture.supplyAsync(() -> {
            String sql = "SELECT * FROM orders WHERE orderId = ?";
            try (Connection conn = dbConnector.getConnection();
                 PreparedStatement statement = conn.prepareStatement(sql)) {

                statement.setInt(1, id);
                ResultSet rs = statement.executeQuery();

                if (rs.next()) {
                    int orderId = rs.getInt("orderId");
                    int productQuantity = rs.getInt("productQuantity");
                    return new Orders(orderId, productQuantity);
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
            String sql = "SELECT * FROM orders";

            try (Connection conn = dbConnector.getConnection();
                 PreparedStatement statement = conn.prepareStatement(sql)) {
                ResultSet rs = statement.executeQuery(sql);

                while (rs.next()) {
                    int orderId = rs.getInt("orderId");
                    int productQuantity = rs.getInt("productQuantity");

                    Orders order = new Orders(orderId, productQuantity);
                    orders.add(order);
                }
                return orders;
            } catch (SQLException e) {
                throw new RuntimeException("Error fetching orders from database", e);
            }
        }, executorService);
    }
}