package dk.easv.belsign.GUI;

import dk.easv.belsign.Belsign;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.IOException;


public class MainframeController {

    @FXML
        private Pane mainPane;
        @FXML
        private Text txtOrder;
        @FXML
        private Button btnLogOut;
        @FXML
        private ImageView imgLogout;
        public void initialize() {
            String orderText = "Belman";
            setOrder(orderText);
        }

        public void setOrder(String order) {
            txtOrder.setText(order);
        }

        public void setBtnLogout(Boolean visible) {
            btnLogOut.setVisible(visible);
            imgLogout.setVisible(visible);
        }

        public void fillMainPane(FXMLLoader loader) {
            try {

                Parent root = loader.load();

                Object controller = loader.getController();
                if (controller instanceof IParentAware) {
                    ((IParentAware) controller).setParent(this);
                }

                mainPane.getChildren().clear();
                mainPane.getChildren().add(root);

            } catch (
                    IOException e) {
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

    @FXML
    private void handleLogOut(ActionEvent event) throws IOException {
        try {
            fillMainPane(new FXMLLoader(getClass().getResource("/dk/easv/belsign/Login.fxml")));

            setOrder("Belman");
        } catch (Exception e) {
            showError("Failed to log out: " + e.getMessage());
        }
    }
}

