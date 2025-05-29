package dk.easv.belsign.GUI;

import dk.easv.belsign.BE.Users;
import dk.easv.belsign.BLL.Util.ExceptionHandler;
import dk.easv.belsign.BLL.Util.SceneService;
import dk.easv.belsign.Models.OrdersModel;
import dk.easv.belsign.Models.PhotosModel;
import dk.easv.belsign.Models.ProductsModel;
import dk.easv.belsign.Models.UsersModel;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
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
    @FXML
    private TableView<Users> tblUsers;
    @FXML
    private TableColumn colId;
    @FXML
    private TableColumn colRole;
    @FXML
    private TableColumn colFirstName;
    @FXML
    private TableColumn colLastName;
    @FXML
    private TableColumn colEmail;




    private UsersModel usersModel;

    public void initialize() throws Exception {
        // Initialize the tables and load data

        try {

            this.usersModel = new UsersModel();

        } catch (Exception e) {
            showError("Failed to initialize OrdersModel - Contact System Administrator");
        }

        DisplayUsers();
    }

    private void DisplayUsers() {
        tblUsers.setItems(usersModel.getObservableUsers());
        colId.setCellValueFactory(new PropertyValueFactory<>("userId"));
        colRole.setCellValueFactory(new PropertyValueFactory<>("roleId"));
        colFirstName.setCellValueFactory(new PropertyValueFactory<>("firstName"));
        colLastName.setCellValueFactory(new PropertyValueFactory<>("lastName"));
        colEmail.setCellValueFactory(new PropertyValueFactory<>("email"));

    }

    @Override
    public void setParent(MainframeController parent) {
        this.parent = parent;
        parent.setBtnLogout(true);
    }

    public void onCreateUserButtonClicked(ActionEvent actionEvent) {
        try {
            String fxmlPath = "/dk/easv/belsign/CreateUserFrame.fxml";
            SceneService.openModalWindow(fxmlPath, "Create User", 300, 400);

            // After window closes, refresh the users
            refreshUsers();
        } catch (Exception e) {
            ExceptionHandler.handleUnexpectedException(e);
            showError("Failed to open the Create User window");
        }
    }



    @FXML
    private void openAdminFrame(ActionEvent actionEvent) {
        try {
            String fxmlPath = "/dk/easv/belsign/AdminFrame.fxml";
            SceneService.loadCenterContent((StackPane) parent.getMainPane(), fxmlPath, parent);
        } catch (Exception e) {
            ExceptionHandler.handleUnexpectedException(e);
            showError("Failed to load the Admin Frame, if problem persists you should contact IT");
        }
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Admin Alert");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public void onDeleteUserButtonClicked(ActionEvent actionEvent) {
        try {
            Users selectedUser = tblUsers.getSelectionModel().getSelectedItem();
            if (selectedUser == null) {
                showError("Please select a user to delete.");
                return;
            }

            // Use SceneService.load to get both Parent view and controller
            SceneService.ViewTuple<DeleteUserFrameController> tuple = SceneService.load("/dk/easv/belsign/DeleteUserFrame.fxml");
            DeleteUserFrameController deleteController = tuple.controller;

            // Pass the selected user ID and this parent controller to the delete controller
            deleteController.setUserIdToDelete(selectedUser.getUserId());
            deleteController.setParentController(this);

            // Create a new Stage for the modal window
            Stage stage = new Stage();
            stage.setTitle("Delete User");
            stage.setScene(new Scene(tuple.view, 400, 300));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setResizable(false);

            // Show and wait until the modal window is closed
            stage.showAndWait();

            // Refresh users after the modal closes
            refreshUsers();

        } catch (Exception e) {
            ExceptionHandler.handleUnexpectedException(e);
            showError("Failed to open the Delete User window");
        }
    }

    public void refreshUsers() {
        try {
            // Clear existing observableUsers and add updated users
            var newUsers = usersModel.getAllUsersFromDB();
            usersModel.getObservableUsers().clear();
            usersModel.getObservableUsers().addAll(newUsers);

            // Optionally call refresh on the TableView
            tblUsers.refresh();

        } catch (Exception e) {
            ExceptionHandler.handleUnexpectedException(e);
            showError("Failed to refresh users");
        }
    }
    public void onEditUserButtonClicked(ActionEvent actionEvent) {
        try {
            Users selectedUser = tblUsers.getSelectionModel().getSelectedItem();
            if (selectedUser == null) {
                showError("Please select a user to edit.");
                return;
            }

            // Load the FXML and controller
            SceneService.ViewTuple<EditUserFrameController> tuple = SceneService.load("/dk/easv/belsign/EditUserFrame.fxml");
            EditUserFrameController editController = tuple.controller;

            // Pass selected user and parent controller to edit controller
            editController.setUserToEdit(selectedUser);
            editController.setParentController(this);

            // Open modal window
            Stage stage = new Stage();
            stage.setTitle("Edit User");
            stage.setScene(new Scene(tuple.view, 300, 400));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setResizable(false);
            stage.showAndWait();

            // Refresh table after edit
            refreshUsers();

        } catch (Exception e) {
            ExceptionHandler.handleUnexpectedException(e);
            showError("Failed to open the Edit User window");
        }
    }

}
