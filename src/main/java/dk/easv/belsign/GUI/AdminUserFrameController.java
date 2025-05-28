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

    public void onDeleteUserButtonClicked(ActionEvent actionEvent) {
        try {
            String fxmlPath = "/dk/easv/belsign/DeleteUserFrame.fxml";
            SceneService.openModalWindow(fxmlPath, "Delete User", 300, 200);
        } catch (Exception e) {
            ExceptionHandler.handleUnexpectedException(e);
            showError("Failed to open the Delete User window");
        }
    }
}
