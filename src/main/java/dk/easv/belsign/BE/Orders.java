package dk.easv.belsign.BE;

public class Orders {
    private String orderId;
    private int productQuantity;

    public Orders(String orderId, int productQuantity) {
        this.orderId = orderId;
        this.productQuantity = productQuantity;
    }
}
