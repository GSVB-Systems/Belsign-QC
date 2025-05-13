package dk.easv.belsign.BLL;

import dk.easv.belsign.BE.Products;
import dk.easv.belsign.DAL.IProductDAO;
import dk.easv.belsign.DAL.ProductsDAO;

import java.io.IOException;
import java.util.List;

public class ProductsManager {

    private final IProductDAO productsDataAccess;

    public ProductsManager() throws IOException {
        productsDataAccess = new ProductsDAO();
    }

    public List<Products> getAllProductsByOrderId(int orderId) throws Exception {
        return (List<Products>) productsDataAccess.readAllByOrderId(orderId).join();
    }



    public void updateProduct(Products product) throws Exception {
        productsDataAccess.update(product);
    }
}