package dk.easv.belsign.Models;

import dk.easv.belsign.BE.Photos;
import dk.easv.belsign.BE.Products;
import dk.easv.belsign.BLL.ProductsManager;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.ArrayList;
import java.util.List;

public class ProductsModel {

    private final ObservableList<Products> observableProducts;
    private ObservableList<Photos> observablePhotos;
    private final ProductsManager productsManager;


    public ProductsModel() throws Exception {
        this.productsManager = new ProductsManager();
        observableProducts = FXCollections.observableArrayList();


    }

    public ObservableList<Photos> getObservablePhotos(Products product) {
        observablePhotos = FXCollections.observableArrayList(product.getPhotos());
        return observablePhotos;
    }


    public ObservableList<Products> getObservableProducts(int orderId) throws Exception {
        observableProducts.clear();
        observableProducts.addAll(productsManager.getAllProductsByOrderId(orderId));
        return observableProducts;
    }

    public ObservableList<Products> getProductsByOrder() {
        return observableProducts;

    }

    public void create(Products product) throws Exception {
            productsManager.create(product);

    }

    public List<Integer> createProducts(ArrayList<Products> productList) throws Exception {
        return productsManager.createProducts(productList);
    }

    public void updateProduct(Products product) throws Exception {
        productsManager.updateProduct(product);

    }

    public void deleteProductsByOrderId(int selectedOrderId) throws Exception {
        productsManager.deleteProductsByOrderId(selectedOrderId);
    }
}
