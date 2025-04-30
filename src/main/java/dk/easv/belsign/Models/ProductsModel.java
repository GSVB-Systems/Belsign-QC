package dk.easv.belsign.Models;

import dk.easv.belsign.BE.Products;
import dk.easv.belsign.BLL.ProductsManager;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.io.IOException;
import java.sql.SQLException;

public class ProductsModel {

    private final ObservableList<Products> observableProducts;
    private final ProductsManager productsManager;

    public ProductsModel() throws SQLException, IOException {
        this.productsManager = new ProductsManager();
        observableProducts = FXCollections.observableArrayList();
        observableProducts.addAll(productsManager.getAllProducts());
    }

    public ObservableList<Products> getObservableProducts() {
        return observableProducts;
    }

    public void updateProduct(Products product) throws SQLException {
        productsManager.updateProduct(product);
    }
}
