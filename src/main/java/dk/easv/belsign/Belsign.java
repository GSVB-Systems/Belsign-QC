package dk.easv.belsign;

import dk.easv.belsign.GUI.MainframeController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.IOException;

import dk.easv.belsign.BLL.Util.ThreadShutdownUtil;

public class Belsign extends Application {

    @Override
    public void start(Stage stage) throws IOException {
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

            stage.setTitle("Belsign");
            stage.getIcons().add(new Image(getClass().getResourceAsStream("/dk/easv/belsign/images/BelmanLogoLines.png")));
            stage.setScene(scene);
            stage.setMaximized(true);
            stage.show();
            MainframeController mainframeController = fxmlLoader.getController();
            mainframeController.fillMainPane(new FXMLLoader(getClass().getResource("/dk/easv/belsign/Login.fxml")));

        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Failed to load the main application window.");
        }
    }

    public static void main(String[] args) {
        launch();
    }

    @Override
    public void stop() {
        // Explicitly shutdown all registered executor services
        ThreadShutdownUtil.getInstance().shutdownAll();
        System.out.println("Application stopped");
    }
}