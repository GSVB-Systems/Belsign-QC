package dk.easv.belsign.BE;

public class Orders {
    private String orderId;
    private int productQuantity;

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public int getProductQuantity() {
        return productQuantity;
    }

    public void setProductQuantity(int productQuantity) {
        this.productQuantity = productQuantity;
    }

    public Orders(String orderId, int productQuantity) {
        this.orderId = orderId;
        this.productQuantity = productQuantity;
    }
}
