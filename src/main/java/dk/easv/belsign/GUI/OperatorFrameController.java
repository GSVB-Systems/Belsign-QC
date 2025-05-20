package dk.easv.belsign.GUI;

import dk.easv.belsign.BE.Photos;
import dk.easv.belsign.BE.Products;
import dk.easv.belsign.BLL.Util.*;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.util.List;
import java.util.Objects;

public class OperatorFrameController {
    private MainframeController mainframeController;

    @FXML
    public ScrollPane scrPane;
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
    }

    public void setProduct(Products selectedProduct) {
        this.products = selectedProduct;
        ProductSession.setEnteredProduct(selectedProduct);
        showImages();
    }

    private void showImages() {
        fpFlowpane.getChildren().clear();
        for (int i = 0; i < products.getSize(); i++) {
            Photos photo = products.getPhotos().get(i);
            Pane imageBox = createImageBox(false, false, photo);
            Label label = new Label(photo.getPhotoName());
            label.setPadding(new Insets(10));

            VBox vbox = (VBox) imageBox.getChildren().get(0);
            vbox.getChildren().add(0, label);

            fpFlowpane.getChildren().add(imageBox);
        }
        fpFlowpane.getChildren().add(createImageBox(true, true, null));
    }

    @FXML
    private void handleUpload(ActionEvent event) {
        savePhotosToDatabase();
        CameraHandler.getInstance().releaseCam();
    }

    public void setParent(MainframeController mainframeController) {
        this.mainframeController = mainframeController;
    }

    private Photos openCameraAndCapturePhoto() {
        CameraViewController controller = SceneService.fullscreen("CameraView.fxml", "Camera");
        if (controller == null) {
            System.err.println("Failed to load Camera view.");
            return null;
        }

        return controller.getCapturedPhoto(); // return Photos, not Image
    }

    private Pane createImageBox(boolean includeComboBox, boolean allowAddNewBox, Photos photo) {
        Pane customPane = new Pane();
        customPane.setPrefSize(550, 310);
        customPane.getStyleClass().add("custom-pane");

        VBox vbox = new VBox();
        vbox.setPrefWidth(customPane.getPrefWidth());
        customPane.getChildren().add(vbox);

        if (includeComboBox) {
            ComboBox<String> comboBox = new ComboBox<>();
            comboBox.getItems().addAll("tag1", "tag2", "tag3");
            vbox.getChildren().add(comboBox);
        }

        ImageView imageView = new ImageView();
        imageView.setFitWidth(customPane.getPrefWidth());
        imageView.setFitHeight(260);
        imageView.setImage(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/dk/easv/belsign/images/belmanlogo.png"))));
        vbox.getChildren().add(imageView);

        customPane.setOnMouseClicked(event -> {
            PhotoSession.setCurrentPhoto(photo);

            Photos newPhoto = openCameraAndCapturePhoto(); // Updated method returning Photos object

            if (newPhoto != null) {
                imageView.setImage(new Image("file:" + newPhoto.getPhotoPath()));
                ProductSession.getEnteredProduct().getPhotos().add(newPhoto);
            }

            if (allowAddNewBox) {
                Pane newBox = createImageBox(true, true, null);
                fpFlowpane.getChildren().add(newBox);
            }
        });

        return customPane;
    }
    private void savePhotosToDatabase() {
        try {
            PhotoService photoService = new PhotoService();
            List<Photos> photos = ProductSession.getEnteredProduct().getPhotos();

            photoService.savePhotos(photos)
                    .thenRun(() -> System.out.println("Photos saved successfully!"))
                    .exceptionally(ex -> {
                        ex.printStackTrace();
                        return null;
                    });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}