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
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;

import java.util.Objects;
import java.util.Optional;

public class QCFrameController implements IParentAware{
    public ScrollPane scrPane;
    private PDFGenerator pdfGenerator;
    private ProductsModel productsModel;
    private PhotosModel photosModel;

    @FXML
    private FlowPane fpFlowpane;

    private Products products;
    private Object image;

    private MainframeController parent;

    public QCFrameController() {
        this.pdfGenerator = new PDFGenerator();
    }


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
            showError("Failed to initialize ProductsModel: " + e.getMessage());
            e.printStackTrace();
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
        // Then use a regular for loop with the size
        Pane customPane1 = null;
        Image image = null;
        for (Photos photo : products.getPhotos()) {
            // Create container for event card
            customPane1 = new Pane();
            customPane1.setPrefSize(550, 310);
            fpFlowpane.getChildren().add(customPane1);
            customPane1.setStyle("-fx-background-color: #FFF; -fx-background-radius: 8px; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.2), 10, 0, 0, 0);");


            // Create vertical layout for event content
            VBox vbox1 = new VBox();
            vbox1.setPrefWidth(customPane1.getPrefWidth()); // Match the pane width
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
            Image finalImage = new Image(Objects.requireNonNull(getClass().getResourceAsStream(imagePath)));

            Photos currentPhoto = photo;

            Button btnApprove = new Button("✔");
            Button btnDecline = new Button("✖");
            btnApprove.setStyle("-fx-background-color: #008000; -fx-text-fill: #FFF; -fx-font-size: 20px;");
            btnDecline.setStyle("-fx-background-color: #FF0000; -fx-text-fill: #FFF; -fx-font-size: 20px;");
            btnApprove.setPadding(new Insets(10));
            btnDecline.setPadding(new Insets(10));
            hbox1.getChildren().addAll(btnApprove, btnDecline);

            btnApprove.setOnAction(event -> {
                updatePhotoStatus(currentPhoto, "Approved");
            });

            btnDecline.setOnAction(event -> {
                updatePhotoStatus(currentPhoto, "Declined");
            });

            Button btnComment = new Button("🗒🖋");
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
                        productsModel.updateProduct(products);
                    } catch (Exception e) {
                        showError("Failed to update product: " + e.getMessage());
                    }
                }
            });
            // Event image
            ImageView imageViewEvent = new ImageView();
            imageViewEvent.setFitWidth(customPane1.getPrefWidth());
            imageViewEvent.setFitHeight(260);
            image = new Image(Objects.requireNonNull(getClass().getResourceAsStream(photo.getPhotoPath())));
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


    public void onGeneratePDFPressed(ActionEvent actionEvent) {
        try{

        pdfGenerator.createPDF("src/main/resources/dk/easv/belsign/PDF/QCReport.pdf", products);
        }catch (Exception e){
            showError("PDF generation failed: " + e.getMessage());
        }
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("PDF Generation Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void updatePhotoStatus(Photos photo, String status) {
        try {
            photo.setPhotoStatus(status);
            photosModel.updatePhoto(photo);
        } catch (Exception e) {
            showError("Failed to update photo status: " + e.getMessage());
        }
    }

    @FXML
    private void onConfirmPressed(ActionEvent actionEvent) throws Exception {
        ProductApprovalUtil productApprovalUtil = new ProductApprovalUtil(productsModel);
        int approvedBy = UserSession.getLoggedInUser().getUserId();
        try {
            productApprovalUtil.setProductStatus(products, approvedBy);
        } catch (Exception e) {
            showError("Failed to set product status: " + e.getMessage());
        }finally {

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/dk/easv/belsign/ProductFrame.fxml"));
            parent.fillMainPane(loader);
            Object controller = loader.getController();
            if (controller instanceof IParentAware) {
                ((IParentAware) controller).setParent(parent);
            }
        }
    }
}
