package dk.easv.belsign.GUI;

import dk.easv.belsign.BE.Products;
import dk.easv.belsign.BLL.Util.CameraHandler;
import dk.easv.belsign.BLL.Util.PDFGenerator;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;

import java.util.Objects;

public class QCFrameController {
    public ScrollPane scrPane;
    private MainframeController mainframeController;
    private PDFGenerator pdfGenerator;

    @FXML
    private FlowPane fpFlowpane;

    private Products products;
    private Object image;

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


    }

    public void setProduct(Products selectedProduct) {
        this.products = selectedProduct;
        showImages();
    }


    private void showImages() {
        // Then use a regular for loop with the size
        Pane customPane1 = null;
        Image image = null;
        for (int i = 0; i < products.getSize(); i++) {
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

            Label label = new Label(products.getPhotoName());
            label.setStyle("-fx-font-size: 20px; -fx-text-fill: #000;");
            label.setPadding(new Insets(10));
            hbox1.getChildren().add(label);



            Button btnApprove = new Button("Approve");
            Button btnDecline = new Button("Decline");
            btnApprove.setStyle("-fx-background-color: #008000; -fx-text-fill: #FFF;");
            btnDecline.setStyle("-fx-background-color: #FF0000; -fx-text-fill: #FFF;");
            btnApprove.setPadding(new Insets(10));
            btnDecline.setPadding(new Insets(10));
            hbox1.getChildren().addAll(btnApprove, btnDecline);

            // Event image
            ImageView imageViewEvent = new ImageView();
            imageViewEvent.setFitWidth(customPane1.getPrefWidth());
            imageViewEvent.setFitHeight(260);
            image = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/dk/easv/belsign/images/belmanlogo.png")));
            imageViewEvent.setImage(image);
            vbox1.getChildren().add(imageViewEvent);

            Image finalImage = image;
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
        pdfGenerator.createPDF("src/main/resources/dk/easv/belsign/PDF/QCReport.pdf");
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


}
