package dk.easv.belsign.GUI;

import dk.easv.belsign.BE.Photos;
import dk.easv.belsign.BLL.Util.CameraHandler;
import dk.easv.belsign.BLL.Util.OrderSession;
import dk.easv.belsign.BLL.Util.PhotoSession;
import dk.easv.belsign.BLL.Util.ProductSession;
import javafx.animation.AnimationTimer;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import org.opencv.photo.Photo;

import java.net.URL;
import java.util.ResourceBundle;

public class CameraViewController implements Initializable {

    @FXML
    private ImageView camFeed;
    @FXML
    private StackPane feedPane;
    private AnimationTimer timer;
    private Image capturedImage;
    private int orderId;
    private int productId;
    private String photoType;
    private Photos capturedPhoto;


    @Override
    public void initialize(URL location, ResourceBundle resources) {

        //Sizing til ImageView
        camFeed.fitWidthProperty().bind(feedPane.widthProperty());
        camFeed.fitHeightProperty().bind(feedPane.heightProperty());

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

        int orderId = OrderSession.getEnteredOrder().getOrderId();
        int productId = ProductSession.getEnteredProduct().getProductId();
        String photoName = PhotoSession.getCurrentPhoto().getPhotoName();

        String path = CameraHandler.getInstance().saveImagesToOrders(capturedImage, orderId, productId, photoName);
        close();

        if (path != null) {

            PhotoSession.getCurrentPhoto().setPhotoPath(path);


            // 2. Optional: store in controller too
            capturedPhoto = new Photos();
            capturedPhoto.setPhotoPath(path);
            capturedPhoto.setPhotoName(photoName);
            capturedPhoto.setProductId(productId);

        }
    }


    private void close() {
        Stage stage = (Stage) camFeed.getScene().getWindow();
        stage.close();
    }

    public Image getCapturedImage() {
        return capturedImage;
    }

    public Photos getCapturedPhoto() {
        return capturedPhoto;
    }

}
