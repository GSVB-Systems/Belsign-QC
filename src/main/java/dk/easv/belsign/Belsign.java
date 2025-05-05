package dk.easv.belsign;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

import dk.easv.belsign.BLL.Util.ThreadShutdownUtil;

public class Belsign extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(Belsign.class.getResource("Login.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        stage.setTitle("templogin");
        stage.setScene(scene);
        stage.show();
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