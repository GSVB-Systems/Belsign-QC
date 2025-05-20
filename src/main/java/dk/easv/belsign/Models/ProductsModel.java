package dk.easv.belsign.Models;

import dk.easv.belsign.BE.Photos;
import dk.easv.belsign.BE.Products;
import dk.easv.belsign.BLL.ProductsManager;
import dk.easv.belsign.BLL.Util.PhotoService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

public class ProductsModel {

    private final ObservableList<Products> observableProducts;
    private final ProductsManager productsManager;
    private final PhotoService photoService;

    public ProductsModel() throws Exception {
        this.productsManager = new ProductsManager();
        this.photoService = new PhotoService();
        observableProducts = FXCollections.observableArrayList();

    }


    public ObservableList<Products> getObservableProducts(int orderId) throws Exception {
        observableProducts.clear();
        observableProducts.addAll(productsManager.getAllProductsByOrderId(orderId));
        return observableProducts;
    }

    public ObservableList<Products> getProductsByOrder() {
        return observableProducts;

    }

    public void updateProduct(Products product) throws Exception {
        productsManager.updateProduct(product);

    }

    public List<Photos> getPhotosByProductId(int productId) throws Exception {
        return (List<Photos>) photoService.getPhotosByProductId(productId);
    }

    public void updatePhoto (Photos photos) throws Exception {
        photoService.updatePhoto(photos);
    }

    public void updatePhotoAndComments (Photos photo) {
        photoService.updatePhotoAndComments(photo);
    }
}
