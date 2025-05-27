package dk.easv.belsign.GUI;

import dk.easv.belsign.BE.Orders;
import dk.easv.belsign.BE.Photos;
import dk.easv.belsign.BE.Products;
import dk.easv.belsign.Models.OrdersModel;
import dk.easv.belsign.Models.PhotosModel;
import dk.easv.belsign.Models.ProductsModel;
import dk.easv.belsign.Models.UsersModel;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Alert;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

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
    private TableColumn colOrderId;
    @FXML
    private TableColumn colProductId;
    @FXML
    private TableColumn colProductName;
    @FXML
    private TableColumn colSize;
    @FXML
    private TableColumn colPhotoName;
    @FXML
    private TableColumn colPhotoStatus;
    @FXML
    private TableColumn colApprovedBy;
    @FXML
    private TableColumn colApprovalDate;
    @FXML
    private TableColumn colProductStatus;

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
            showError("Failed to initialize OrdersModel - Contact System Administrator: ");
            e.printStackTrace();
        }

        DisplayOrders();

    }





    private void DisplayOrders() {

        tblOrders.setItems(ordersModel.getObservableOrders());
        colOrderId.setCellValueFactory(new PropertyValueFactory<>("orderId"));

        tblOrders.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                selectedOrder = newValue;
                selectedOrderId = selectedOrder.getOrderId();
                try {
                    DisplayProducts();
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        });


    }

    private void DisplayProducts() throws Exception {
        // Implement the logic to display products in the tblProducts TableView

         tblProducts.setItems(productsModel.getObservableProducts(selectedOrderId));
         colProductId.setCellValueFactory(new PropertyValueFactory<>("productId"));
         colProductName.setCellValueFactory(new PropertyValueFactory<>("productName"));
         colSize.setCellValueFactory(new PropertyValueFactory<>("size"));
         colApprovedBy.setCellValueFactory(new PropertyValueFactory<>("approvedBy"));
         colApprovalDate.setCellValueFactory(new PropertyValueFactory<>("approvalDate"));
         colProductStatus.setCellValueFactory(new PropertyValueFactory<>("productStatus"));

        tblProducts.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                selectedProduct = newValue;
                selectedProductId = selectedProduct.getProductId();
                try {

                    DisplayPhotos();
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        });



    }

    private void DisplayPhotos() throws Exception {

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
                return new javafx.beans.property.SimpleStringProperty("Unknown");
            }
        });    }


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
            showError("Failed to load Admin User Frame, contact system administrator: " + e.getMessage());
        }
    }
}
