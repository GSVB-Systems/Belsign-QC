package dk.easv.belsign.BLL.Util;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;

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

    //selve loaderen
    public static <T> ViewTuple<T> load(String fxmlPath) {
        try{
            FXMLLoader loader = new FXMLLoader(SceneService.class.getResource(fxmlPath));
            Parent root = loader.load();
            T controller = loader.getController();
            return new ViewTuple<>(root, controller);
        } catch (IOException e) {
            throw new RuntimeException("Couldnt load FXML" + fxmlPath, e);
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

    //til fullscreen, kommer til at blive brugt sammen med cam.
    public static <T> T fullscreen(String fxmlPath, String title) {
        ViewTuple<T> tuple = load(fxmlPath);
        Stage stage = new Stage();
        stage.setTitle(title);
        stage.setScene(new Scene(tuple.view));
        stage.setFullScreen(true);
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.showAndWait();
        return tuple.controller;
    }

}
