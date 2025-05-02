package dk.easv.belsign.GUI;


import dk.easv.belsign.BLL.Util.LoginValidator;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import java.io.IOException;

public class LoginController {
    @FXML
    private PasswordField txtPassword;
    @FXML
    private TextField txtEmail;
    @FXML
    private TextField txtShowPassword;
    @FXML
    private Button btnShowPw;

    private boolean isPwShown = false;



    public void initialize() {

        txtShowPassword.textProperty().bindBidirectional(txtPassword.textProperty());

        txtShowPassword.setVisible(false);
        txtShowPassword.setManaged(false);

        btnShowPw.setText("👀");

        //IM A FUCKING LIAR I AM USED!
        btnShowPw.setOnAction(e -> {
            isPwShown = !isPwShown;

            txtShowPassword.setVisible(isPwShown);
            txtShowPassword.setManaged(isPwShown);
            txtPassword.setVisible(!isPwShown);
            txtPassword.setManaged(!isPwShown);

            btnShowPw.setText(isPwShown ? "👀" : "🫣");
        });
}


    @FXML
    //
    private void onLogin(ActionEvent event) throws IOException {
        LoginValidator loginValidator = new LoginValidator();

        Boolean success = loginValidator.validateLogin(txtEmail.getText(), txtPassword.getText());

        if (success) {
            goToApp();
        } else {
            showError("Invalid email or password.");
        }
    }

    //temp metode til at sende videre til main app
    private void goToApp() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/dk/easv/belsign/Mainframe.fxml"));
            Parent root = fxmlLoader.load();

            Scene scene = new Scene(root);
            String cssPath = getClass().getResource("/dk/easv/belsign/style.css").toExternalForm();
            if (cssPath != null) {
                scene.getStylesheets().add(cssPath);
            } else {
                System.err.println("CSS file not found: /dk/easv/belsign/style.css");
            }

            Stage stage = (Stage) txtEmail.getScene().getWindow();
            stage.setTitle("Belsign");
            stage.setScene(scene);
            stage.setMaximized(true);
            stage.show();
            MainframeController mainframeController = fxmlLoader.getController();
            mainframeController.fillMainPane();

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

