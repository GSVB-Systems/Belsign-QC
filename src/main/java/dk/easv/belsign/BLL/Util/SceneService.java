package dk.easv.belsign.BLL.Util;

import dk.easv.belsign.GUI.IParentAware;
import dk.easv.belsign.GUI.MainframeController;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.layout.StackPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.logging.Level;

public class SceneService {

    private static Object lastLoadedController;
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


    public static Stage openModalWindow(String fxmlPath, String title, int width, int height) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(SceneService.class.getResource(fxmlPath));
        Parent root = fxmlLoader.load();
        Scene scene = new Scene(root, width, height);
        Stage stage = new Stage();
        stage.setScene(scene);
        stage.setTitle(title);
        stage.setResizable(false);
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.show();
        return stage;
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

    public static void loadCenterContent(StackPane mainPane, String fxmlPath, MainframeController parent) {
        try {
            FXMLLoader loader = new FXMLLoader(SceneService.class.getResource(fxmlPath));
            Parent view = loader.load();

            lastLoadedController = loader.getController();
            Object controller = loader.getController();
            if (controller instanceof IParentAware) {
                ((IParentAware) controller).setParent(parent);
            }

            mainPane.getChildren().clear();
            mainPane.getChildren().add(view);

        } catch (IOException e) {
            ExceptionHandler.handleUnexpectedException(e);
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText(null);
            alert.setContentText("Failed to load view: " + e.getMessage());
            alert.showAndWait();
        }
    }

    public static Object getLastLoadedController() {
        return lastLoadedController;
    }
}
