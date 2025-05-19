package dk.easv.belsign.BE;

public class Photos {
    private int photoId;
    private String photoName;
    private String photoPath;
    private String photoStatus;
    private String photoComment;
    private int productId;

    public Photos(int photoId, int productId, String photoName, String photoPath, String photoStatus, String photoComment) {
        this.photoId = photoId;
        this.photoName = photoName;
        this.photoPath = photoPath;
        this.photoStatus = photoStatus;
        this.photoComment = photoComment;
        this.productId = productId;
    }

    public Photos(int photoId, int productId, String pending) {
    }

    public int getProductId() {
        return productId;
    }

    public void setProductId(int productId) {
        this.productId = productId;
    }

    public String getPhotoComment() {
        return photoComment;
    }

    public void setPhotoComment(String photoComment) {
        this.photoComment = photoComment;
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


}
