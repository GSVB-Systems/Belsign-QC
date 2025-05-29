package dk.easv.belsign.BLL;

import dk.easv.belsign.BE.Orders;
import dk.easv.belsign.DAL.ICrudRepo;
import dk.easv.belsign.DAL.IOrderDAO;
import dk.easv.belsign.DAL.OrdersDAO;

import java.io.IOException;
import java.util.List;

public class OrdersManager {

    private final IOrderDAO ordersDataAccess;

    public OrdersManager() throws IOException {
        ordersDataAccess = new OrdersDAO();
    }

    public List<Orders> getAllOrders() throws Exception {
        return (List<Orders>) ordersDataAccess.readAll().join();
    }

    public void updateOrder(Orders order) throws Exception {
        ordersDataAccess.update(order);
    }

    public Orders getOrderById(int orderId) throws Exception {
        return (Orders) ordersDataAccess.read(orderId).join();
    }

    public void createOrderApproval(Orders order) throws Exception {
        ordersDataAccess.createOrderApproval(order);
    }

    public void createOrder(Orders order) throws Exception {
        ordersDataAccess.create(order);
    }

    public void deleteOrder(int selectedOrderId) throws Exception {
        ordersDataAccess.delete(selectedOrderId);
    }
}