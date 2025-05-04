package dk.easv.belsign.GUI;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;

public class OperatorFrameController {
    private MainframeController mainframeController;

    @FXML
    private void handleUpload(ActionEvent event) {
    }

    public void setParent(MainframeController mainframeController) {
        this.mainframeController = mainframeController;
    }
}
