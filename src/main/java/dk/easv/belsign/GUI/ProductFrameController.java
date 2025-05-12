package dk.easv.belsign.GUI;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.SVGPath;
import javafx.scene.image.Image;

import java.util.Objects;

public class ProductFrameController {
    public VBox vbLeft;
    public VBox vbRight;

    private MainframeController parent;


    public void initialize() {
        vbLeft.setAlignment(javafx.geometry.Pos.CENTER);
        vbLeft.setSpacing(20);

        showProducts();
        showImages();
    }

    public void showProducts() {
        for (int i = 0; i < 15; i++) {
            SVGPath svgPath = new SVGPath();
            svgPath.setContent("M301 33.0001L279 0.890137L22 1.00006L0 32.5001L22 64.0001L279 64.11L301 33.0001Z");
            svgPath.setFill(Color.valueOf("#4CAF50"));


            Label label = new Label(""+(i + 1));
            label.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: white;");

            StackPane stack = new StackPane();
            stack.getChildren().addAll(svgPath, label);

            vbLeft.getChildren().add(stack);
        }
    }

    public void showImages() {
        for (int i = 0; i < 15; i++) {
            HBox container = new HBox();
            container.setSpacing(10);
            container.setPadding(new Insets(0, 50, 0, 50));

            Label label1 = new Label("Label 1: " + (i + 1));
            Label label2 = new Label("Label 2: " + (i + 1));

            VBox labelContainer = new VBox();
            labelContainer.getChildren().addAll(label1, label2);
            labelContainer.setSpacing(5);

            ImageView imageView = new ImageView();
            imageView.setImage(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/dk/easv/belsign/images/belmanlogo.png"))));
            imageView.setFitWidth(50);
            imageView.setFitHeight(50);
            imageView.setPreserveRatio(true);

            container.getChildren().addAll(labelContainer, imageView);

            HBox.setHgrow(labelContainer, Priority.ALWAYS);

            vbRight.getChildren().add(container);
        }
    }

    public void onOpenButtonPressed(ActionEvent actionEvent) {

    }

    public void onPDFButtonPressed(ActionEvent actionEvent) {
    }
}