package dk.easv.belsign.GUI;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;

import javax.swing.text.html.ImageView;
import java.awt.*;

public class OperatorFrameController {
    private MainframeController mainframeController;

    @FXML
    private ImageView imgFront;
    private ImageView imgRight;
    private ImageView imgLeft;
    private ImageView imgBack;
    private ImageView imgTop;

    @FXML
    private void handleUpload(ActionEvent event) {
    }

    public void setParent(MainframeController mainframeController) {
        this.mainframeController = mainframeController;
    }
}
