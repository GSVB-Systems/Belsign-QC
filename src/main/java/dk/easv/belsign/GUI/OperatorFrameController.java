package dk.easv.belsign.GUI;

import dk.easv.belsign.BE.Products;
import dk.easv.belsign.BLL.Util.CameraHandler;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;

import java.util.Objects;

public class OperatorFrameController {
    private MainframeController mainframeController;

    @FXML
    private FlowPane fpFlowpane;

    private Products products;

    public void initialize() {
        showImages();
    }

   private void showImages() {
       // Then use a regular for loop with the size
       for(int i = 0; i < products.getSize(); i++) {
            // Create container for event card
            Pane customPane1 = new Pane();
            customPane1.setPrefSize(460, 430);
            fpFlowpane.getChildren().add(customPane1);
            customPane1.setStyle("-fx-background-color: #FFF; -fx-background-radius: 8px; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.2), 10, 0, 0, 0);");


            // Create vertical layout for event content
            VBox vbox1 = new VBox();
            vbox1.setPrefWidth(customPane1.getPrefWidth()); // Match the pane width
            customPane1.getChildren().add(vbox1);

            // Event image
            ImageView imageViewEvent = new ImageView();
            imageViewEvent.setFitHeight(260);
            imageViewEvent.setFitWidth(customPane1.getPrefWidth());
            imageViewEvent.setImage(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/dk/easv/belsign/images/belmanlogo.png"))));
            vbox1.getChildren().add(imageViewEvent);

            imageViewEvent.setOnMouseClicked(event -> {
                Image snapshot = CameraHandler.getInstance().capturePic();
                if (snapshot != null) {
                    imageViewEvent.setImage(snapshot);
                }
            });
        }
    }

    @FXML
    private void handleUpload(ActionEvent event) {
    }

    public void setParent(MainframeController mainframeController) {
        this.mainframeController = mainframeController;
    }

    @FXML
    private void onCapture(ActionEvent actionEvent) {
    }

/*
    @FXML
    private void onCapture() {
        Image snapshot = CameraHandler.getInstance().capturePic();
            //commetnh
        if(snapshot !=null) {
            imgFront.setImage(snapshot);
        } else {
            System.err.println("Something went wrong, couldnt capture image");
        }
    }

*/
}

