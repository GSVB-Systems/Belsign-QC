package dk.easv.belsign.GUI;

import dk.easv.belsign.BLL.Util.CameraHandler;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

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
        Image snapshot = CameraHandler.getInstance().capturePic();

        if(snapshot !=null) {
            imgFront.setImage(snapshot);
        } else {
            System.err.println("Something went wrong, couldnt capture image");
        }
    }
}
