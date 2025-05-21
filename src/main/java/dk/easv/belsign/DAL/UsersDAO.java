package dk.easv.belsign.DAL;

import dk.easv.belsign.BE.Users;
import dk.easv.belsign.BLL.Util.ThreadShutdownUtil;

import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

public class UsersDAO implements ICrudRepo<Users> {

    private final DBConnector dbConnector;
    private ExecutorService executorService;
    private final ThreadShutdownUtil threadShutdownUtil;

    public UsersDAO() throws IOException {
        this.dbConnector = DBConnector.getInstance();
        this.threadShutdownUtil = ThreadShutdownUtil.getInstance();
        this.executorService = Executors.newFixedThreadPool(4);
        threadShutdownUtil.registerExecutorService(executorService);
    }


    @Override
    public CompletableFuture<Void> create(Users user) throws Exception {
        return CompletableFuture.runAsync(() -> {
            String sql = "INSERT INTO users (userId, roleId, firstName, lastName, email, hashedPassword) VALUES (?, ?, ?)";

            try (Connection conn = dbConnector.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(sql)) {

                stmt.setInt(1, user.getUserId());
                stmt.setInt(2, user.getRoleId());
                stmt.setString(3, user.getEmail());
                stmt.setString(4, user.getFirstName());
                stmt.setString(5, user.getLastName());
                stmt.setString(6, user.getEmail());
                stmt.setString(7, user.getHashedPassword());

                stmt.executeUpdate();

            } catch (SQLException e) {
                throw new RuntimeException("Error inserting user into database", e);
            }
        });
    }

    @Override
    public CompletableFuture<List<Users>> readAll() {
        return CompletableFuture.supplyAsync(() -> {
            ArrayList<Users> users = new ArrayList<>();
            String sql = "SELECT * FROM users";

            try (Connection conn = dbConnector.getConnection();
                 PreparedStatement statement = conn.prepareStatement(sql)) {
                ResultSet rs = statement.executeQuery(sql);

                while (rs.next()) {
                    int userId = rs.getInt("userId");
                    int roleId = rs.getInt("roleId");
                    String firstName = rs.getString("firstName");
                    String lastName = rs.getString("lastName");
                    String hashedPassword = rs.getString("hashedPassword");
                    String email = rs.getString("email");

                    Users user = new Users(userId, roleId, firstName, lastName, email, hashedPassword);
                    users.add(user);
                }
                return users;
            } catch (SQLException e) {
                throw new RuntimeException("Error fetching users from database", e);
            }
        }, executorService);
    }

    @Override
    public CompletableFuture<Void> update(Users user) {
        return CompletableFuture.runAsync(() -> {
            String sql = "UPDATE users SET roleId = ?, firstName = ?, lastName = ?, hashedPassword = ?, email = ? WHERE userId = ?";
            try (Connection conn = dbConnector.getConnection()) {
                conn.setAutoCommit(false);

                try (PreparedStatement statement = conn.prepareStatement(sql)) {
                    statement.setInt(1, user.getRoleId());
                    statement.setString(2, user.getFirstName());
                    statement.setString(3, user.getLastName());
                    statement.setString(4, user.getHashedPassword());
                    statement.setString(5, user.getEmail());
                    statement.setInt(6, user.getUserId());

                    statement.executeUpdate();
                    conn.commit();
                } catch (SQLException e) {
                    conn.rollback();
                    throw new RuntimeException("Error updating user in database", e);
                }
            } catch (SQLException e) {
                throw new RuntimeException("Database connection error", e);
            }
        }, executorService);
    }

    @Override
    public CompletableFuture<Void> delete(int id) throws Exception {
        return null;
    }

    // In UsersDAO.java
    @Override
    public CompletableFuture<Users> read(int UserId) {
        return CompletableFuture.supplyAsync(() -> {
            String sql = "SELECT * FROM users WHERE UserId = ?";
            try (Connection conn = dbConnector.getConnection();
                 PreparedStatement statement = conn.prepareStatement(sql)) {
                statement.setInt(1, UserId);
                ResultSet rs = statement.executeQuery();

                if (rs.next()) {
                    int userId = rs.getInt("userId");
                    int roleId = rs.getInt("roleId");
                    String firstName = rs.getString("firstName");
                    String lastName = rs.getString("lastName");
                    String storedPassword = rs.getString("hashedPassword");
                    String email = rs.getString("email");

                    return new Users(userId, roleId, firstName, lastName, email, storedPassword);
                }
                return null;
            } catch (SQLException e) {
                throw new RuntimeException("Error retrieving user", e);
            }
        }, executorService);
    }

    public void shutdown() {
        executorService.shutdown();
    }
}
