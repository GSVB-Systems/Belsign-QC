package dk.easv.belsign.BLL;

import dk.easv.belsign.BE.Orders;
import dk.easv.belsign.DAL.IOrdersDataAccess;
import dk.easv.belsign.DAL.OrdersDAO;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

public class OrdersManager {

    private final IOrdersDataAccess ordersDataAccess;

    public OrdersManager() throws IOException {
        ordersDataAccess = new OrdersDAO();
    }

    public List<Orders> getAllOrders() {
        return ordersDataAccess.getAllOrders().join();
    }

    public void updateOrder(Orders order) {
        ordersDataAccess.updateOrder(order);
    }
}