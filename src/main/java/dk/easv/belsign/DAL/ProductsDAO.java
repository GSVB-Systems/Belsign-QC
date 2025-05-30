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
    public CompletableFuture<Void> create(Products product) throws Exception {
        return CompletableFuture.runAsync(() -> {
            String sql = "INSERT INTO products (orderId, productName, size) VALUES (?, ?, ?)";
            try (Connection conn = dbConnector.getConnection();
                 PreparedStatement statement = conn.prepareStatement(sql)) {

                conn.setAutoCommit(false); // Start transaktion

                statement.setInt(1, product.getOrderId());
                statement.setString(2, product.getProductName());
                statement.setInt(3, product.getSize());

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
    public CompletableFuture<Integer> createProducts(Products product) {
        return CompletableFuture.supplyAsync(() -> {
            String sql = "INSERT INTO products (orderId, productName, size) VALUES (?, ?, ?)";
            try (Connection conn = dbConnector.getConnection();
                 PreparedStatement statement = conn.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {

                conn.setAutoCommit(false);

                statement.setInt(1, product.getOrderId());
                statement.setString(2, product.getProductName());
                statement.setInt(3, product.getSize());

                statement.executeUpdate();

                // Get the generated ID
                try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        int productId = generatedKeys.getInt(1);
                        conn.commit();
                        return productId;
                    } else {
                        conn.rollback();
                        throw new SQLException("Creating product failed, no ID obtained.");
                    }
                }
            } catch (SQLException e) {
                // Error handling
                throw new RuntimeException("Error creating product", e);
            }
        }, executorService);
    }

    @Override
    public CompletableFuture<Void> delete(int id) throws Exception {
        return CompletableFuture.runAsync(() -> {
            String sql = "DELETE FROM products WHERE orderId = ?";

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
                    int size = rs.getInt("size");

                    return new Products(productId, orderId, productName, size);
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
                    int size = rs.getInt("size");

                    Products product = new Products(productId, orderId, productName, size);
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
            String productsSql = "UPDATE Products SET orderId = ?, productName = ?, size = ? WHERE productId = ?";
            String approvalSql = "UPDATE ProductApproval SET userId = ?, approvalDate = ?, productStatus = ? WHERE productId = ?";
            String insertApprovalSql = "INSERT INTO ProductApproval (productId, userId, approvalDate, productStatus) VALUES (?, ?, ?, ?)";

            Connection conn = null;
            try {
                conn = dbConnector.getConnection();
                conn.setAutoCommit(false); // Start transaction

                // Update Products table
                try (PreparedStatement stmt = conn.prepareStatement(productsSql)) {
                    stmt.setInt(1, product.getOrderId());
                    stmt.setString(2, product.getProductName());
                    stmt.setInt(3, product.getSize());
                    stmt.setInt(4, product.getProductId());

                    stmt.executeUpdate();
                }

                // Check if record exists in ProductApproval
                boolean recordExists = false;
                try (PreparedStatement checkStmt = conn.prepareStatement("SELECT 1 FROM ProductApproval WHERE productId = ?")) {
                    checkStmt.setInt(1, product.getProductId());
                    try (ResultSet rs = checkStmt.executeQuery()) {
                        recordExists = rs.next();
                    }
                }

                // Update or insert into ProductApproval
                if (recordExists) {
                    try (PreparedStatement stmt = conn.prepareStatement(approvalSql)) {
                        stmt.setInt(1, product.getApprovedBy());
                        stmt.setObject(2, product.getApprovalDate());
                        stmt.setString(3, product.getProductStatus());
                        stmt.setInt(4, product.getProductId());

                        stmt.executeUpdate();
                    }
                } else {
                    try (PreparedStatement stmt = conn.prepareStatement(insertApprovalSql)) {
                        stmt.setInt(1, product.getProductId());
                        stmt.setInt(2, product.getApprovedBy());
                        stmt.setObject(3, product.getApprovalDate());
                        stmt.setString(4, product.getProductStatus());

                        stmt.executeUpdate();
                    }
                }

                conn.commit(); // Commit transaction
            } catch (SQLException e) {
                if (conn != null) {
                    try {
                        conn.rollback(); // Rollback on error
                    } catch (SQLException ex) {
                        throw new RuntimeException("Failed to rollback transaction", ex);
                    }
                }
                throw new RuntimeException("Failed to update product", e);
            } finally {
                if (conn != null) {
                    try {
                        conn.setAutoCommit(true);
                        conn.close();
                    } catch (SQLException e) {
                        throw new RuntimeException("Failed to close connection", e);
                    }
                }
            }
        }, executorService);
    }

    @Override
    public CompletableFuture<List<Products>> readAllByOrderId(int orderId) {
        return CompletableFuture.supplyAsync(() -> {
            List<Products> products = new ArrayList<>();
            List<Integer> processedProductIds = new ArrayList<>();

            String sql = "SELECT p.*, ph.photoPath, ph.photoName, ph.photoStatus, ph.photoId AS photo_id, " +
                    "pc.photoComment AS photoComment, " +
                    "pa.approvalDate, pa.productStatus, pa.userId " +
                    "FROM Products p " +
                    "LEFT JOIN Photos ph ON p.productId = ph.productId " +
                    "LEFT JOIN PhotoComments pc ON ph.photoId = pc.photoId " +
                    "LEFT JOIN ProductApproval pa ON p.productId = pa.productId " +
                    "WHERE p.orderId = ?";

            try (Connection conn = dbConnector.getConnection();
                 PreparedStatement statement = conn.prepareStatement(sql)) {

                statement.setInt(1, orderId);
                ResultSet rs = statement.executeQuery();

                while (rs.next()) {
                    int productId = rs.getInt("productId");
                    String productName = rs.getString("productName");
                    int size = rs.getInt("size");

                    // Get approval date and product status
                    java.sql.Timestamp approvalDate = rs.getTimestamp("approvalDate");
                    String productStatus = rs.getString("productStatus");
                    int userId = rs.getInt("userId");

                    // Find or create the product
                    Products product = null;
                    if (!processedProductIds.contains(productId)) {
                        product = new Products(productId, orderId, productName, size);
                        product.setPhotos(new ArrayList<>());

                        // Set approval date and product status
                        if (approvalDate != null) {
                            product.setApprovalDate(approvalDate.toLocalDateTime());
                        }
                        product.setProductStatus(productStatus);
                        product.setApprovedBy(userId);

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
                        Photos photo = new Photos(photoId, photoName, photoPath, photoStatus, productId, photoComment);
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