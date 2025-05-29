package dk.easv.belsign.GUI;

import dk.easv.belsign.BE.Users;
import dk.easv.belsign.BLL.Util.ExceptionHandler;
import dk.easv.belsign.Models.UsersModel;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

public class CreateUserFrameController {
    @FXML
    private TextField txtfirstNameField;

    @FXML
    private TextField txtlastNameField;

    @FXML
    private ComboBox<String> roleComboBox;

    @FXML
    private TextField txtemailField;

    @FXML
    private PasswordField txtpasswordField;

    @FXML
    private Button btnsubmit;

    private final UsersModel usersModel;

    private AdminUserFrameController parentController;

    public CreateUserFrameController() throws Exception {
        this.usersModel = new UsersModel();
    }

    public void setParentController(AdminUserFrameController parentController) {
        this.parentController = parentController;
    }
    @FXML
    private void onSubmitButtonClicked() {
        String firstName = txtfirstNameField.getText().trim();
        String lastName = txtlastNameField.getText().trim();
        String role = roleComboBox.getValue();
        String email = txtemailField.getText().trim();
        String password = txtpasswordField.getText();


        if (firstName.isEmpty() || lastName.isEmpty() || role == null || email.isEmpty() || password.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Missing Information", "Please fill in all fields.");
            return;
        }
        int roleId = role.equals("Operator") ? 1 : 2;
        Users newUser = new Users(roleId, firstName, lastName, email, password);

        try {
            usersModel.createUser(newUser);
            showAlert(Alert.AlertType.INFORMATION, "User Created", "User has been created");
        } catch (Exception e) {
            ExceptionHandler.handleUnexpectedException(e);
            showError("Failed to open the Create User window");
        }
        if (parentController != null) {
            parentController.refreshUsers();
        }
        Stage stage = (Stage) btnsubmit.getScene().getWindow();
        stage.close();
    }
    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Admin Alert");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
