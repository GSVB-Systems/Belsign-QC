package dk.easv.belsign.GUI;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;

public class CreateOrderController {
    @FXML
    private FlowPane fpProducts;
    @FXML
    private TextField txfOrderId;

    @FXML
    private void addProduct(ActionEvent actionEvent) {

            // Create container for the ticket type form
            Pane pane = new Pane();
            pane.setPrefSize(522, 270);
            pane.setStyle("-fx-border-color: Black;");

            // Create and position ticket name label and field
            Label lblProductName = new Label("Product Name");
            lblProductName.setLayoutX(20);
            lblProductName.setLayoutY(14);

            TextField txtProductName = new TextField();
            txtProductName.setId("txtProductName");
            txtProductName.setLayoutX(20);
            txtProductName.setLayoutY(42);
            txtProductName.setPrefSize(491, 26);
            txtProductName.setPromptText("Product name");

            VBox vbox = new VBox();
            Button btnSmall = new Button("Small");
            Button btnMedium = new Button("Medium");
            Button btnLarge = new Button("Large");
            vbox.getChildren().addAll(btnSmall, btnMedium, btnLarge);

            // Create and position price label and field
            Label lblPrice = new Label("Price");
            lblPrice.setLayoutX(20);
            lblPrice.setLayoutY(210);

            // Create delete button with handler to remove this ticket type
            Button btnDelete = new Button("Delete");
            btnDelete.setLayoutX(420);
            btnDelete.setLayoutY(228);
            btnDelete.setOnAction(e -> {
                fpProducts.getChildren().remove(pane);
            });

            // Add all components to the ticket type form panel
            pane.getChildren().addAll(lblProductName, txtProductName, vbox,
                    lblPrice, btnDelete);

            // Add the panel to the flow pane and increment counter
            fpProducts.getChildren().add(pane);

    }

}
