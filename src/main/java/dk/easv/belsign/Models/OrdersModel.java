package dk.easv.belsign.Models;

import dk.easv.belsign.BE.Orders;
import dk.easv.belsign.BLL.OrdersManager;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.io.IOException;
import java.sql.SQLException;

public class OrdersModel {

    private final ObservableList<Orders> observableOrders;
    private final OrdersManager ordersManager;

    public OrdersModel() throws Exception {
        this.ordersManager = new OrdersManager();
        observableOrders = FXCollections.observableArrayList();

    }

    public ObservableList<Orders> getObservableOrders() throws Exception {
        observableOrders.addAll(ordersManager.getAllOrders());
        return observableOrders;
    }

    public void createOrder(Orders order) throws Exception {
        ordersManager.createOrder(order);

    }

    public void updateOrder(Orders order) throws Exception {
        ordersManager.updateOrder(order);
    }

    public void createOrderApproval(Orders order) throws Exception {
        ordersManager.createOrderApproval(order);
    }

    public Orders getOrderById(int orderId) throws Exception {
        return ordersManager.getOrderById(orderId);
    }

    public void deleteOrder(int selectedOrderId) throws Exception {
        ordersManager.deleteOrder(selectedOrderId);
    }
}


