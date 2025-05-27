package dk.easv.belsign.BLL.Util;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.logging.Level;

public class SceneService {

    //Tuple class til at returne parent og controller.
    public static class ViewTuple<T> {
        public final Parent view;
        public final T controller;

        public ViewTuple(Parent view, T controller) {
            this.view = view;
            this.controller = controller;
        }
    }

    public static <T> ViewTuple<T> load(String fxmlPath) {
        try {
            FXMLLoader loader = new FXMLLoader(SceneService.class.getResource(fxmlPath));
            Parent root = loader.load();
            T controller = loader.getController();
            return new ViewTuple<>(root, controller);
        } catch (IOException e) {
            ExceptionHandler.getLogger().log(Level.SEVERE, "Failed to load FXML: " + fxmlPath, e);
            throw new RuntimeException("Couldn't load FXML: " + fxmlPath, e);
        }
    }


    //til et lille popup vindue
    public static <T> T openSmallPopup(String fxmlPath, String title) {
        ViewTuple<T> tuple = load(fxmlPath);
        Stage stage = new Stage();
        stage.setTitle(title);
        stage.setScene(new Scene(tuple.view));
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.showAndWait();
        return tuple.controller;
    }

    public static <T> T fullscreen(String fxmlPath, String title) {
        try {
            URL resource = SceneService.class.getResource("/dk/easv/belsign/" + fxmlPath);
            if (resource == null) {
                String msg = "FXML file not found: " + fxmlPath;
                ExceptionHandler.getLogger().severe(msg);
                throw new RuntimeException(msg);
            }

            FXMLLoader loader = new FXMLLoader(resource);
            Parent root = loader.load();
            T controller = loader.getController();

            Stage stage = new Stage();
            stage.setTitle(title);
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setMaximized(true);
            stage.showAndWait();

            return controller;
        } catch (IOException e) {
            ExceptionHandler.getLogger().log(Level.SEVERE, "Error loading fullscreen FXML: " + fxmlPath, e);
            throw new RuntimeException("Error loading FXML: " + fxmlPath, e);
        }
    }
}
