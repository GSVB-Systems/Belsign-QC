package dk.easv.belsign.GUI;

import dk.easv.belsign.Models.UsersModel;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class DeleteUserFrameController {

    @FXML private Button btnDecline;
    @FXML private Button btnConfirm;
    @FXML private Text txtConfirm;

    private final UsersModel usersModel;
    private AdminUserFrameController parentController;
    private int userIdToDelete;

    // Constructor - instantiate UsersModel here
    public DeleteUserFrameController() throws Exception {
        this.usersModel = new UsersModel();
    }

    // Setter for parent controller
    public void setParentController(AdminUserFrameController parentController) {
        this.parentController = parentController;
    }

    // Setter for the user ID to delete and update confirmation text
    public void setUserIdToDelete(int userId) {
        this.userIdToDelete = userId;
        txtConfirm.setText("Are you sure you want to delete user ID: " + userId + "?");
    }

    @FXML
    public void onDeclineButtonClicked(ActionEvent actionEvent) {
        ((Stage) btnDecline.getScene().getWindow()).close();
    }

    @FXML
    public void onConfirmButtonClicked(ActionEvent actionEvent) {
        try {
            usersModel.deleteUser(userIdToDelete);
            if (parentController != null) {
                parentController.refreshUsers();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        ((Stage) btnConfirm.getScene().getWindow()).close();
    }
}
