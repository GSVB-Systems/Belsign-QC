package dk.easv.belsign.BE;

public class Photos {
    private int photoId;
    private String photoName;
    private String photoPath;
    private int orderId;
    private String photoStatus;

    public Photos(int photoId, String photoName, String photoPath, int orderId, String photoStatus) {
        this.photoId = photoId;
        this.photoName = photoName;
        this.photoPath = photoPath;
        this.orderId = orderId;
        this.photoStatus = photoStatus;
    }

    public String getPhotoStatus() {
        return photoStatus;
    }

    public void setPhotoStatus(String photoStatus) {
        this.photoStatus = photoStatus;
    }

    public int getPhotoId() {
        return photoId;
    }

    public void setPhotoId(int photoId) {
        this.photoId = photoId;
    }

    public String getPhotoName() {
        return photoName;
    }

    public void setPhotoName(String photoName) {
        this.photoName = photoName;
    }

    public String getPhotoPath() {
        return photoPath;
    }

    public void setPhotoPath(String photoPath) {
        this.photoPath = photoPath;
    }

    public int getOrderId() {
        return orderId;
    }

    public void setOrderId(int orderId) {
        this.orderId = orderId;
    }
}
