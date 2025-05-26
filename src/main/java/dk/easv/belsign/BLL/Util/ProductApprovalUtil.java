package dk.easv.belsign.BLL.Util;

import dk.easv.belsign.BE.Photos;
import dk.easv.belsign.BE.Products;
import dk.easv.belsign.BE.Users;
import dk.easv.belsign.BLL.ProductsManager;
import dk.easv.belsign.Models.ProductsModel;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class ProductApprovalUtil {

    private final ProductsModel productsModel;

    public ProductApprovalUtil(ProductsModel productsModel) throws Exception {
        this.productsModel = new ProductsModel();
    }


    public void setProductStatus(Products product, int approvedBy) throws Exception {
    if (product == null) {
        throw new IllegalArgumentException("Product cannot be null");
    }

    List<Photos> photos = product.getPhotos();

    if (photos == null || photos.isEmpty()) {
        throw new IllegalStateException("Product must have photos to determine status");
    }

    boolean allApproved = true;
    boolean anyRejected = false;

    for (Photos photo : photos) {
        String status = photo.getPhotoStatus();
        if (!"Approved".equalsIgnoreCase(status)) {
            allApproved = false;
        }
        if ("Rejected".equalsIgnoreCase(status) || "Declined".equalsIgnoreCase(status)) {
            anyRejected = true;
        }
    }

    if (allApproved) {
        product.setProductStatus("Approved");
    } else if (anyRejected) {
        product.setProductStatus("Declined");
    } else {
        throw new IllegalStateException("Cannot set product status: Photos are pending or incomplete");
    }

    product.setApprovalDate(java.time.LocalDateTime.now());
    product.setApprovedBy(approvedBy);

    // Persist the change
    productsModel.updateProduct(product);
}

    public boolean isProductApproved(Products product) {
        if (product == null) {
            return false;
        }
        return "Approved".equals(product.getProductStatus());
    }

    public boolean canUserApprove(Users user) {
        if (user == null) {
            return false;
        }
        return user.getRoleId() == 1; // Assuming 1 is admin role
    }

    public String getApprovalStatusText(Products product) {
        if (product == null) {
            return "";
        }

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

        switch (product.getProductStatus()) {
            case "Pending":
                return "Awaiting Approval";
            case "Approved":
                return "Approved on " + (product.getApprovalDate() != null ?
                        product.getApprovalDate().format(formatter) : "");
            case "Rejected":
                return "Rejected" + (product.getApprovalDate() != null ?
                        " on " + product.getApprovalDate().format(formatter) : "");
            default:
                return product.getProductStatus();
        }
    }

    public void approveProduct(Products product, int approvedBy) {
        if (product == null) {
            throw new IllegalArgumentException("Product cannot be null");
        }

        List<Photos> photos = product.getPhotos();

        // Check that all photos are approved
        if (photos != null && !photos.isEmpty()) {
            for (Photos photo : photos) {
                if (!"Approved".equalsIgnoreCase(photo.getPhotoStatus())) {
                    throw new IllegalStateException("Cannot approve product: Not all photos are approved");
                }
            }
        }

        // Set product as approved
        product.setProductStatus("Approved");
        product.setApprovalDate(LocalDateTime.now());
        product.setApprovedBy(approvedBy);

        try {
            // Persist the change
            productsModel.updateProduct(product);
        } catch (Exception e) {
            throw new RuntimeException("Failed to update product", e);
        }
    }

    public void rejectProduct(Products product) {
        if (product == null) {
            throw new IllegalArgumentException("Product cannot be null");
        }

        List<Photos> photos = product.getPhotos();

        if (photos == null || photos.isEmpty()) {
            throw new IllegalStateException("Cannot reject product: No photos associated with this product");
        }

        boolean hasDeclinedPhoto = false;

        for (Photos photo : photos) {
            String status = photo.getPhotoStatus();
            if ("Rejected".equalsIgnoreCase(status) || "Declined".equalsIgnoreCase(status)) {
                hasDeclinedPhoto = true;
                break;
            }
        }

        if (!hasDeclinedPhoto) {
            throw new IllegalStateException("Cannot reject product: No photos are declined");
        }

        // Set product as rejected
        product.setProductStatus("Rejected");
        product.setApprovalDate(LocalDateTime.now());

        try {
            // Persist the change
            productsModel.updateProduct(product);
        } catch (Exception e) {
            throw new RuntimeException("Failed to update product", e);
        }
    }
}