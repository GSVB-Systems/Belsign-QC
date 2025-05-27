package dk.easv.belsign.GUI;


import dk.easv.belsign.BE.Users;
import dk.easv.belsign.BLL.Util.ExceptionHandler;
import dk.easv.belsign.BLL.Util.LoginValidator;
import dk.easv.belsign.BLL.Util.OrderValidator;
import dk.easv.belsign.BLL.Util.UserSession;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import java.io.IOException;

public class LoginController implements IParentAware {
    @FXML
    private PasswordField txtPassword;
    @FXML
    private TextField txtId;
    @FXML
    private TextField txtShowPassword;
    @FXML
    private Button btnShowPw;

    private boolean isPwShown = false;

    private MainframeController parent;

    @Override
    public void setParent(MainframeController parent) {
        this.parent = parent;
        parent.setBtnLogout(false);
    }


    public void initialize() {

        ImageView imageView = new ImageView();
        txtShowPassword.textProperty().bindBidirectional(txtPassword.textProperty());

        txtShowPassword.setVisible(false);
        txtShowPassword.setManaged(false);

        imageView.setFitHeight(24);
        imageView.setFitWidth(24);
        imageView.setImage(new Image("/dk/easv/belsign/images/passVisible.png"));
        btnShowPw.setGraphic(imageView);

        //IM A FUCKING LIAR I AM USED!
        btnShowPw.setOnAction(e -> {
            isPwShown = !isPwShown;

            txtShowPassword.setVisible(isPwShown);
            txtShowPassword.setManaged(isPwShown);
            txtPassword.setVisible(!isPwShown);
            txtPassword.setManaged(!isPwShown);

            if (isPwShown) {
                imageView.setImage(new Image("/dk/easv/belsign/images/passNotVisible.png"));
            } else {
                imageView.setImage(new Image("/dk/easv/belsign/images/passVisible.png"));
            }
        });

        txtPassword.setOnKeyPressed(event -> {
            if (event.getCode().toString().equals("ENTER")) {
                try {
                    onLogin(new ActionEvent());
                } catch (IOException e) {
                    showError("Invalid ID or password.");
                }
            }
        });
    }
    
    @FXML
    //
    private void onLogin(ActionEvent event) throws IOException {
        try {
            LoginValidator loginValidator = new LoginValidator();

            Boolean success = loginValidator.validateLogin(Integer.parseInt(txtId.getText()),
                    txtPassword.getText()
            );

            if (success) {
                goToApp();
            } else {
                showError("Invalid ID or password.");
            }

        } catch (Exception e) {
            ExceptionHandler.handleUnexpectedException(e);
            showError("Input Error: Use your UserID to login");
        }
    }

    //temp metode til at sende videre til main app
    private void goToApp() {
        try {
            if (UserSession.getLoggedInUser().getRoleId() == 3) {
                parent.fillMainPane(new FXMLLoader(getClass().getResource("/dk/easv/belsign/AdminFrame.fxml")));
            } else {
                parent.fillMainPane(new FXMLLoader(getClass().getResource("/dk/easv/belsign/OrderSelection.fxml")));
            }
        } catch (Exception e) {
            ExceptionHandler.handleUnexpectedException(e);
            showError("Failed to load main Application, contact system administrator: " + e.getMessage());
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

