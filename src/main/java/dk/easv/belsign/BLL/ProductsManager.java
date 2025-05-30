package dk.easv.belsign.BLL;

import dk.easv.belsign.BE.Products;
import dk.easv.belsign.DAL.IProductDAO;
import dk.easv.belsign.DAL.ProductsDAO;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

public class ProductsManager {

    private final IProductDAO<Products> productsDataAccess;

    public ProductsManager() throws Exception {
        productsDataAccess = new ProductsDAO();
    }

    public void create(Products product) throws Exception {
        // Execute the future and wait for the result (blocking operation)
        productsDataAccess.create(product);
    }

    public List<Integer> createProducts(List<Products> products) throws Exception {

        List<CompletableFuture<Integer>> futures = new ArrayList<>();


        for (Products product : products) {
            futures.add(productsDataAccess.createProducts(product));
        }


        CompletableFuture<Void> allFutures = CompletableFuture.allOf(
                futures.toArray(new CompletableFuture[0]));


        allFutures.join();


        return futures.stream()
                .map(CompletableFuture::join)
                .collect(Collectors.toList());
    }

    public List<Products> getAllProductsByOrderId(int orderId) throws Exception {
        return productsDataAccess.readAllByOrderId(orderId).join();
    }

    public void updateProduct(Products product) throws Exception {
        productsDataAccess.update(product);
    }

    public void deleteProductsByOrderId(int selectedOrderId) throws Exception {
        productsDataAccess.delete(selectedOrderId);
    }
}