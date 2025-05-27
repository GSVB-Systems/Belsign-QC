package dk.easv.belsign.GUI;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;

public class AdminUserFrameController {
    @FXML
    private Button btnCreateUser;
    @FXML
    private Button btnEditUser;
    @FXML
    private Button btnDeleteUser;


    public void onCreateUserButtonClicked(ActionEvent actionEvent) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/dk/easv/belsign/CreateUserFrame.fxml"));
        Parent root = fxmlLoader.load();
        Scene scene = new Scene(root);
        Stage stage = new Stage();
        stage.setScene(scene);
        stage.setTitle("Create User");
        stage.setResizable(false);
        stage.setWidth(300);
        stage.setHeight(400);

        stage.initModality(Modality.APPLICATION_MODAL);
        stage.show();
    }
}
