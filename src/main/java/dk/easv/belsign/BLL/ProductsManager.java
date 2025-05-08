package dk.easv.belsign.BLL;

import dk.easv.belsign.BE.Products;
import dk.easv.belsign.DAL.ICrudRepo;
import dk.easv.belsign.DAL.ProductsDAO;

import java.io.IOException;
import java.util.List;

public class ProductsManager {

    private final ICrudRepo productsDataAccess;

    public ProductsManager() throws IOException {
        productsDataAccess = new ProductsDAO();
    }

    public List<Products> getAllProducts() throws Exception {
        return (List<Products>) productsDataAccess.readAll().join();
    }

    public void updateProduct(Products product) throws Exception {
        productsDataAccess.update(product);
    }
}