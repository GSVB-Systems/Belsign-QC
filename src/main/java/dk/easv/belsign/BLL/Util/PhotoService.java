package dk.easv.belsign.BLL.Util;

import dk.easv.belsign.BE.Photos;
import dk.easv.belsign.BE.Products;
import dk.easv.belsign.DAL.DBConnector;
import dk.easv.belsign.DAL.PhotoDAO;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.concurrent.CompletableFuture;


public class PhotoService {
    private final DBConnector dbConnector;
    private final PhotoDAO photoDAO;


    public PhotoService() throws IOException {
        this.dbConnector = DBConnector.getInstance();
        this.photoDAO = new PhotoDAO();
    }

    public CompletableFuture<Void> updatePhoto(Photos photo) throws Exception {
        return photoDAO.update(photo);
    }

    public CompletableFuture<Void> createPhoto(Photos photo) throws Exception {
        return photoDAO.create(photo);
    }

    public CompletableFuture<Void> deletePhoto(int photoId) throws Exception {
        return photoDAO.delete(photoId);
    }
    public CompletableFuture<Void> updatePhotoComment(Photos photo) {
        return CompletableFuture.runAsync(() -> {
            try (Connection conn = dbConnector.getConnection()) {
                conn.setAutoCommit(false);

                photoDAO.updatePhoto(photo);
                if (photo.getPhotoComments() != null) {
                    photoDAO.updatePhotoComment(photo);
                }

                conn.commit();
            } catch (SQLException e) {
                throw new RuntimeException("Failed to update photo or comments", e);
            }
        });
    }

    public CompletableFuture<Void> linkPhotosToProduct(Products product) {
        return photoDAO.getPhotosByProductId(product.getProductId())
                .thenAccept(photos -> {
                    product.setPhotos(photos);

                    if(!photos.isEmpty()) {
                        Photos firstPhoto = photos.get(0);
                        product.setPhotoPath(firstPhoto.getPhotoPath());
                        product.setPhotoName(firstPhoto.getPhotoName());
                        product.setPhotoStatus(firstPhoto.getPhotoStatus());
                    }
                });
    }

    public CompletableFuture<List<Photos>> getPhotosByProductId(int productId) {
        return photoDAO.getPhotosByProductId(productId);
    }

    public CompletableFuture<Void> savePhotos(List<Photos> photos) {
        CompletableFuture<?>[] futures = photos.stream()
                .map(photoDAO::create)
                .toArray(CompletableFuture[]::new);
        return CompletableFuture.allOf(futures);
    }
}

