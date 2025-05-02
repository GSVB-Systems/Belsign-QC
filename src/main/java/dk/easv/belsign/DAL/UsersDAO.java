package dk.easv.belsign.DAL;

import at.favre.lib.crypto.bcrypt.BCrypt;
import dk.easv.belsign.BE.Users;
import dk.easv.belsign.BLL.Util.UserSession;

import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

public class UsersDAO implements IUsersDataAccess {

    private final DBConnector dbConnector;
    private final ExecutorService executorService = Executors.newCachedThreadPool();

    public UsersDAO() throws IOException {
        this.dbConnector = DBConnector.getInstance();
    }

    public UsersDAO(DBConnector dbConnector) {
        this.dbConnector = dbConnector;
    }

    @Override
    public CompletableFuture<List<Users>> getAllUsers() {
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
    public CompletableFuture<Void> updateUser(Users user) {
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
    public CompletableFuture<Boolean> checkUserCredentials(String email, String hashedPassword) {
        return CompletableFuture.supplyAsync(() -> {
            String sql = "SELECT * FROM users WHERE email = ?";
            try (Connection conn = dbConnector.getConnection();
                 PreparedStatement statement = conn.prepareStatement(sql)) {
                statement.setString(1, email);
                ResultSet rs = statement.executeQuery();

                if (rs.next()) {
                    String storedPassword = rs.getString("hashedPassword");
                    if (BCrypt.verifyer().verify(hashedPassword.toCharArray(), storedPassword).verified) {
                        int userId = rs.getInt("userId");
                        int roleId = rs.getInt("roleId");
                        String firstName = rs.getString("firstName");
                        String lastName = rs.getString("lastName");

                        Users user = new Users(userId, roleId, firstName, lastName, email, hashedPassword);
                        UserSession.setLoggedInUser(user);
                        return true;
                    }
                }
            } catch (SQLException e) {
                throw new RuntimeException("Error checking user credentials", e);

            }
            return false;
        }, executorService);
    }

    public void shutdown() {
        executorService.shutdown();
    }

}
