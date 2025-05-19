package dk.easv.belsign.GUI;

import dk.easv.belsign.BE.Photos;
import dk.easv.belsign.BE.Products;
import dk.easv.belsign.BLL.Util.OrderSession;
import dk.easv.belsign.Models.ProductsModel;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.SVGPath;
import javafx.scene.image.Image;
import dk.easv.belsign.BLL.Util.UserSession;
import javafx.scene.shape.StrokeType;


public class ProductFrameController implements IParentAware {
    public VBox vbLeft;
    public VBox vbRight;
    public ProductsModel productsModel;
    public Button btnOpen;
    private MainframeController parent;
    private Products selectedProduct;
    private StackPane previouslySelectedStack = null;
    @FXML
    private Label lblApprovedBy;
    @FXML
    private Label lblApproval;

    @Override
    public void setParent(MainframeController parent) {
        this.parent = parent;
    }

    public void initialize() {
        try {
            productsModel = new ProductsModel();
            productsModel.getObservableProducts(OrderSession.getEnteredOrder().getOrderId());

            vbLeft.setAlignment(javafx.geometry.Pos.CENTER);
            vbLeft.setSpacing(20);

            showProducts();
            // Don't show images on init - wait for selection
        } catch (Exception e) {
            showError("Error initializing: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void showProducts() {
        try {
            for (int i = 0; i < productsModel.getObservableProducts(OrderSession.getEnteredOrder().getOrderId()).size(); i++) {
                Products products = productsModel.getProductsByOrder().get(i);

                SVGPath svgPath = new SVGPath();
                svgPath.setContent("M301 33.0001L279 0.890137L22 1.00006L0 32.5001L22 64.0001L279 64.11L301 33.0001Z");

                String status = products.getProductStatus();
                Color fillColor;

                if ("Approved".equals(status)) {
                    fillColor = Color.valueOf("#B6D7A8"); // Green for approved
                } else if ("Declined".equals(status)) {
                    fillColor = Color.valueOf("#F4B6B6"); // Red for declined/disapproved
                } else if (status != null && !status.isEmpty()) {
                    fillColor = Color.valueOf("#FFF2B2"); // Yellow for pending approval
                } else if (products.getPhotos() == null || products.getPhotos().isEmpty()) { // Might need to be changed to if photoPath is null
                    fillColor = Color.valueOf("#D9D9D9"); // Gray for products without photos
                } else {
                    fillColor = Color.valueOf("#FFF2B2"); // Default to yellow for other cases
                }

                svgPath.setFill(fillColor);

                Label label = new Label(products.getProductName());
                label.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: black;");

                StackPane stack = new StackPane();
                stack.getChildren().addAll(svgPath, label);

                stack.setUserData(products);

                stack.setOnMouseClicked(event -> selectProduct((Products) stack.getUserData()));
                stack.setOnMouseEntered(e -> stack.setCursor(javafx.scene.Cursor.HAND));

                vbLeft.getChildren().add(stack);
            }
        } catch (Exception e) {
            showError("Error loading products: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void selectProduct(Products product) {
        this.selectedProduct = product;
        showImages();

        // Update the mainframe title to show order + product name
        try {
            String orderNumber = OrderSession.getEnteredOrder().getOrderId() + "";
            String productName = product.getProductName();
            parent.setOrder(orderNumber + "-" + productName);
            lblApproval.setText(product.getProductStatus());
        } catch (Exception e) {
            showError("Error updating order title: " + e.getMessage());
        }

        // Clear previous selection highlight
        if (previouslySelectedStack != null) {
            for (javafx.scene.Node node : previouslySelectedStack.getChildren()) {
                if (node instanceof SVGPath) {
                    ((SVGPath) node).setStroke(null);
                    ((SVGPath) node).setStrokeWidth(0);
                }
            }
        }

        // Highlight the newly selected product
        for (javafx.scene.Node node : vbLeft.getChildren()) {
            if (node instanceof StackPane) {
                StackPane stack = (StackPane) node;
                if (stack.getUserData() == product) {
                    for (javafx.scene.Node child : stack.getChildren()) {
                        if (child instanceof SVGPath) {
                            SVGPath svg = (SVGPath) child;
                            svg.setStroke(Color.web("#004B88"));
                            svg.setStrokeWidth(3);
                            svg.setStrokeType(StrokeType.INSIDE);  // Set stroke to render inside
                        }
                    }
                    previouslySelectedStack = stack;
                    break;
                }
            }
        }
    }

    public void showImages() {
        try {
            // Clear previous images
            vbRight.getChildren().clear();

            // If no product is selected, return
            if (selectedProduct == null) return;

          for(Photos photo : selectedProduct.getPhotos()) {
              HBox container = new HBox();
              container.setSpacing(10);
              container.setPadding(new Insets(0, 50, 0, 50));


              Label label1 = new Label(photo.getPhotoName());

              String statusOrComment;
              if ("Approved".equals(photo.getPhotoStatus()) || "Declined".equals(photo.getPhotoStatus())) {
                  statusOrComment = photo.getPhotoStatus();
              } else {
                  statusOrComment = photo.getPhotoComments();
              }
              Label label2 = new Label(statusOrComment);


              VBox labelContainer = new VBox();
              labelContainer.getChildren().addAll(label1, label2);
              labelContainer.setSpacing(5);


              ImageView imageView = new ImageView();
              imageView.setImage(new Image(photo.getPhotoPath()));
              imageView.setFitWidth(50);
              imageView.setFitHeight(50);
              imageView.setPreserveRatio(true);

              container.getChildren().addAll(labelContainer, imageView);
              HBox.setHgrow(labelContainer, Priority.ALWAYS);

              vbRight.getChildren().add(container);
          }





        } catch (Exception e) {
            showError("Error loading image: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void onOpenButtonPressed(ActionEvent actionEvent) {
        if (selectedProduct == null) {
            showError("Please select a product first");
            return;
        }

        try {
            FXMLLoader loader = null;
            int roleId = UserSession.getLoggedInUser().getRoleId();

            if (roleId == 1) {
                loader = new FXMLLoader(getClass().getResource("/dk/easv/belsign/OperatorFrame.fxml"));
            } else if(roleId == 2) {
                loader = new FXMLLoader(getClass().getResource("/dk/easv/belsign/QCFrame.fxml"));
            }

            if (loader == null) {
                showError("Invalid role ID");
                return;
            }

            parent.fillMainPane(loader);

            if (roleId == 1) {
                OperatorFrameController controller = loader.getController();
                controller.setProduct(selectedProduct);
            } else if(roleId == 2) {
                QCFrameController controller = loader.getController();
                controller.setProduct(selectedProduct);
            }

        } catch (Exception e) {
            showError("Error opening frame: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void onPDFButtonPressed(ActionEvent actionEvent) {
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("PDF Generation Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}