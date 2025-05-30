package dk.easv.belsign.GUI;

import com.itextpdf.io.image.ImageDataFactory;
import dk.easv.belsign.BE.Photos;
import dk.easv.belsign.BE.Products;
import dk.easv.belsign.BLL.Util.*;
import dk.easv.belsign.Models.PhotosModel;
import dk.easv.belsign.Models.ProductsModel;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;

import java.io.File;
import java.util.Objects;
import java.util.Optional;

public class QCFrameController implements IParentAware{
    public ScrollPane scrPane;

    private ProductsModel productsModel;
    private PhotosModel photosModel;

    @FXML
    private FlowPane fpFlowpane;

    private Products products;
    private Object image;

    private MainframeController parent;




    public void initialize() {
        // Set appropriate properties for better layout behavior
        fpFlowpane.setHgap(10);
        fpFlowpane.setVgap(10);
        fpFlowpane.setPadding(new Insets(10));

        scrPane.widthProperty().addListener((obs, oldVal, newVal) -> {
            fpFlowpane.setPrefWidth(newVal.doubleValue() - 20);
        });

        try {
            this.productsModel = new ProductsModel();
            this.photosModel = new PhotosModel();

        } catch (Exception e) {
            ExceptionHandler.handleUnexpectedException(e);
            showError("Failed to initialize ProductsModel");
        }
    }

    public void setProduct(Products selectedProduct) {
        this.products = selectedProduct;
        showImages();
    }

    @Override
    public void setParent(MainframeController parent) {
        this.parent = parent;
    }

    private void showImages() {
        Pane customPane1;
        Image image;

        for (Photos photo : products.getPhotos()) {
            customPane1 = new Pane();
            customPane1.setPrefSize(550, 310);
            fpFlowpane.getChildren().add(customPane1);
            customPane1.setStyle("-fx-background-color: #FFF; -fx-background-radius: 8px; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.2), 10, 0, 0, 0);");

            // Overlay for corner highlight
            Region colorCorner = new Region();
            colorCorner.setPrefSize(60, 60); // Adjust size as needed
            colorCorner.setStyle("-fx-background-color: rgba(0,255,0,0.25); -fx-background-radius: 0 20px 0 0;"); // Green by default, 25% opacity
            colorCorner.setVisible(false); // Hidden by default

            // Position
            colorCorner.layoutXProperty().bind(customPane1.widthProperty().subtract(colorCorner.prefWidthProperty()));
            colorCorner.setLayoutY(0);

            customPane1.getChildren().add(colorCorner);

            VBox vbox1 = new VBox();
            vbox1.setPrefWidth(customPane1.getPrefWidth());
            customPane1.getChildren().add(vbox1);

            HBox hbox1 = new HBox(15);
            hbox1.setPrefWidth(customPane1.getPrefWidth());
            hbox1.setPadding(new Insets(8));
            vbox1.getChildren().add(hbox1);

            Label label = new Label(photo.getPhotoName());
            label.setStyle("-fx-font-size: 20px; -fx-text-fill: #000;");
            label.setPadding(new Insets(10));
            hbox1.getChildren().add(label);

            String imagePath = photo.getPhotoPath();
            Image finalImage = new Image(new File(imagePath).toURI().toString());

            Photos currentPhoto = photo;

            Button btnApprove = new Button("✔");
            Button btnDecline = new Button("✖");
            btnApprove.setStyle("-fx-background-color: #008000; -fx-text-fill: #FFF; -fx-font-size: 20px;");
            btnDecline.setStyle("-fx-background-color: #FF0000; -fx-text-fill: #FFF; -fx-font-size: 20px;");
            btnApprove.setPadding(new Insets(10));
            btnDecline.setPadding(new Insets(10));
            hbox1.getChildren().addAll(btnApprove, btnDecline);

            if(photo.getPhotoStatus() != null) {
                if (photo.getPhotoStatus().equals("Approved")) {
                    colorCorner.setStyle("-fx-background-color: rgba(0,255,0,0.25); -fx-background-radius: 0 8px 0 10px;");
                    colorCorner.setVisible(true);
                } else if (photo.getPhotoStatus().equals("Declined")) {
                    colorCorner.setStyle("-fx-background-color: rgba(255,0,0,0.25); -fx-background-radius: 0 8px 0 10px;");
                    colorCorner.setVisible(true);
                }
            }

            // Approve
            btnApprove.setOnAction(event -> {
                updatePhotoStatus(currentPhoto, "Approved");
                colorCorner.setStyle("-fx-background-color: rgba(0,255,0,0.25); -fx-background-radius: 0 8px 0 10px;");
                colorCorner.setVisible(true);
            });

            // Decline
            btnDecline.setOnAction(event -> {
                updatePhotoStatus(currentPhoto, "Declined");
                colorCorner.setStyle("-fx-background-color: rgba(255,0,0,0.25); -fx-background-radius: 0 8px 0 10px;");
                colorCorner.setVisible(true);
            });

            Button btnComment = new Button("🖋");
            btnComment.setStyle("-fx-background-color: #ffff00; -fx-text-fill: #000; -fx-font-size: 20px;");
            btnComment.setPadding(new Insets(10));
            hbox1.getChildren().add(btnComment);

            String finalImagePath = imagePath;
            btnComment.setOnAction(event -> {
                String existingComment = photo.getPhotoComments();
                if (existingComment == null) {
                    existingComment = "";
                }
                TextInputDialog tiDialog = new TextInputDialog(existingComment);
                tiDialog.setTitle("Comment");
                tiDialog.setHeaderText("Edit, enter new, or delete the comment:");
                tiDialog.setContentText("Enter comment for image: " + photo.getPhotoName());
                Optional<String> result = tiDialog.showAndWait();
                String comment = result.orElse(null);
                if (comment != null) {
                    photo.setPhotoComment(comment);
                    try {
                        photosModel.updatePhotoComment(photo);
                    } catch (Exception e) {
                        ExceptionHandler.handleUnexpectedException(e);
                        showError("Failed to update product: " + e.getMessage());
                    }
                }
            });

            ImageView imageViewEvent = new ImageView();
            imageViewEvent.setFitWidth(customPane1.getPrefWidth());
            imageViewEvent.setFitHeight(260);
            image = new Image(new File(photo.getPhotoPath()).toURI().toString());
            imageViewEvent.setImage(image);
            vbox1.getChildren().add(imageViewEvent);

            customPane1.setOnMouseClicked(event -> openImageInLargeWindow(finalImage));
        }
    }

