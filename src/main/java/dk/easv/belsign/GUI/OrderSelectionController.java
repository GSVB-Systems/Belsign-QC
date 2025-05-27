package dk.easv.belsign.GUI;

import dk.easv.belsign.BLL.Util.OrderValidator;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;

import java.io.IOException;

public class OrderSelectionController implements IParentAware {
    @FXML
    private Button btnSearch;
    @FXML
    private TextField txtSearch;

    private MainframeController mainframeController;

    private MainframeController parent;

    @Override
    public void setParent(MainframeController parent) {
        this.parent = parent;
        parent.setBtnLogout(true);
    }

    public void initialize() {
        btnSearch.setText("🔍");
        txtSearch.setOnKeyPressed(event -> {
            if (event.getCode().toString().equals("ENTER")) {
                onSearchButtonClick(new ActionEvent());
            }
        });

    }
    private String order;

    public void onSearchButtonClick(javafx.event.ActionEvent actionEvent) {
        try {
            OrderValidator orderValidator = new OrderValidator();


            order = txtSearch.getText();
            if (orderValidator.validateOrder(Integer.parseInt(order))) {
                parent.setOrder(order);
                parent.fillMainPane(new FXMLLoader(getClass().getResource("/dk/easv/belsign/ProductFrame.fxml")));

            } else throw new Exception();


        } catch (Exception e) {
            showError("Order not found, make sure you type the right orderID - remove dashes.");
        }
    }
    //Til Exception handeling - prompter en Alarm popup til GUI
    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Order error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
