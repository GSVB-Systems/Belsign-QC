package dk.easv.belsign.GUI;

import dk.easv.belsign.BLL.Util.CameraHandler;
import javafx.animation.AnimationTimer;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

public class CameraViewController implements Initializable {

    @FXML
    private ImageView camFeed;

    private AnimationTimer timer;
    private Image capturedImage;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        boolean opened = CameraHandler.getInstance().openCamera();
        if (!opened) {
            System.err.println("Cant Open!");
            return;
        }

        //starter timeren til at opdatere kamerafeedet
        timer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                Image frame = CameraHandler.getInstance().capturePic();
                if (frame != null) {
                    camFeed.setImage(frame);
                }
            }
        };
        //viser feedet i imageviewet
        timer.start();
    }

    @FXML
    private void onCap() {
        timer.stop();
        capturedImage = camFeed.getImage();
        CameraHandler.getInstance().releaseCam();
        close();
    }

    private void close() {
        Stage stage = (Stage) camFeed.getScene().getWindow();
        stage.close();
    }

    public Image getCapturedImage() {
        return capturedImage;
    }
}
