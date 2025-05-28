    package dk.easv.belsign.GUI;

    import dk.easv.belsign.BE.Users;
    import dk.easv.belsign.Models.UsersModel;
    import javafx.fxml.FXML;
    import javafx.scene.control.*;
    import javafx.stage.Stage;

    public class EditUserFrameController {
        @FXML private TextField txtfirstNameField;
        @FXML private TextField txtlastNameField;
        @FXML private TextField txtemailField;
        @FXML private PasswordField txtpasswordField;
        @FXML private ComboBox<String> roleComboBox;
        @FXML private Button btnsubmit;

        private Users userToEdit;
        private UsersModel usersModel;
        private AdminUserFrameController parentController;

        public EditUserFrameController() {
            try {
                usersModel = new UsersModel();
            } catch (Exception e) {
                showError("Error initializing user model: " + e.getMessage());
            }
        }

        public void setUserToEdit(Users user) {
            this.userToEdit = user;
            txtfirstNameField.setText(user.getFirstName());
            txtlastNameField.setText(user.getLastName());
            txtemailField.setText(user.getEmail());
            txtpasswordField.setText(""); // Optional: Clear password

            // Map roleId to role name
            roleComboBox.setValue(user.getRoleId() == 1 ? "Operator" : "Quality Control");
        }

        public void setParentController(AdminUserFrameController controller) {
            this.parentController = controller;
        }

        @FXML
        public void onSubmitButtonClicked() {
            try {
                userToEdit.setFirstName(txtfirstNameField.getText());
                userToEdit.setLastName(txtlastNameField.getText());
                userToEdit.setEmail(txtemailField.getText());

                // Check if password field is not empty → update + rehash via manager
                if (!txtpasswordField.getText().isEmpty()) {
                    userToEdit.setHashedPassword(txtpasswordField.getText()); // plain password passed, manager will hash
                }

                userToEdit.setRoleId("Operator".equals(roleComboBox.getValue()) ? 1 : 2);

                usersModel.updateUser(userToEdit); // delegates to manager → hashes → sends to DAL

                // Close dialog
                ((Stage) btnsubmit.getScene().getWindow()).close();

                // Refresh list
                if (parentController != null) parentController.refreshUsers();

            } catch (Exception e) {
                showError("Could not update user: " + e.getMessage());
                e.printStackTrace();
            }
        }


        private void showError(String message) {
            Alert alert = new Alert(Alert.AlertType.ERROR, message, ButtonType.OK);
            alert.showAndWait();
        }
    }
