package dk.easv.belsign.BLL;

import dk.easv.belsign.BE.Photos;
import dk.easv.belsign.DAL.IPhotoDAO;
import dk.easv.belsign.DAL.PhotoDAO;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class PhotoManager {

    private final IPhotoDAO photoDataAccess;

    public PhotoManager() throws IOException {
        photoDataAccess = new PhotoDAO();
    }

    public List<Photos> getAllPhotos() throws Exception {
        return (List<Photos>) photoDataAccess.readAll().join();
    }

    public void updatePhoto(Photos photo) throws Exception {
        photoDataAccess.update(photo);
    }

    public Photos getPhotoById(int photoId) throws Exception {
        return (Photos) photoDataAccess.read(photoId).join();
    }

    public void updatePhotoComment(Photos photo) throws SQLException {
        photoDataAccess.updatePhotoComment(photo);
    }

    public void updatePhotoList(List<Photos> photos) {
        List<Photos> validPhotos = new ArrayList<>(photos);

        validPhotos.removeIf(photo -> Objects.equals(photo.getPhotoName(), " ") || photo.getPhotoName().trim().isEmpty());

        for (Photos photo : validPhotos) {
            try {
                photoDataAccess.update(photo);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void createPhoto(Photos photo) throws Exception {
        photoDataAccess.create(photo);
    }

    public void deletePhoto(Photos photo) throws Exception {
        photoDataAccess.delete(photo.getPhotoId());
    }
}
