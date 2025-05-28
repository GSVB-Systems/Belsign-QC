package dk.easv.belsign.GUI;

import javafx.fxml.FXML;
import javafx.scene.control.*;

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

    @FXML
    private void onSubmitButtonClicked() {
        String firstName = txtfirstNameField.getText().trim();
        String lastName = txtlastNameField.getText().trim();
        String role = roleComboBox.getValue();
        String email = txtemailField.getText().trim();
        String password = txtpasswordField.getText();

        // Basic validation
        if (firstName.isEmpty() || lastName.isEmpty() || role == null || email.isEmpty() || password.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Missing Information", "Please fill in all fields.");
            return;
        }
        showAlert(Alert.AlertType.INFORMATION, "User Created", "User " + firstName + " " + lastName + " has been created successfully.");
    }
    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
