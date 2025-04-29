package dk.easv.belsign.BE;

public class Products {

    private int productId;
    private int photoId;
    private int orderId;
    private String productName;
    private int quantity;


    public Products(int productId, int photoId, int orderId, String productName, int quantity) {
        this.productId = productId;
        this.photoId = photoId;
        this.orderId = orderId;
        this.productName = productName;
        this.quantity = quantity;
    }
}
