package dk.easv.belsign.DAL;

import dk.easv.belsign.BE.Orders;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class OrdersDAO implements IOrdersDataAccess {

    private final DBConnector dbConnector;

    public OrdersDAO() throws IOException {
        this.dbConnector = DBConnector.getInstance();
    }

    @Override
    public List<Orders> getAllOrders() throws SQLException {

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
            throw new SQLException("Error fetching orders from database", e);
        }

    }

    @Override
    public void updateOrder(Orders order) throws SQLException {

        String sql = "UPDATE orders SET productQuantity = ? WHERE orderId = ?";

        try (Connection conn = dbConnector.getConnection()) {
            conn.setAutoCommit(false);

            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setInt(1, order.getProductQuantity());
                stmt.setString(2, order.getOrderId());

                stmt.executeUpdate();
            } catch (SQLException e) {
                conn.rollback();
                throw new SQLException("Error updating order in database", e);
            }
            conn.commit();
        }
    }
}