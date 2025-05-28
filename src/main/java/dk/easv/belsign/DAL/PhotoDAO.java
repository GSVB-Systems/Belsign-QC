package dk.easv.belsign.DAL;

import dk.easv.belsign.BE.Photos;
import dk.easv.belsign.BLL.Util.ExceptionHandler;
import dk.easv.belsign.BLL.Util.ThreadShutdownUtil;

import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

public class PhotoDAO implements IPhotoDAO<Photos> {
    private final DBConnector dbConnector;
    private final ExecutorService executorService;
    private final ThreadShutdownUtil threadShutdownUtil;

    public PhotoDAO() throws IOException {
        this.dbConnector = DBConnector.getInstance();
        this.executorService = Executors.newFixedThreadPool(4);
        this.threadShutdownUtil = ThreadShutdownUtil.getInstance();
        threadShutdownUtil.registerExecutorService(executorService);
    }

    @Override
    public CompletableFuture<Void> create(Photos photo) {
        return CompletableFuture.runAsync(() -> {
            String sql = "INSERT INTO Photos (photoPath, photoName, photoStatus, productId) VALUES (?, ?, ?, ?)";
            try (Connection conn = dbConnector.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, photo.getPhotoPath());
                stmt.setString(2, photo.getPhotoName());
                stmt.setString(3, photo.getPhotoStatus());
                stmt.setInt(4, photo.getProductId());

                stmt.executeUpdate();
            } catch (SQLException e) {
                ExceptionHandler.handleDALException(new DALExceptions("Failed to create photo", e));
            }
        }, executorService);
    }

    @Override
    public CompletableFuture<Photos> read(int id) {
        return CompletableFuture.supplyAsync(() -> {
            String sql = "SELECT * FROM Photos WHERE photoId = ?";
            try (Connection conn = dbConnector.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setInt(1, id);
                ResultSet rs = stmt.executeQuery();
                if (rs.next()) {
                    return mapResultSetToPhoto(rs);
                }
            } catch (SQLException e) {
                ExceptionHandler.handleDALException(new DALExceptions("Failed to read photo with ID: " + id, e));
            }
            return null;
        }, executorService);
    }

    @Override
    public CompletableFuture<List<Photos>> readAll() {
        return CompletableFuture.supplyAsync(() -> {
            List<Photos> photos = new ArrayList<>();
            String sql = "SELECT * FROM Photos";

            try (Connection conn = dbConnector.getConnection();
                 PreparedStatement statement = conn.prepareStatement(sql)) {
                ResultSet rs = statement.executeQuery();

                while (rs.next()) {
                    photos.add(mapResultSetToPhoto(rs));
                }
            } catch (SQLException e) {
                ExceptionHandler.handleDALException(new DALExceptions("Failed to fetch all photos", e));
            }

            return photos;
        }, executorService);
    }

    @Override
    public CompletableFuture<Void> update(Photos photo) {
        return CompletableFuture.runAsync(() -> {
            Connection conn = null;
            try {
                conn = dbConnector.getConnection();
                conn.setAutoCommit(false);

                String updateSql = "UPDATE Photos SET photoPath = ?, photoName = ?, photoStatus = ?, productId = ? WHERE photoId = ?";
                int rowsAffected;
                try (PreparedStatement updateStmt = conn.prepareStatement(updateSql)) {
                    updateStmt.setString(1, photo.getPhotoPath());
                    updateStmt.setString(2, photo.getPhotoName());
                    updateStmt.setString(3, photo.getPhotoStatus());
                    updateStmt.setInt(4, photo.getProductId());
                    updateStmt.setInt(5, photo.getPhotoId());
                    rowsAffected = updateStmt.executeUpdate();
                }

                if (rowsAffected == 0) {
                    String insertSql = "INSERT INTO Photos (photoPath, photoName, photoStatus, productId) VALUES (?, ?, ?, ?)";
                    try (PreparedStatement insertStmt = conn.prepareStatement(insertSql, Statement.RETURN_GENERATED_KEYS)) {
                        insertStmt.setString(1, photo.getPhotoPath());
                        insertStmt.setString(2, photo.getPhotoName());
                        insertStmt.setString(3, photo.getPhotoStatus());
                        insertStmt.setInt(4, photo.getProductId());
                        insertStmt.executeUpdate();

                        try (ResultSet keys = insertStmt.getGeneratedKeys()) {
                            if (keys.next()) {
                                photo.setPhotoId(keys.getInt(1));
                            }
                        }
                    }
                }

                conn.commit();
            } catch (SQLException e) {
                rollbackQuietly(conn);
                ExceptionHandler.handleDALException(new DALExceptions("Failed to update or insert photo", e));
            } finally {
                closeQuietly(conn);
            }
        }, executorService);
    }

    @Override
    public CompletableFuture<Void> delete(int id) {
        return CompletableFuture.runAsync(() -> {
            String sql = "DELETE FROM Photos WHERE photoId = ?";
            try (Connection conn = dbConnector.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setInt(1, id);
                stmt.executeUpdate();
            } catch (SQLException e) {
                ExceptionHandler.handleDALException(new DALExceptions("Failed to delete photo with ID: " + id, e));
            }
        }, executorService);
    }

    @Override
    public void updatePhotoComment(Photos photo) {
        Connection conn = null;
        try {
            conn = dbConnector.getConnection();
            conn.setAutoCommit(false);

            String updateSql = "UPDATE PhotoComments SET photoComment = ? WHERE photoId = ?";
            int rowsAffected;
            try (PreparedStatement updateStmt = conn.prepareStatement(updateSql)) {
                updateStmt.setString(1, photo.getPhotoComments());
                updateStmt.setInt(2, photo.getPhotoId());
                rowsAffected = updateStmt.executeUpdate();
            }

            if (rowsAffected == 0) {
                String insertSql = "INSERT INTO PhotoComments (photoId, photoComment) VALUES (?, ?)";
                try (PreparedStatement insertStmt = conn.prepareStatement(insertSql)) {
                    insertStmt.setInt(1, photo.getPhotoId());
                    insertStmt.setString(2, photo.getPhotoComments());
                    insertStmt.executeUpdate();
                }
            }

            conn.commit();
        } catch (SQLException e) {
            rollbackQuietly(conn);
            ExceptionHandler.handleDALException(new DALExceptions("Failed to update photo comment", e));
        } finally {
            closeQuietly(conn);
        }
    }

    public CompletableFuture<Void> updatePhoto(Photos photo) {
        return CompletableFuture.runAsync(() -> {
            String sql = "UPDATE Photos SET photoName = ?, photoPath = ?, photoStatus = ? WHERE productId = ? AND photoId = ?";
            Connection conn = null;
            try {
                conn = dbConnector.getConnection();
                conn.setAutoCommit(false);

                try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                    stmt.setString(1, photo.getPhotoName());
                    stmt.setString(2, photo.getPhotoPath());
                    stmt.setString(3, photo.getPhotoStatus());
                    stmt.setInt(4, photo.getProductId());
                    stmt.setInt(5, photo.getPhotoId());
                    stmt.executeUpdate();
                }

                conn.commit();
            } catch (SQLException e) {
                rollbackQuietly(conn);
                ExceptionHandler.handleDALException(new DALExceptions("Failed to update photo", e));
            } finally {
                closeQuietly(conn);
            }
        }, executorService);
    }

    public CompletableFuture<List<Photos>> getPhotosByProductId(int productId) {
        return CompletableFuture.supplyAsync(() -> {
            List<Photos> photosList = new ArrayList<>();
            String sql = "SELECT * FROM Photos WHERE productId = ?";

            try (Connection conn = dbConnector.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setInt(1, productId);
                try (ResultSet rs = stmt.executeQuery()) {
                    while (rs.next()) {
                        photosList.add(mapResultSetToPhoto(rs));
                    }
                }
            } catch (SQLException e) {
                ExceptionHandler.handleDALException(new DALExceptions("Failed to fetch photos for product ID: " + productId, e));
            }

            return photosList;
        }, executorService);
    }

    private Photos mapResultSetToPhoto(ResultSet rs) throws SQLException {
        return new Photos(
                rs.getInt("photoId"),
                rs.getString("photoName"),
                rs.getString("photoPath"),
                rs.getString("photoStatus"),
                rs.getInt("productId"),
                rs.getString("photoComments")
        );
    }

    private void rollbackQuietly(Connection conn) {
        if (conn != null) {
            try {
                conn.rollback();
            } catch (SQLException ignored) {
            }
        }
    }

    private void closeQuietly(Connection conn) {
        if (conn != null) {
            try {
                conn.setAutoCommit(true);
                conn.close();
            } catch (SQLException ignored) {
            }
        }
    }
}
