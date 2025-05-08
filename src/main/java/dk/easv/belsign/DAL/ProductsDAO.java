package dk.easv.belsign.DAL;

import dk.easv.belsign.BE.Products;
import dk.easv.belsign.BLL.Util.ThreadShutdownUtil;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ProductsDAO implements IProductsDataAccess {

    private DBConnector dbConnector;
    private ExecutorService executorService;
    private final ThreadShutdownUtil threadShutdownUtil;

    public ProductsDAO() throws IOException {
        this.dbConnector = DBConnector.getInstance();
        this.threadShutdownUtil = ThreadShutdownUtil.getInstance();
        this.executorService = Executors.newFixedThreadPool(4);
        threadShutdownUtil.registerExecutorService(executorService);
    }

    @Override
    public CompletableFuture<List<Products>> getAllProducts() {
        return CompletableFuture.supplyAsync(() -> {
            ArrayList<Products> products = new ArrayList<>();
            String sql = "SELECT * FROM products";

            try(Connection conn = dbConnector.getConnection();
                PreparedStatement statement = conn.prepareStatement(sql)){
                ResultSet rs = statement.executeQuery(sql);

                while (rs.next()) {
                    int productId = rs.getInt("productId");
                    int photoId = rs.getInt("photoId");
                    int orderId = rs.getInt("orderId");
                    String productName = rs.getString("productName");
                    int quantity = rs.getInt("quantity");

                    Products product = new Products(productId, photoId, orderId, productName, quantity);
                    products.add(product);
                }
                return products;
            } catch (SQLException e) {
                throw new RuntimeException("Error fetching products from database", e);
            }
        }, executorService);
    }

    @Override
    public CompletableFuture<Void> updateProduct(Products product) {
        return CompletableFuture.runAsync(() -> {
            String sql = "UPDATE products SET quantity = ?, photoId = ?, productName = ? WHERE productId = ?";

            try(Connection conn = dbConnector.getConnection()){
                conn.setAutoCommit(false);

                try(PreparedStatement statement = conn.prepareStatement(sql)){
                    statement.setInt(1, product.getQuantity());
                    statement.setInt(2, product.getPhotoId());
                    statement.setString(3, product.getProductName());
                    statement.setInt(4, product.getProductId());

                    statement.executeUpdate();
                    conn.commit();
                } catch (SQLException e) {
                    try {
                        conn.rollback();
                    } catch (SQLException ex) {
                        throw new RuntimeException("Error rolling back transaction", ex);
                    }
                    throw new RuntimeException("Error updating product in database", e);
                }
            } catch (SQLException e) {
                throw new RuntimeException("Database connection error", e);
            }
        }, executorService);
    }

    public void shutdown() {
        executorService.shutdown();
    }
}