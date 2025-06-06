package dk.easv.belsign.GUI;

import dk.easv.belsign.BE.Orders;
import dk.easv.belsign.BE.Photos;
import dk.easv.belsign.BE.Products;
import dk.easv.belsign.BLL.Util.ExceptionHandler;
import dk.easv.belsign.BLL.Util.SceneService;
import dk.easv.belsign.Models.OrdersModel;
import dk.easv.belsign.Models.PhotosModel;
import dk.easv.belsign.Models.ProductsModel;
import dk.easv.belsign.Models.UsersModel;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;

public class AdminFrameController implements IParentAware {
    private OrdersModel ordersModel;
    private ProductsModel productsModel;
    private PhotosModel photosModel;
    private UsersModel usersModel;
    private MainframeController parent;

    @FXML
    private TableView<Orders> tblOrders;
    @FXML
    private TableView<Products> tblProducts;
    @FXML
    private TableView<Photos> tblPhotos;
    @FXML
    private TableColumn<Orders, Integer> colOrderId;
    @FXML
    private TableColumn<Products, Integer> colProductId;
    @FXML
    private TableColumn<Products, String> colProductName;
    @FXML
    private TableColumn<Products, String> colSize;
    @FXML
    private TableColumn<Photos, String> colPhotoName;
    @FXML
    private TableColumn<Photos, String> colPhotoStatus;
    @FXML
    private TableColumn<Photos, String> colPhotoComment;
    @FXML
    private TableColumn<Products, String> colApprovedBy;
    @FXML
    private TableColumn<Products, String> colApprovalDate;
    @FXML
    private TableColumn<Products, String> colProductStatus;

    private Orders selectedOrder;
    private Products selectedProduct;
    private int selectedOrderId;
    private int selectedProductId;


    @Override
    public void setParent(MainframeController parent) {
        this.parent = parent;
        parent.setBtnLogout(true);
    }

    public void initialize() throws Exception {
        // Initialize the tables and load data

        try {
            this.ordersModel = new OrdersModel();
            this.productsModel = new ProductsModel();
            this.photosModel = new PhotosModel();
            this.usersModel = new UsersModel();

        } catch (Exception e) {
            ExceptionHandler.handleUnexpectedException(e);
            showError("Failed to initialize OrdersModel - Contact System Administrator");

        }

        DisplayOrders();

    }





    private void DisplayOrders() {
        tblOrders.getItems().clear();

        try {
            tblOrders.setItems(ordersModel.getObservableOrders());
        } catch (Exception e) {
            showError("Failed to load orders: " + e.getMessage());
            ExceptionHandler.handleUnexpectedException(e);
        }
        colOrderId.setCellValueFactory(new PropertyValueFactory<>("orderId"));

        tblOrders.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                selectedOrder = newValue;
                selectedOrderId = selectedOrder.getOrderId();
                try {
                    DisplayProducts();
                } catch (Exception e) {
                    ExceptionHandler.handleUnexpectedException(e);
                    showError("Failed to show products, contact System Admin");
                }
            }
        });


    }

    private void DisplayProducts()  {
        // Implement the logic to display products in the tblProducts TableView

        try {
            tblProducts.setItems(productsModel.getObservableProducts(selectedOrderId));
        } catch (Exception e) {
            ExceptionHandler.handleUnexpectedException(e);
        }
        colProductId.setCellValueFactory(new PropertyValueFactory<>("productId"));
         colProductName.setCellValueFactory(new PropertyValueFactory<>("productName"));
         colSize.setCellValueFactory(new PropertyValueFactory<>("size"));
         colApprovedBy.setCellValueFactory(new PropertyValueFactory<>("approvedBy"));
         colApprovalDate.setCellValueFactory(new PropertyValueFactory<>("approvalDate"));
         colProductStatus.setCellValueFactory(new PropertyValueFactory<>("productStatus"));

        colApprovedBy.setCellValueFactory(cellData -> {
            Products product = cellData.getValue();
            int approvedById = product.getApprovedBy();
            try {
                String approvedByName = usersModel.getUserById(approvedById).getLastName() + ", "
                        + usersModel.getUserById(approvedById).getFirstName();
                return new javafx.beans.property.SimpleStringProperty(approvedByName);
            } catch (Exception e) {
                ExceptionHandler.handleUnexpectedException(e);
                return new javafx.beans.property.SimpleStringProperty("Unknown");
            }
        });

        tblProducts.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                selectedProduct = newValue;
                selectedProductId = selectedProduct.getProductId();
                try {

                    DisplayPhotos();
                } catch (Exception e) {
                    ExceptionHandler.handleUnexpectedException(e);
                    showError("Failed to display associated products - Contact System Admin");

                }
            }
        });



    }

    private void DisplayPhotos()  {

        tblPhotos.setItems(productsModel.getObservablePhotos(selectedProduct));
        colPhotoName.setCellValueFactory(new PropertyValueFactory<>("photoName"));
        colPhotoStatus.setCellValueFactory(new PropertyValueFactory<>("photoStatus"));
        colApprovedBy.setCellValueFactory(cellData -> {
            int approvedById = selectedProduct.getApprovedBy();
            try {
                String approvedByName = usersModel.getUserById(approvedById).getLastName() + ", "
                        + usersModel.getUserById(approvedById).getFirstName();
                return new javafx.beans.property.SimpleStringProperty(approvedByName);
            } catch (Exception e) {
                ExceptionHandler.handleUnexpectedException(e);
                return new javafx.beans.property.SimpleStringProperty("Unknown");
            }
        });
        colPhotoComment.setCellValueFactory(new PropertyValueFactory<>("photoComments"));
        
    }


    //Til Exception handeling - prompter en Alarm popup til GUI
    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Admin Error!");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    @FXML
    private void openAdminUserFrame(ActionEvent actionEvent) {
        try {
            parent.fillMainPane(new FXMLLoader(getClass().getResource("/dk/easv/belsign/AdminUserFrame.fxml")));
        } catch (Exception e) {
            showError("Failed to load Admin User Frame, contact system administrator");
        }
    }

    @FXML
    private void openCreateOrder(ActionEvent actionEvent) {
        try {
            String fxmlPath = "/dk/easv/belsign/CreateOrderPane.fxml";
            SceneService.openModalWindow(fxmlPath, "Create Order", 1200, 800);
            reloadOrders();
        } catch (Exception e) {
            ExceptionHandler.handleUnexpectedException(e);
            showError("Failed to open the Create Order window");
        }

    }

    public void openDeleteOrder(ActionEvent actionEvent) {
        try {
            if (selectedOrder != null) {
                ordersModel.deleteOrder(selectedOrderId);


                tblOrders.getItems().remove(selectedOrder);
                selectedOrder = null;
                selectedOrderId = 0;
                tblProducts.getItems().clear();
                tblPhotos.getItems().clear();

            } else {
                showError("No order selected for deletion.");
            }
        } catch (Exception e) {
            showError("Failed to delete the order: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void reloadOrders() {
        try {
            DisplayOrders();
        } catch (Exception e) {
            showError("Failed to reload orders: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
