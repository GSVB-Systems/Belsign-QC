package dk.easv.belsign.DAL;

import dk.easv.belsign.BE.Orders;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class OrdersDAO implements IOrdersDataAccess{

    private DBConnector dbConnector = new DBConnector();

    public OrdersDAO() throws IOException {
        this.dbConnector = new DBConnector();
    }

    @Override
    public List<Orders> getAllOrders() throws SQLException {

        ArrayList<Orders> orders = new ArrayList<>();
        String sql = "SELECT * FROM orders";

        try(Connection conn = dbConnector.getConnection();
            PreparedStatement statement = conn.prepareStatement(sql)){
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
}
