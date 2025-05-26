package dk.easv.belsign.GUI;


import com.itextpdf.layout.element.List;
import com.itextpdf.layout.element.Table;

import dk.easv.belsign.BE.Orders;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

import dk.easv.belsign.BE.Orders;


public class AdminOrderController implements IParentAware {

    private Orders order;

    private MainframeController parent;
    @FXML
    private TableView<Order> tblOrder;
    private TableView tblProducts;
    private TableView tblInformation;
    @FXML
    private TableColumn<Order, Integer> colOrderId;

    @Override
    public void setParent(MainframeController parent) {
        this.parent = parent;
    }

    public void initialize() {
        // Set up columns with property names matching Orders getters
        colOrderId.setCellValueFactory(new PropertyValueFactory<>("orderId"));

        loadOrders();
    }

    private void loadOrders() {
        // Get list from DAL, then update the TableView
        List<order> orders = database.getOrders();
        tblOrder.getItems().setAll(orders);
    }

}
