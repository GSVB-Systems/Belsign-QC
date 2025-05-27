package dk.easv.belsign.GUI;

import dk.easv.belsign.BLL.Util.OrderValidator;
import dk.easv.belsign.BLL.Util.SceneService;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.StackPane;

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
            String orderInput = txtSearch.getText();
            OrderValidator orderValidator = new OrderValidator();

            if (orderValidator.validateOrder(Integer.parseInt(orderInput))) {
                parent.setOrder(orderInput);
                String fxmlPath = "/dk/easv/belsign/ProductFrame.fxml";
                SceneService.loadCenterContent((StackPane) parent.getMainPane(), fxmlPath, parent);
            } else {
                throw new IllegalArgumentException("Invalid order ID");
            }
        } catch (NumberFormatException e) {
            showError("Invalid input: Please enter a numeric order ID.");
        } catch (IllegalArgumentException e) {
            showError(e.getMessage());
        } catch (Exception e) {
            showError("Order not found. Make sure you type the correct order ID without dashes.");
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
