package dk.easv.belsign.DAL;

import dk.easv.belsign.BE.Photos;
import dk.easv.belsign.BLL.Util.ThreadShutdownUtil;


import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class PhotoDAO implements IPhotoDAO<Photos> {
    private final DBConnector dbConnector;
    private ExecutorService executorService;
    private final ThreadShutdownUtil threadShutdownUtil;

    public PhotoDAO() throws IOException {
        this.dbConnector = DBConnector.getInstance();
        this.threadShutdownUtil = ThreadShutdownUtil.getInstance();
        this.executorService = Executors.newFixedThreadPool(4);
        threadShutdownUtil.registerExecutorService(executorService);

    }

    @Override
    public CompletableFuture<Void> create(Photos photo) {
        return CompletableFuture.runAsync(() -> {
            String sql = "INSERT INTO Photos (photoPath, photoName, photoStatus, productId, photoComments) VALUES (?, ?, ?, ?, ?)";
            try (Connection conn = dbConnector.getConnection();
                    PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, photo.getPhotoPath());
                stmt.setString(2, photo.getPhotoName());
                stmt.setString(3, photo.getPhotoStatus());
                stmt.setInt(4, photo.getProductId());
                stmt.setString(5, photo.getPhotoComments());
                stmt.executeUpdate();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        });
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
                throw new RuntimeException(e);
            }
            return null;
        });
    }

    /*
    @Override
    public CompletableFuture<List<Photos>> readAll() {
        return CompletableFuture.supplyAsync(() -> {
            List<Photos> photos = new ArrayList<>();
            String sql = "SELECT * FROM Photos";
            try (Connection conn = dbConnector.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(sql)) {
                ResultSet rs = stmt.executeQuery(sql);
                while (rs.next()) {
                    photos.add(mapResultSetToPhoto(rs));
                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
            return photos;
        });
    }
     */


    @Override
    public CompletableFuture<List<Photos>> readAll() {

        return CompletableFuture.supplyAsync(() -> {
            ArrayList<Photos> photos = new ArrayList<>();
            String sql = "SELECT * FROM Photos";

            try (Connection conn = dbConnector.getConnection();
                 PreparedStatement statement = conn.prepareStatement(sql)) {
                ResultSet rs = statement.executeQuery(sql);

                while (rs.next()) {
                    int photoId = rs.getInt("photoId");
                    String photoName = rs.getString("photoName");
                    String photoPath = rs.getString("photoPath");
                    String photoStatus = rs.getString("photoStatus");
                    int productId = rs.getInt("productId");
                    String photoComments = rs.getString("photoComments");

                    Photos photo = new Photos(photoId, photoName, photoPath, photoStatus, productId, photoComments);
                    photos.add(photo);
                }
                return photos;
            } catch (SQLException e) {
                throw new RuntimeException("Error fetching photos from database", e);
            }
        }, executorService);



    }

    @Override
    public CompletableFuture<Void> update(Photos photo) {
        return CompletableFuture.runAsync(() -> {
            String sql = "UPDATE Photos SET photoPath = ?, photoName = ?, photoStatus = ?, productId = ?, photoComments = ? WHERE photoId = ?";
            Connection conn = null;
            try {
                conn = dbConnector.getConnection();
                conn.setAutoCommit(false);

                try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                    stmt.setString(1, photo.getPhotoPath());
                    stmt.setString(2, photo.getPhotoName());
                    stmt.setString(3, photo.getPhotoStatus());
                    stmt.setInt(4, photo.getProductId());
                    stmt.setString(5, photo.getPhotoComments());
                    stmt.setInt(6, photo.getPhotoId());

                    stmt.executeUpdate();
                    conn.commit();
                }
            } catch (SQLException e) {
                if (conn != null) {
                    try {
                        conn.rollback();
                    } catch (SQLException ex) {
                        throw new RuntimeException("Failed to rollback transaction", ex);
                    }
                }
                throw new RuntimeException("Failed to update photo", e);
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
    public CompletableFuture<Void> delete(int id) {
        return CompletableFuture.runAsync(() -> {
            String sql = "DELETE FROM Photos WHERE photoId = ?";
            try (Connection conn = dbConnector.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setInt(1, id);
                stmt.executeUpdate();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        });
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
                    conn.commit();
                }
            } catch (SQLException e) {
                if (conn != null) {
                    try {
                        conn.rollback();
                    } catch (SQLException ex) {
                        throw new RuntimeException("Failed to rollback transaction", ex);
                    }
                }
                throw new RuntimeException("Failed to update photo", e);
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

    public void updatePhotoComment(int photoId, String comment) throws SQLException {
        String sql =
                "UPDATE Photos SET photoComments = ? WHERE photoId = ?;";


        try (Connection conn = dbConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, photoId);
            stmt.setString(2, comment);
            stmt.executeUpdate();
        }
    }

    public CompletableFuture<List<Photos>> getPhotosByProductId(int productId) {
        return CompletableFuture.supplyAsync(() -> {
            List<Photos> photosList = new ArrayList<>();

            String sql = "SELECT * FROM photos WHERE productId = ?";

            try (Connection conn = dbConnector.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(sql)) {

                stmt.setInt(1, productId);
                try (ResultSet rs = stmt.executeQuery()) {
                    while (rs.next()) {
                        int photoId = rs.getInt("photoId");
                        String photoName = rs.getString("photoName");
                        String photoPath = rs.getString("photoPath");
                        String photoStatus = rs.getString("photoStatus");
                        String photoComments = rs.getString("photoComments");

                        Photos photo = new Photos(photoId, photoName, photoPath, photoStatus, productId, photoComments);
                        photosList.add(photo);
                    }
                }

            } catch (SQLException e) {
                throw new RuntimeException("Error fetching photos for product ID: " + productId, e);
            }

            return photosList;
        });
    }
}
