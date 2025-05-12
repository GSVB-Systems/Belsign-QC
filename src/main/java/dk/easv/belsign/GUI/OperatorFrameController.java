package dk.easv.belsign.GUI;

import dk.easv.belsign.BE.Products;
import dk.easv.belsign.BLL.Util.CameraHandler;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;

import java.util.Objects;

public class OperatorFrameController {
    public ScrollPane scrPane;
    private MainframeController mainframeController;

    @FXML
    private FlowPane fpFlowpane;

    private Products products;

    public void initialize() {
        // Set appropriate properties for better layout behavior
        fpFlowpane.setHgap(10);
        fpFlowpane.setVgap(10);
        fpFlowpane.setPadding(new Insets(10));

        scrPane.widthProperty().addListener((obs, oldVal, newVal) -> {
            fpFlowpane.setPrefWidth(newVal.doubleValue() - 20);
        });

        showImages();
    }

   private void showImages() {
       // Then use a regular for loop with the size
       for(int i = 0; i < 150; i++) {
            // Create container for event card
            Pane customPane1 = new Pane();
            customPane1.setPrefSize(550, 310);
            fpFlowpane.getChildren().add(customPane1);
            customPane1.setStyle("-fx-background-color: #FFF; -fx-background-radius: 8px; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.2), 10, 0, 0, 0);");


            // Create vertical layout for event content
            VBox vbox1 = new VBox();
            vbox1.setPrefWidth(customPane1.getPrefWidth()); // Match the pane width
            customPane1.getChildren().add(vbox1);

            Label label = new Label("Event " + (i + 1));
            label.setStyle("-fx-font-size: 20px; -fx-text-fill: #000;");
            label.setPadding(new Insets(10));
            vbox1.getChildren().add(label);

            // Event image
            ImageView imageViewEvent = new ImageView();
            imageViewEvent.setFitWidth(customPane1.getPrefWidth());
            imageViewEvent.setFitHeight(260);
            imageViewEvent.setImage(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/dk/easv/belsign/images/belmanlogo.png"))));
            vbox1.getChildren().add(imageViewEvent);

            customPane1.setOnMouseClicked(event -> {
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
}

