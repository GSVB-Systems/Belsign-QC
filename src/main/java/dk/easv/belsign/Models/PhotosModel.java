package dk.easv.belsign.Models;

import dk.easv.belsign.BE.Photos;
import dk.easv.belsign.BLL.PhotoManager;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.SQLException;
import java.util.List;

public class PhotosModel {

    private final ObservableList<Photos> observablePhotos;
    private final PhotoManager photoManager;

    public PhotosModel() throws Exception {
        this.photoManager = new PhotoManager();
        observablePhotos = FXCollections.observableArrayList();
        //observablePhotos.addAll(photoManager.getAllPhotos());
    }

    public ObservableList<Photos> getObservablePhotos() {
        return observablePhotos;
    }

    public void updatePhoto(Photos photo) throws Exception {
        photoManager.updatePhoto(photo);
    }

    public void createPhoto(Photos photo) throws Exception {
        photoManager.createPhoto(photo);
    }

    public Photos getPhotoById(int photoId) throws Exception {
        return photoManager.getPhotoById(photoId);
    }

    public void updatePhotoComment(Photos photo) throws SQLException {
        photoManager.updatePhotoComment(photo);
    }

    public void updatePhotoList(List<Photos> photos) {
        photoManager.updatePhotoList(photos);
    }


    public void deletePhotos(List<Photos> Photos) throws Exception {
        for (Photos photo : Photos) {

            photoManager.deletePhoto(photo);

        }

    }
}
