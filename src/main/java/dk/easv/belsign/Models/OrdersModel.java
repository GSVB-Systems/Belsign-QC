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
        observableOrders.addAll(ordersManager.getAllOrders());
    }

    public ObservableList<Orders> getObservableOrders() {
        return observableOrders;
    }

    public void updateOrder(Orders order) throws Exception {
        ordersManager.updateOrder(order);
    }

    public Orders getOrderById(int orderId) throws Exception {
        return ordersManager.getOrderById(orderId);
    }
}


