package dk.easv.belsign.DAL;

import dk.easv.belsign.BE.Products;
import dk.easv.belsign.BLL.Util.ThreadShutdownUtil;
import dk.easv.belsign.BE.Photos;

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
            String sql = "INSERT INTO products ( orderId, productName, quantity, size) VALUES (?, ?, ?, ?, ?)";
            try (Connection conn = dbConnector.getConnection();
                 PreparedStatement statement = conn.prepareStatement(sql)) {

                conn.setAutoCommit(false); // Start transaktion

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

                    return new Products(productId, orderId, productName, quantity, size);
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
                    int orderId = rs.getInt("orderId");
                    String productName = rs.getString("productName");
                    int quantity = rs.getInt("quantity");
                    int size = rs.getInt("size");

                    Products product = new Products(productId, orderId, productName, quantity, size);
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
            String productSql = "UPDATE products SET quantity = ?, productName = ?, size = ? WHERE productId = ?";
            String photoSql = "UPDATE Photos SET photoName = ?, photoPath = ?, photoStatus = ? WHERE productId = ? AND photoId = ?";
            String commentSql =
                    "MERGE INTO PhotoComments AS target " +
                            "USING (VALUES(?, ?)) AS source(photoId, photoComment) " +
                            "ON target.photoId = source.photoId " +
                            "WHEN MATCHED THEN " +
                            "  UPDATE SET target.photoComment = source.photoComment " +
                            "WHEN NOT MATCHED THEN " +
                            "  INSERT (photoId, photoComment) VALUES (source.photoId, source.photoComment);";
            String approvalDateSql = "UPDATE ProductApproval SET approvalDate = CURRENT_TIMESTAMP WHERE productId = ?";

            try (Connection conn = dbConnector.getConnection()) {
                // Update product information
                conn.setAutoCommit(false); // Start transaction for product update
                try {
                    try (PreparedStatement productStatement = conn.prepareStatement(productSql)) {
                        productStatement.setInt(1, product.getQuantity());
                        productStatement.setString(2, product.getProductName());
                        productStatement.setInt(3, product.getSize());
                        productStatement.setInt(4, product.getProductId());
                        productStatement.executeUpdate();
                    }

                    try (PreparedStatement approvalDateStatement = conn.prepareStatement(approvalDateSql)) {
                        approvalDateStatement.setInt(1, product.getProductId());
                        approvalDateStatement.executeUpdate();
                    }

                    conn.commit();
                } catch (SQLException e) {
                    conn.rollback();
                    throw new RuntimeException("Failed to update product information", e);
                } finally {
                    conn.setAutoCommit(true); // Reset for next transaction
                }

                // Process each photo in a separate transaction
                if (product.getPhotos() != null && !product.getPhotos().isEmpty()) {
                    for (Photos photo : product.getPhotos()) {
                        conn.setAutoCommit(false); // Start transaction for each photo
                        try {
                            // Update photo information
                            try (PreparedStatement photoStatement = conn.prepareStatement(photoSql)) {
                                photoStatement.setString(1, photo.getPhotoName());
                                photoStatement.setString(2, photo.getPhotoPath());
                                photoStatement.setString(3, photo.getPhotoStatus());
                                photoStatement.setInt(4, photo.getProductId());
                                photoStatement.setInt(5, photo.getPhotoId());
                                photoStatement.executeUpdate();
                            }

                            // Update photo comment
                            if (photo.getPhotoComment() != null) {
                                try (PreparedStatement commentStatement = conn.prepareStatement(commentSql)) {
                                    commentStatement.setInt(1, photo.getPhotoId());
                                    commentStatement.setString(2, photo.getPhotoComment());
                                    commentStatement.executeUpdate();
                                }
                            }
                            conn.commit();
                        } catch (SQLException e) {
                            conn.rollback();
                            throw new RuntimeException("Failed to update photo ID: " + photo.getPhotoId(), e);
                        } finally {
                            conn.setAutoCommit(true); // Reset for next photo
                        }
                    }
                }
            } catch (SQLException e) {
                throw new RuntimeException("Database connection error during product update", e);
            }
        }, executorService);
    }

    @Override
    public CompletableFuture<List<Products>> readAllByOrderId(int orderId) {
        return CompletableFuture.supplyAsync(() -> {
            List<Products> products = new ArrayList<>();
            List<Integer> processedProductIds = new ArrayList<>();

            String sql = "SELECT p.*, ph.photoPath, ph.photoName, ph.photoStatus, ph.photoId AS photo_id, " +
                    "pc.photoComment AS photoComment " +
                    "FROM Products p " +
                    "LEFT JOIN Photos ph ON p.productId = ph.productId " +
                    "LEFT JOIN PhotoComments pc ON ph.photoId = pc.photoId " +
                    "WHERE p.orderId = ?";

            try (Connection conn = dbConnector.getConnection();
                 PreparedStatement statement = conn.prepareStatement(sql)) {

                statement.setInt(1, orderId);
                ResultSet rs = statement.executeQuery();

                while (rs.next()) {
                    int productId = rs.getInt("productId");
                    String productName = rs.getString("productName");
                    int quantity = rs.getInt("quantity");
                    int size = rs.getInt("size");

                    // Find or create the product
                    Products product = null;
                    if (!processedProductIds.contains(productId)) {
                        product = new Products(productId, orderId, productName, quantity, size);
                        product.setPhotos(new ArrayList<>());
                        products.add(product);
                        processedProductIds.add(productId);
                    } else {
                        for (Products p : products) {
                            if (p.getProductId() == productId) {
                                product = p;
                                break;
                            }
                        }
                    }

                    // Add photo if it exists
                    if (product != null && rs.getObject("photo_id") != null) {
                        int photoId = rs.getInt("photo_id");
                        String photoPath = rs.getString("photoPath");
                        String photoName = rs.getString("photoName");
                        String photoStatus = rs.getString("photoStatus");
                        String photoComment = rs.getString("photoComment");

                        // Create photo with comment
                        Photos photo = new Photos(photoId, productId, photoName, photoPath, photoStatus, photoComment);
                        product.getPhotos().add(photo);

                        // For backward compatibility
                        if (product.getPhotoPath() == null) {
                            product.setPhotoPath(photoPath);
                            product.setPhotoName(photoName);
                            product.setPhotoStatus(photoStatus);
                        }
                    }
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