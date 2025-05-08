package dk.easv.belsign.BLL;

import dk.easv.belsign.BE.Products;
import dk.easv.belsign.DAL.IProductsDataAccess;
import dk.easv.belsign.DAL.ProductsDAO;

import java.io.IOException;
import java.util.List;

public class ProductsManager {

    private final IProductsDataAccess productsDataAccess;

    public ProductsManager() throws IOException {
        productsDataAccess = new ProductsDAO();
    }

    public List<Products> getAllProducts() {
        return productsDataAccess.getAllProducts().join();
    }

    public void updateProduct(Products product) {
        productsDataAccess.updateProduct(product);
    }
}