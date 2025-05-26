package dk.easv.belsign.GUI;

import dk.easv.belsign.BE.Photos;
import dk.easv.belsign.BE.Products;
import dk.easv.belsign.BLL.Util.*;
import dk.easv.belsign.Models.PhotosModel;
import dk.easv.belsign.Models.ProductsModel;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import org.opencv.photo.Photo;

import java.io.IOException;
import java.util.List;
import java.util.Objects;

public class OperatorFrameController implements IParentAware {
    private MainframeController mainframeController;


    @FXML
    public ScrollPane scrPane;
    @FXML
    private FlowPane fpFlowpane;

    private Products products;
    private PhotosModel photosModel;

    public void initialize() {
        // Set appropriate properties for better layout behavior
        fpFlowpane.setHgap(10);
        fpFlowpane.setVgap(10);
        fpFlowpane.setPadding(new Insets(10));

        scrPane.widthProperty().addListener((obs, oldVal, newVal) -> {
            fpFlowpane.setPrefWidth(newVal.doubleValue() - 20);
        });

        try {
            this.photosModel = new PhotosModel();
        } catch (Exception e) {
            showError("Failed to initialize ProductsModel: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void setProduct(Products selectedProduct) {
        this.products = selectedProduct;
        ProductSession.setEnteredProduct(selectedProduct);
        showImages();
    }

    private void showImages() {
        fpFlowpane.getChildren().clear();
        for (Photos photo : products.getPhotos()) {
            Pane imageBox = createImageBox(false, false, photo);
            Label label = new Label(photo.getPhotoName());
            label.setPadding(new Insets(10));

            VBox vbox = (VBox) imageBox.getChildren().get(0);
            vbox.getChildren().add(0, label);

            fpFlowpane.getChildren().add(imageBox);
        }
        fpFlowpane.getChildren().add(createImageBox(true, true, newPhoto()));
    }

    @FXML
    private void handleUpload(ActionEvent event) {
        savePhotosToDatabase();
        CameraHandler.getInstance().releaseCam();
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/dk/easv/belsign/ProductFrame.fxml"));

        mainframeController.fillMainPane(loader);
        Object controller = loader.getController();
        if (controller instanceof IParentAware) {
            ((IParentAware) controller).setParent(mainframeController);
        }
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


    private Pane createImageBox(boolean includeComboBox, boolean allowAddNewBox, Photos photoIndex) {
        Pane customPane = new Pane();
        customPane.setPrefSize(550, 310);
        customPane.getStyleClass().add("custom-pane");

        VBox vbox = new VBox();
        vbox.setPrefWidth(customPane.getPrefWidth());
        customPane.getChildren().add(vbox);

            if (includeComboBox) {
                ComboBox<String> comboBox = new ComboBox<>();
                comboBox.getItems().addAll("Front", "Back", "Left", "Right", "Top", "Cosmetic Damage", "Other");
                vbox.getChildren().add(comboBox);
                comboBox.setOnAction(event -> {
                    String selectedTag = comboBox.getValue();
                    if (selectedTag != null) {
                        photoIndex.setPhotoName(selectedTag);
                    }
                    System.out.println(photoIndex.getPhotoName());
                });
            }

            ImageView imageView = new ImageView();
            imageView.setFitWidth(customPane.getPrefWidth());
            imageView.setFitHeight(260);
            imageView.setImage(new Image(getClass().getResourceAsStream(photoIndex.getPhotoPath())));
            vbox.getChildren().add(imageView);

            customPane.setOnMouseClicked(event -> {
                PhotoSession.setCurrentPhoto(photoIndex);
                CameraViewController controller = SceneService.fullscreen("CameraView.fxml", "Camera");
                if (controller != null) {
                    Image capturedImage = controller.getCapturedImage();
                    Photos capturedPhotoData = controller.getCapturedPhoto();
                    if (capturedImage != null) {
                        photoIndex.setPhotoPath(capturedPhotoData.getPhotoPath());
                        photoIndex.setPhotoName(capturedPhotoData.getPhotoName());
                        imageView.setImage(capturedImage);
                    }
                }
                if (allowAddNewBox) {
                    Pane newBox = createImageBox(true, true, newPhoto());
                    fpFlowpane.getChildren().add(newBox);
                }
            });


        return customPane;
    }
    private void savePhotosToDatabase() {

            List<Photos> photos = products.getPhotos();
            photosModel.updatePhotoList(photos);

            for (Photos photo : photos) {
                if (photo.getPhotoPath() != null) {
                    System.out.println("Photo path: " + photo.getPhotoPath() + " Photo name: " + photo.getPhotoName() + " Photo status: " + photo.getPhotoStatus());
                }
            }


    }

    private Photos newPhoto() {
        Photos photo = new Photos();
        photo.setPhotoName(" ");
        photo.setPhotoPath("/dk/easv/belsign/images/addPhoto.png");
        photo.setProductId(products.getProductId());
        products.getPhotos().add(photo);
        return photo;
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }


}