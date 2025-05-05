package dk.easv.belsign.GUI;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.awt.*;

public class OperatorFrameController {
    private MainframeController mainframeController;

    @FXML
    private ImageView imageFront;
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
}
