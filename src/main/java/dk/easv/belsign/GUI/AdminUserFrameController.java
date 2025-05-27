package dk.easv.belsign.GUI;

import dk.easv.belsign.BLL.Util.ExceptionHandler;
import dk.easv.belsign.BLL.Util.SceneService;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;

public class AdminUserFrameController implements IParentAware {
    private MainframeController parent;
    @FXML
    private Button btnCreateUser;
    @FXML
    private Button btnEditUser;
    @FXML
    private Button btnDeleteUser;

    @Override
    public void setParent(MainframeController parent) {
        this.parent = parent;
        parent.setBtnLogout(true);
    }

    public void onCreateUserButtonClicked(ActionEvent actionEvent) {
        try {
            String fxmlPath = "/dk/easv/belsign/CreateUserFrame.fxml";
            SceneService.openModalWindow(fxmlPath, "Create User", 300, 400);
        } catch (Exception e) {
            ExceptionHandler.handleUnexpectedException(e);
            showError("Failed to open the Create User window: " + e.getMessage());
        }
    }

    @FXML
    private void openAdminFrame(ActionEvent actionEvent) {
        try {
            String fxmlPath = "/dk/easv/belsign/AdminFrame.fxml";
            SceneService.loadCenterContent((StackPane) parent.getMainPane(), fxmlPath, parent);
        } catch (Exception e) {
            ExceptionHandler.handleUnexpectedException(e);
            showError("Failed to load the Admin Frame, you should probably contact IT : " + e.getMessage());
        }
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Admin Alert");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
