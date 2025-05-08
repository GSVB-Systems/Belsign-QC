package dk.easv.belsign.DAL;

import dk.easv.belsign.BE.Products;

import java.sql.SQLException;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface IProductsDataAccess {
    CompletableFuture<List<Products>> getAllProducts();
    CompletableFuture<Void> updateProduct(Products product);
}