package dk.easv.belsign.BE;

import java.util.List;

public class Photos {
    private int photoId;
    private String photoName;
    private String photoPath;
    private String photoStatus;
    private String photoComments;
    private int productId;
    private List<Photos> photos;

    public  Photos(int photoId, String photoName, String photoPath, String photoStatus, int productId, String photoComments) {
        this.photoId = photoId;
        this.photoName = photoName;
        this.photoPath = photoPath;
        this.photoStatus = photoStatus;
        this.photoComments = photoComments;
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

    public String getPhotoComments() {
        return photoComments;
    }

    public void setPhotoComment(String photoComments) {
        this.photoComments = photoComments;
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

    public List<Photos> getPhotos() {
        return photos;
    }

    public void setPhotos(List<Photos> photos) {
        this.photos = photos;
    }
}
