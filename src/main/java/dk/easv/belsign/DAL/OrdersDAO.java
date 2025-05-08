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

public class OrdersDAO implements IOrdersDataAccess {

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
    public CompletableFuture<List<Orders>> getAllOrders() {
        return CompletableFuture.supplyAsync(() -> {
            ArrayList<Orders> orders = new ArrayList<>();
            String sql = "SELECT * FROM orders";

            try (Connection conn = dbConnector.getConnection();
                 PreparedStatement statement = conn.prepareStatement(sql)) {
                ResultSet rs = statement.executeQuery(sql);

                while (rs.next()) {
                    String orderId = rs.getString("orderId");
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

    @Override
    public CompletableFuture<Void> updateOrder(Orders order) {
        return CompletableFuture.runAsync(() -> {
            String sql = "UPDATE orders SET productQuantity = ? WHERE orderId = ?";

            try (Connection conn = dbConnector.getConnection()) {
                conn.setAutoCommit(false);

                try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                    stmt.setInt(1, order.getProductQuantity());
                    stmt.setString(2, order.getOrderId());

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

    public void shutdown() {
        executorService.shutdown();
    }
}