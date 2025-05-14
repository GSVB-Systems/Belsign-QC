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

public class ProductsDAO implements IProductDAO<Products> {

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
    public CompletableFuture<Void> create(Products product) {
        return CompletableFuture.runAsync(() -> {
            String sql = "INSERT INTO products (photoId, orderId, productName, quantity, size) VALUES (?, ?, ?, ?, ?)";
            try (Connection conn = dbConnector.getConnection();
                 PreparedStatement statement = conn.prepareStatement(sql)) {

                conn.setAutoCommit(false); // Start transaktion

                statement.setInt(1, product.getPhotoId());
                statement.setInt(2, product.getOrderId());
                statement.setString(3, product.getProductName());
                statement.setInt(4, product.getQuantity());
                statement.setInt(5, product.getSize());

                statement.executeUpdate();
                conn.commit(); // Commit transaktion hvis alt lykkes

            } catch (SQLException e) {
                try {
                    Connection conn = dbConnector.getConnection();
                    conn.rollback(); // Rollback ved fejl
                } catch (SQLException rollbackEx) {
                    throw new RuntimeException("Rollback failed after insert error", rollbackEx);
                }
                throw new RuntimeException("Error inserting product into database", e);
            }
        }, executorService);
    }


    @Override
    public CompletableFuture<Void> delete(int id) throws Exception {
        return CompletableFuture.runAsync(() -> {
            String sql = "DELETE FROM products WHERE productId = ?";

            try (Connection conn = dbConnector.getConnection();
                 PreparedStatement statement = conn.prepareStatement(sql)) {

                conn.setAutoCommit(false); // Start transaktion

                statement.setInt(1, id);
                int affectedRows = statement.executeUpdate();

                if (affectedRows == 0) {
                    conn.rollback();
                    throw new RuntimeException("No product found with ID: " + id);
                }

                conn.commit(); // Commit hvis alt er ok

            } catch (SQLException e) {
                try {
                    Connection conn = dbConnector.getConnection();
                    conn.rollback(); // Rollback ved fejl
                } catch (SQLException rollbackEx) {
                    throw new RuntimeException("Rollback failed after delete error", rollbackEx);
                }
                throw new RuntimeException("Error deleting product with ID: " + id, e);
            }
        }, executorService);
    }




    @Override
    public CompletableFuture<Products> read(int productId) {
        return CompletableFuture.supplyAsync(() -> {
            String sql = "SELECT * FROM products WHERE productId = ?";

            try (Connection conn = dbConnector.getConnection();
                 PreparedStatement statement = conn.prepareStatement(sql)) {

                statement.setInt(1, productId);
                ResultSet rs = statement.executeQuery();

                if (rs.next()) {
                    int photoId = rs.getInt("photoId");
                    int orderId = rs.getInt("orderId");
                    String productName = rs.getString("productName");
                    int quantity = rs.getInt("quantity");
                    int size = rs.getInt("size");

                    return new Products(productId, photoId, orderId, productName, quantity, size);
                } else {
                    return null;
                }

            } catch (SQLException e) {
                throw new RuntimeException("Error fetching product with ID " + productId, e);
            }
        }, executorService);
    }


    @Override
    public CompletableFuture<List<Products>> readAll() {
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
                    int size = rs.getInt("size");

                    Products product = new Products(productId, photoId, orderId, productName, quantity, size);
                    products.add(product);
                }
                return products;
            } catch (SQLException e) {
                throw new RuntimeException("Error fetching products from database", e);
            }
        }, executorService);
    }

    @Override
    public CompletableFuture<Void> update(Products product) {
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

    @Override
    public CompletableFuture<List<Products>> readAllByOrderId(int orderId) {
        return CompletableFuture.supplyAsync(() -> {
            ArrayList<Products> products = new ArrayList<>();
            String sql = "SELECT p.*, ph.photoPath, ph.photoName, ph.photoStatus " +
                    "FROM Products p " +
                    "LEFT JOIN Photos ph ON p.photoId = ph.photoId " +
                    "WHERE p.orderId = ?";

            try (Connection conn = dbConnector.getConnection();
                 PreparedStatement statement = conn.prepareStatement(sql)) {

                statement.setInt(1, orderId);
                ResultSet rs = statement.executeQuery();

                while (rs.next()) {
                    int productId = rs.getInt("productId");
                    int photoId = rs.getInt("photoId");
                    String productName = rs.getString("productName");
                    int quantity = rs.getInt("quantity");
                    int size = rs.getInt("size");
                    String photoPath = rs.getString("photoPath");
                    String photoName = rs.getString("photoName");
                    String photoStatus = rs.getString("photoStatus");

                    Products product = new Products(productId, photoId, orderId, productName, quantity, size, photoPath, photoName, photoStatus);
                    products.add(product);
                }
                return products;
            } catch (SQLException e) {
                throw new RuntimeException("Error fetching products by order ID", e);
            }
        }, executorService);
    }

    public void shutdown() {
        executorService.shutdown();
    }
}