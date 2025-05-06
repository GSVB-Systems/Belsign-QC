package dk.easv.belsign.GUI;

import dk.easv.belsign.BLL.Util.CameraHandler;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import org.bytedeco.javacv.Frame;
import org.bytedeco.javacv.FrameGrabber;
import org.bytedeco.javacv.OpenCVFrameGrabber;

import java.awt.*;

public class OperatorFrameController {
    private MainframeController mainframeController;

    @FXML
    private ImageView imgFront;
    @FXML
    private ImageView imgRight;
    @FXML
    private ImageView imgLeft;
    @FXML
    private ImageView imgBack;
    @FXML
    private ImageView imgTop;

    @FXML
    private void handleUpload(ActionEvent event) {
    }

    public void setParent(MainframeController mainframeController) {
        this.mainframeController = mainframeController;
    }

    @FXML
    private void onCapture() {
        try (OpenCVFrameGrabber grabber = new OpenCVFrameGrabber(0)) { // 0 for default camera
            grabber.start(); // Start the grabber
            Frame frame = grabber.grab(); // Grab a frame

            if (frame != null) {
                // Convert Frame to BufferedImage
                java.awt.image.BufferedImage bufferedImage = org.bytedeco.javacv.Java2DFrameUtils.toBufferedImage(frame);

                // Convert BufferedImage to JavaFX Image
                Image fxImage = javafx.embed.swing.SwingFXUtils.toFXImage(bufferedImage, null);

                // Set the image in imgFront
                imgFront.setImage(fxImage);
            } else {
                System.err.println("Failed to capture frame.");
            }
        } catch (FrameGrabber.Exception e) {
            e.printStackTrace();
        }


        /*
        Image snapshot = CameraHandler.getInstance().capturePic();

        if(snapshot !=null) {
            imgFront.setImage(snapshot);
        } else {
            System.err.println("Something went wrong, couldnt capture image");
        }

         */
    }
}
