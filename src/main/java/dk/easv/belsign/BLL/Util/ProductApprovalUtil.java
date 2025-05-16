package dk.easv.belsign.BLL.Util;

import dk.easv.belsign.BE.Photos;
import dk.easv.belsign.BE.Products;
import dk.easv.belsign.BLL.ProductsManager;

import java.time.LocalDateTime;
import java.util.List;

public class ProductApprovalUtil {
    private final ProductsManager productsManager;

    public ProductApprovalUtil(ProductsManager productsManager) {
        this.productsManager = productsManager;
    }


    public boolean checkAndApproveProduct() {
        try {

            Products product = ProductSession.getEnteredProduct();

            if (product == null || product.getPhotos() == null || product.getPhotos().isEmpty()) {
                return false;
            }


            if (areAllPhotosApproved(product.getPhotos())) {
                return updateProductApproval(product);
            } else {
                return false;
            }
        } catch (Exception e) {
            System.err.println("Error checking product approval: " + e.getMessage());
            return false;
        }
    }

    private boolean areAllPhotosApproved(List<Photos> photos) {
        for (Photos photo : photos) {
            if (!"approved".equalsIgnoreCase(photo.getPhotoStatus())) {
                return false;
            }
        }
        return true;
    }

    private boolean updateProductApproval(Products product) {
        try {

            product.setApprovalDate(LocalDateTime.now());
            product.setProductStatus("Approved");
            product.setApprovedBy(UserSession.getLoggedInUser().getUserId());


            productsManager.updateProduct(product);
            return true;
        } catch (Exception e) {
            System.err.println("Error updating product approval: " + e.getMessage());
            return false;
        }
    }
}