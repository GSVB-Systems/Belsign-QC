package dk.easv.belsign.BE;

public class Orders {
    private int orderId;
    private int productQuantity;

    public int getOrderId() {
        return orderId;
    }

    public void setOrderId(int orderId) {
        this.orderId = orderId;
    }

    public int getProductQuantity() {
        return productQuantity;
    }

    public void setProductQuantity(int productQuantity) {
        this.productQuantity = productQuantity;
    }

    public Orders(int orderId, int productQuantity) {
        this.orderId = orderId;
        this.productQuantity = productQuantity;
    }
}
