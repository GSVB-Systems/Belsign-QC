package dk.easv.belsign.GUI;

import dk.easv.belsign.BE.Orders;
import dk.easv.belsign.BE.Photos;
import dk.easv.belsign.BE.PhotosTemplate;
import dk.easv.belsign.BE.Products;
import dk.easv.belsign.BLL.Util.ExceptionHandler;
import dk.easv.belsign.Models.OrdersModel;
import dk.easv.belsign.Models.PhotosModel;
import dk.easv.belsign.Models.ProductsModel;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class CreateOrderController {

        private ArrayList<Products> productsList = new ArrayList<>();
        private ProductsModel productsModel;
        private OrdersModel ordersModel;
        private PhotosModel photosModel;

        @FXML
        private FlowPane fpProducts;
        @FXML
        private TextField txtOrderId;


        public CreateOrderController()  {

            try {
                productsModel = new ProductsModel();
                ordersModel = new OrdersModel();
                photosModel = new PhotosModel();
            } catch (Exception e) {
                    ExceptionHandler.handleUnexpectedException(e);
            }
        }

        @FXML
        private void addProduct(ActionEvent actionEvent) {

                // Create container for the ticket type form
                Pane pane = new Pane();
                pane.setPrefSize(540, 270);
                pane.setStyle("-fx-border-color: Black;");

                // Create and position ticket name label and field
                Label lblProductName = new Label("Product Name");
                lblProductName.setStyle("-fx-font-size: 18px");
                lblProductName.setLayoutX(20);
                lblProductName.setLayoutY(14);

                TextField txtProductName = new TextField();
                txtProductName.setStyle("-fx-font-size: 18px");
                txtProductName.setId("txtProductName");
                txtProductName.setLayoutX(20);
                txtProductName.setLayoutY(42);
                txtProductName.setPrefSize(491, 26);
                txtProductName.setPromptText("Product name");

                HBox hbox = new HBox();
                hbox.setLayoutX(20);
                hbox.setLayoutY(84);
                ToggleGroup sizeGroup = new ToggleGroup();
                ToggleButton btnSmall = new ToggleButton("Small");
                ToggleButton btnMedium = new ToggleButton("Medium");
                ToggleButton btnLarge = new ToggleButton("Large");
                btnSmall.setToggleGroup(sizeGroup);
                btnMedium.setToggleGroup(sizeGroup);
                btnLarge.setToggleGroup(sizeGroup);
                btnSmall.setStyle("-fx-font-size: 18px");
                btnMedium.setStyle("-fx-font-size: 18px");
                btnLarge.setStyle("-fx-font-size: 18px");
                hbox.getChildren().addAll(btnSmall, btnMedium, btnLarge);

                // Create delete button with handler to remove this ticket type
                Button btnDelete = new Button("Delete");
                btnDelete.setLayoutX(420);
                btnDelete.setLayoutY(228);
                btnDelete.setOnAction(e -> {
                        fpProducts.getChildren().remove(pane);
                });

                // Add all components to the ticket type form panel
                pane.getChildren().addAll(lblProductName, txtProductName, hbox, btnDelete);

                // Add the panel to the flow pane and increment counter
                fpProducts.getChildren().add(pane);
        }

        public void onConfirm(ActionEvent actionEvent) throws Exception {

                int orderId;
                if (txtOrderId.getText().isEmpty()) {
                        showAlert("Please enter an Order ID");
                        return;
                }

                try {
                        orderId = Integer.parseInt(txtOrderId.getText());
                } catch (NumberFormatException e) {
                        showAlert("Order ID must be a number");
                        return;
                }

                boolean orderExists = ordersModel.getObservableOrders().stream()
                        .anyMatch(order -> order.getOrderId() == orderId);

                if (orderExists) {
                        showAlert("Order ID already exists. Please use a different ID.");
                        return;
                }


                Orders order = new Orders(orderId);
                ordersModel.createOrder(order);

                List<Products> productsToCreate = new ArrayList<>();

                for (Node node : fpProducts.getChildren()) {
                        if (node instanceof Pane) {
                                // Extract product data as before
                                Pane productPane = (Pane) node;
                                TextField txtProductName = (TextField) productPane.lookup("#txtProductName");
                                String productName = txtProductName.getText();

                                // Get size information
                                String selectedSize = getSelectedSize(productPane);
                                int size = 0;

                            if(Objects.equals(selectedSize, "Small")){
                                        size = 5;
                                } else if (Objects.equals(selectedSize, "Medium")){
                                        size = 10;
                                } else if (Objects.equals(selectedSize, "Large")){
                                        size = 15;
                                }

                                // Create and add product to the list
                                Products product = new Products(orderId, productName, size);
                                productsToCreate.add(product);
                        }
                }

                // Create all products at once and get IDs
                List<Integer> productIds = productsModel.createProducts(new ArrayList<>(productsToCreate));

                // Now create photos with the product IDs
                for (int i = 0; i < productsToCreate.size(); i++) {
                        Products product = productsToCreate.get(i);
                        Integer productId = productIds.get(i);

                        // Determine template based on size
                        PhotosTemplate template = null;
                        if (product.getSize() == 5) {
                                template = PhotosTemplate.SMALL;
                        } else if (product.getSize() == 10) {
                                template = PhotosTemplate.MEDIUM;
                        } else if (product.getSize() == 15) {
                                template = PhotosTemplate.LARGE;
                        }

                        // Create photos for this product
                        if (template != null) {
                                List<Photos> photos = template.createPhotos(productId);
                                for (Photos photo : photos) {

                                        photosModel.createPhoto(photo);
                                }
                        }
                }

                Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
                stage.close();


        }

        private String getSelectedSize(Pane productPane) {
                HBox hbox = (HBox) productPane.getChildren().stream()
                        .filter(child -> child instanceof HBox)
                        .findFirst()
                        .orElse(null);

                if (hbox != null) {
                        for (Node sizeNode : hbox.getChildren()) {
                                if (sizeNode instanceof ToggleButton) {
                                        ToggleButton sizeBtn = (ToggleButton) sizeNode;
                                        if (sizeBtn.isSelected()) {
                                                return sizeBtn.getText();
                                        }
                                }
                        }
                }
                return "Not selected";

        }
        private void showAlert(String message) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Operator Error!");
                alert.setHeaderText(null);
                alert.setContentText(message);
                alert.showAndWait();
        }
}