    private void openImageInLargeWindow(Image image) {
        // Create a new stage (window)
        javafx.stage.Stage imageStage = new javafx.stage.Stage();
        imageStage.setTitle("Image Viewer");

        // Create an ImageView with the image
        ImageView largeImageView = new ImageView(image);

        // Make the image larger but maintain aspect ratio
        largeImageView.setPreserveRatio(true);
        largeImageView.setFitWidth(800); // Larger width

        // Create a scrollable container in case the image is very large
        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setContent(largeImageView);
        scrollPane.setPannable(true); // Allow panning with mouse

        // Create scene and set it to the stage
        javafx.scene.Scene scene = new javafx.scene.Scene(scrollPane, 800, 600);
        imageStage.setScene(scene);

        // Show the stage
        imageStage.show();
    }

    private void updatePhotoStatus(Photos photo, String status) {
        try {
            photo.setPhotoStatus(status);
            photosModel.updatePhoto(photo);
        } catch (Exception e) {
            ExceptionHandler.handleUnexpectedException(e);
            showError("Failed to update photo status");
        }
    }

    @FXML
    private void onConfirmPressed(ActionEvent actionEvent) throws Exception {
        ProductApprovalUtil productApprovalUtil = new ProductApprovalUtil(productsModel);
        int approvedBy = UserSession.getLoggedInUser().getUserId();
        try {
            productApprovalUtil.setProductStatus(products, approvedBy);
        } catch (Exception e) {
            ExceptionHandler.handleUnexpectedException(e);
            showError("Failed to set product status");
        } finally {
            String fxmlPath = "/dk/easv/belsign/ProductFrame.fxml";
            SceneService.loadCenterContent((StackPane) parent.getMainPane(), fxmlPath, parent);
        }
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Quality Controller Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    @FXML
    private void handleBack(ActionEvent event) {
        String fxmlPath = "/dk/easv/belsign/ProductFrame.fxml";
        SceneService.loadCenterContent((StackPane) parent.getMainPane(), fxmlPath, parent);
    }
}
