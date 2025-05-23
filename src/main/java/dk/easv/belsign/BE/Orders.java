package dk.easv.belsign.BE;


import java.time.LocalDateTime;

public class Orders {
    private int orderId;
    private LocalDateTime ApprovalDate;
    private String approvalStatus;



    public LocalDateTime getApprovalDate() {
        return ApprovalDate;
    }
    public void setApprovalDate(LocalDateTime approvalDate) {
        ApprovalDate = approvalDate;
    }
    public String getApprovalStatus() {
        return approvalStatus;
    }
    public void setApprovalStatus(String orderStatus) {
        this.approvalStatus = orderStatus;
    }


    public int getOrderId() {
        return orderId;
    }

    public void setOrderId(int orderId) {
        this.orderId = orderId;
    }

    public Orders(int orderId) {
        this.orderId = orderId;
    }

    public Orders(int orderId, LocalDateTime approvalDate, String approvalStatus) {
        this.orderId = orderId;
        this.ApprovalDate = approvalDate;
        this.approvalStatus = approvalStatus;
    }
}
