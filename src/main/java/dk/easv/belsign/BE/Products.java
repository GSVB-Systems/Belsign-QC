package dk.easv.belsign.BE;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Products {

    private int productId;

    private int orderId;
    private String productName;
    private int quantity;
    private int size;
    private String photoPath;
    private String photoName;
    private String photoStatus;
    private List<Photos> photos;
    private LocalDateTime approvalDate;
    private String productStatus;
    private int approvedBy;

    public Products(int i, int i1, String testProduct, int i2, int i3, String pending) {
    }

    public List<Photos> getPhotos() {
        return photos;
    }
    public void setPhotos(List<Photos> photos) {
        this.photos = photos;
    }

    public String getPhotoName() {
        return photoName;
    }
    public void setPhotoName(String photoName) {
        this.photoName = photoName;
    }
    public String getPhotoStatus() {
        return photoStatus;
    }
    public void setPhotoStatus(String photoStatus) {
        this.photoStatus = photoStatus;
    }

    public String getPhotoPath() {
        return photoPath;
    }
    public void setPhotoPath(String photoPath) {
        this.photoPath = photoPath;
    }

    public int getProductId() {
        return productId;
    }

    public void setProductId(int productId) {
        this.productId = productId;
    }

    public LocalDateTime getApprovalDate() {
        return approvalDate;
    }

    public void setApprovalDate(LocalDateTime approvalDate) {
        this.approvalDate = approvalDate;
    }

    public String getProductStatus() {
        return productStatus;
    }

    public void setProductStatus(String productStatus) {
        this.productStatus = productStatus;
    }

    public int getApprovedBy() {
        return approvedBy;
    }

    public void setApprovedBy(int approvedBy) {
        this.approvedBy = approvedBy;
    }

    public int getOrderId() {
        return orderId;
    }

    public void setOrderId(int orderId) {
        this.orderId = orderId;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public Products(int productId, int orderId, String productName, int quantity, int size) {
        this.productId = productId;
        this.orderId = orderId;
        this.productName = productName;
        this.quantity = quantity;
        this.size = size;
    }

    public Products(int productId, int orderId, String productName, int quantity, int size, String photoPath, String photoName, String photoStatus) {
        this.productId = productId;
        this.orderId = orderId;
        this.productName = productName;
        this.quantity = quantity;
        this.size = size;
        this.photoPath = photoPath;
        this.photoName = photoName;
        this.photoStatus = photoStatus;
        this.photos = new ArrayList<>();
    }
}
