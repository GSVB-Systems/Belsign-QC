package dk.easv.belsign.GUI;

import com.sun.tools.javac.Main;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.awt.event.ActionEvent;
import java.io.IOException;

public class OrderSelectionController {
    @FXML
    private Button btnSearch;
    @FXML
    private TextField txtSearch;

    private MainframeController mainframeController;

    public void initialize() {
        btnSearch.setText("🔍");

    }
    private String order;

    public void setParent(MainframeController mainframeController) {
        this.mainframeController = mainframeController;
    }
    public void onSearchButtonClick(javafx.event.ActionEvent actionEvent) {

        order = txtSearch.getText();
        mainframeController.setOrder(order);
        goToApp();
    }
    private void goToApp() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/dk/easv/belsign/Login.fxml"));
            Parent root = fxmlLoader.load();

            Scene scene = new Scene(root);
            String cssPath = getClass().getResource("/dk/easv/belsign/style.css").toExternalForm();
            if (cssPath != null) {
                scene.getStylesheets().add(cssPath);
            } else {
                System.err.println("CSS file not found: /dk/easv/belsign/style.css");
            }

            Stage stage = (Stage) txtSearch.getScene().getWindow();
            stage.setScene(scene);
            stage.show();

        } catch (IOException e) {
            showError("Failed to go to real app, sums up");
        }
    }
    //Til Exception handeling - prompter en Alarm popup til GUI
    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Login Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }


}
