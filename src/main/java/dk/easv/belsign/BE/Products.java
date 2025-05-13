package dk.easv.belsign.BE;

public class Products {

    private int productId;
    private int photoId;
    private int orderId;
    private String productName;
    private int quantity;
    private int size;
    private String photoPath;

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

    public int getPhotoId() {
        return photoId;
    }

    public void setPhotoId(int photoId) {
        this.photoId = photoId;
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

    public Products(int productId, int photoId, int orderId, String productName, int quantity, int size) {
        this.productId = productId;
        this.photoId = photoId;
        this.orderId = orderId;
        this.productName = productName;
        this.quantity = quantity;
        this.size = size;
    }

    public Products(int productId, int photoId, int orderId, String productName, int quantity, int size, String photoPath) {
        this.productId = productId;
        this.photoId = photoId;
        this.orderId = orderId;
        this.productName = productName;
        this.quantity = quantity;
        this.size = size;
        this.photoPath = photoPath;
    }
}
