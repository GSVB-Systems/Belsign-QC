package dk.easv.belsign.GUI;

import dk.easv.belsign.BE.Orders;
import dk.easv.belsign.BE.Photos;
import dk.easv.belsign.BE.Products;
import dk.easv.belsign.BLL.Util.OrderSession;
import dk.easv.belsign.BLL.Util.PDFGenerator;
import dk.easv.belsign.BLL.Util.SceneService;
import dk.easv.belsign.Models.OrdersModel;
import dk.easv.belsign.Models.ProductsModel;
import dk.easv.belsign.Models.UsersModel;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.SVGPath;
import javafx.scene.image.Image;
import dk.easv.belsign.BLL.Util.UserSession;
import javafx.scene.shape.StrokeType;

import java.io.File;
import java.util.List;
import java.util.Objects;


public class ProductFrameController implements IParentAware {
    public VBox vbLeft;
    public VBox vbRight;
    public ProductsModel productsModel;
    public OrdersModel ordersModel;
    public UsersModel usersModel;
    public Button btnOpen;
    private MainframeController parent;
    private Products selectedProduct;
    private StackPane previouslySelectedStack = null;
    @FXML
    private Label lblApprovedBy;
    @FXML
    private Label lblApproval;


    private PDFGenerator pdfGenerator;
    private Products products;
    @FXML
    private Button btnGeneratePDF;

    public ProductFrameController() {
        this.pdfGenerator = new PDFGenerator();
        this.products = selectedProduct;
    }

    @Override
    public void setParent(MainframeController parent) {
        this.parent = parent;
    }



    public void initialize() {
        try {

            productsModel = new ProductsModel();
            productsModel.getObservableProducts(OrderSession.getEnteredOrder().getOrderId());
            ordersModel = new OrdersModel();
            usersModel = new UsersModel();

            vbLeft.setAlignment(javafx.geometry.Pos.CENTER);
            vbLeft.setSpacing(20);

            if(UserSession.getLoggedInUser().getRoleId() == 1) {
                btnGeneratePDF.setVisible(false);
            } else if(UserSession.getLoggedInUser().getRoleId() == 2) {
                btnGeneratePDF.setVisible(true);
            }

            showProducts();
            // Don't show images on init - wait for selection
        } catch (Exception e) {
            showError("Failed to initialize Productframe, contact system Administrator: " + e.getMessage());

        }
    }

    public void showProducts() {
        try {
            List<Products> productsList = null;
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

                productsList = productsModel.getObservableProducts(OrderSession.getEnteredOrder().getOrderId());
                productsList.add(products);
            }
            OrderApproval(productsList);
        } catch (Exception e) {
            showError("Failed to load products, contact system administrator: " + e.getMessage());

        }
    }

    private void OrderApproval(List<Products> productsList) {
        int productSize = 0;
        int approvedProducts = 0;
        for (int i = 0; i < productsList.size(); i++) {
            products = productsList.get(i);
            productSize++;
            if (Objects.equals(products.getProductStatus(), "Approved")) {
                approvedProducts++;

            }

        }

        if (productSize == approvedProducts) {
            Orders order = OrderSession.getEnteredOrder();
            order.setApprovalStatus("Approved");
            order.setApprovalDate(java.time.LocalDateTime.now());
            try {
                ordersModel.createOrderApproval(order);
            } catch (Exception e) {
                showError("Error creating order approval, Contact system Administrator");


            }
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
            if (product.getApprovedBy() > 0) {
                lblApprovedBy.setText(usersModel.getUserById(product.getApprovedBy()).getFirstName() + " " +
                        usersModel.getUserById(product.getApprovedBy()).getLastName());
            } else {
                lblApprovedBy.setText("...");
            }        } catch (Exception e) {
            showError("Error updating order title, Contact system Administrator: " + e.getMessage());
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

            List<Photos> photos = selectedProduct.getPhotos();
            int photoCount = photos.size();

            for(int i = 0; i < photoCount; i++) {
                Photos photo = photos.get(i);
                HBox container = new HBox();
                container.setSpacing(10);
                container.setPadding(new Insets(0, 50, 0, 50));

                Label label1 = new Label(photo.getPhotoName());

                String statusOrComment;
                if ("Approved".equals(photo.getPhotoStatus()) || "Declined".equals(photo.getPhotoStatus())) {
                    if (photo.getPhotoComments() != null && !photo.getPhotoComments().isEmpty()) {
                        statusOrComment = photo.getPhotoStatus() + ", " + photo.getPhotoComments();
                    } else {
                        statusOrComment = photo.getPhotoStatus();
                    }
                } else {
                    statusOrComment = photo.getPhotoComments();
                }
                Label label2 = new Label(statusOrComment);

                VBox labelContainer = new VBox();
                label1.setStyle("-fx-font-size: 24px;");
                label2.setStyle("-fx-font-size: 24px;");
                labelContainer.getChildren().addAll(label1, label2);
                labelContainer.setSpacing(5);

                ImageView imageView = new ImageView();
                String photoPath = photo.getPhotoPath();
                if(photoPath != null) {
                    imageView.setImage(new Image(new File(photoPath).toURI().toString()));
                }
                imageView.setFitWidth(90);
                imageView.setFitHeight(90);
                imageView.setPreserveRatio(true);

                container.getChildren().addAll(labelContainer, imageView);
                HBox.setHgrow(labelContainer, Priority.ALWAYS);

                vbRight.getChildren().add(container);

                // Add separator after each container except the last one
                if (i < photoCount - 1) {
                    Separator separator = new Separator();
                    separator.setPadding(new Insets(10, 0, 10, 0));
                    vbRight.getChildren().add(separator);
                }
            }
        } catch (Exception e) {
            showError("Error loading image, contact system Administrator: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void onOpenButtonPressed(ActionEvent actionEvent) {
        if (selectedProduct == null) {
            showError("Please select a product first");
            return;
        }

        try {
            int roleId = UserSession.getLoggedInUser().getRoleId();
            String fxmlPath;

            if (roleId == 1) {
                fxmlPath = "/dk/easv/belsign/OperatorFrame.fxml";
            } else if (roleId == 2) {
                fxmlPath = "/dk/easv/belsign/QCFrame.fxml";
            } else {
                showError("Invalid role ID");
                return;
            }

            SceneService.loadCenterContent((StackPane) parent.getMainPane(), fxmlPath, parent);

            Object controller = SceneService.getLastLoadedController();
            if (roleId == 1 && controller instanceof OperatorFrameController) {
                ((OperatorFrameController) controller).setProduct(selectedProduct);
            } else if (roleId == 2 && controller instanceof QCFrameController) {
                ((QCFrameController) controller).setProduct(selectedProduct);
            }

        } catch (Exception e) {
            showError("Error opening frame: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void onPDFButtonPressed(ActionEvent actionEvent) {
        if(OrderSession.getEnteredOrder().getApprovalStatus() == "Approved") {
            try{
                pdfGenerator.createPDF("src/main/resources/dk/easv/belsign/PDF/QCReport.pdf", selectedProduct);
            }catch (Exception e){
                showError("PDF generation failed: " + e.getMessage());
            }


        }else
        {
            showError("Order not approved - Order needs to be approved before generating PDF");
        }

    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Product Selection Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}