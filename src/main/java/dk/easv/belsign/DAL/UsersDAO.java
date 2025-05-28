package dk.easv.belsign.DAL;

import dk.easv.belsign.BE.Users;
import dk.easv.belsign.BLL.Util.ExceptionHandler;
import dk.easv.belsign.BLL.Util.ThreadShutdownUtil;

import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

public class UsersDAO implements ICrudRepo<Users> {

    private final DBConnector dbConnector;
    private final ExecutorService executorService;
    private final ThreadShutdownUtil threadShutdownUtil;

    public UsersDAO() throws IOException {
        this.dbConnector = DBConnector.getInstance();
        this.threadShutdownUtil = ThreadShutdownUtil.getInstance();
        this.executorService = Executors.newFixedThreadPool(4);
        threadShutdownUtil.registerExecutorService(executorService);
    }

    @Override
    public CompletableFuture<Void> create(Users user) {
        return CompletableFuture.runAsync(() -> {
            String sql = "INSERT INTO Users (roleId, firstName, lastName, email, hashedPassword) VALUES (?, ?, ?, ?, ?)";

            try (Connection conn = dbConnector.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);) {

                stmt.setInt(1, user.getRoleId());
                stmt.setString(2, user.getFirstName());
                stmt.setString(3, user.getLastName());
                stmt.setString(4, user.getEmail());
                stmt.setString(5, user.getHashedPassword());

                stmt.executeUpdate();

            } catch (SQLException e) {
                DALExceptions ex = new DALExceptions("Failed to insert User into DB: ", e);
                ExceptionHandler.handleDALException(ex);
                throw ex;
            }
        }, executorService);
    }

    @Override
    public CompletableFuture<List<Users>> readAll() {
        return CompletableFuture.supplyAsync(() -> {
            List<Users> users = new ArrayList<>();
            String sql = "SELECT * FROM Users";

            try (Connection conn = dbConnector.getConnection();
                 PreparedStatement statement = conn.prepareStatement(sql);
                 ResultSet rs = statement.executeQuery()) {

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
                DALExceptions ex = new DALExceptions("Failed to read all Users: ", e);
                ExceptionHandler.handleDALException(ex);
                throw ex;
            }
        }, executorService);
    }

    @Override
    public CompletableFuture<Void> update(Users user) {
        return CompletableFuture.runAsync(() -> {
            String sql = "UPDATE Users SET roleId = ?, firstName = ?, lastName = ?, hashedPassword = ?, email = ? WHERE userId = ?";

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
                    DALExceptions ex = new DALExceptions("Failed to update DB: ", e);
                    ExceptionHandler.handleDALException(ex);
                    throw ex;
                }

            } catch (SQLException e) {
                DALExceptions ex = new DALExceptions("USER - Connection issue: ", e);
                ExceptionHandler.handleDALException(ex);
                throw ex;
            }
        }, executorService);
    }

    @Override
    public CompletableFuture<Users> read(int userId) {
        return CompletableFuture.supplyAsync(() -> {
            String sql = "SELECT * FROM Users WHERE userId = ?";

            try (Connection conn = dbConnector.getConnection();
                 PreparedStatement statement = conn.prepareStatement(sql)) {

                statement.setInt(1, userId);
                ResultSet rs = statement.executeQuery();

                if (rs.next()) {
                    int roleId = rs.getInt("roleId");
                    String firstName = rs.getString("firstName");
                    String lastName = rs.getString("lastName");
                    String storedPassword = rs.getString("hashedPassword");
                    String email = rs.getString("email");

                    return new Users(userId, roleId, firstName, lastName, email, storedPassword);
                }

                return null;

            } catch (SQLException e) {
                DALExceptions ex = new DALExceptions("Failed to get User ", e);
                ExceptionHandler.handleDALException(ex);
                throw ex;
            }
        }, executorService);
    }

    @Override
    public CompletableFuture<Void> delete(int id) {
        return CompletableFuture.runAsync(() -> {
            String sql = "DELETE FROM Users WHERE userId = ?";

            try (Connection conn = dbConnector.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(sql)) {

                stmt.setInt(1, id);
                int affected = stmt.executeUpdate();

                if (affected == 0) {
                    DALExceptions ex = new DALExceptions("No user matching with ID: " + id);
                    ExceptionHandler.handleDALException(ex);
                    throw ex;
                }

            } catch (SQLException e) {
                DALExceptions ex = new DALExceptions("Failed to delete user from DB: ", e);
                ExceptionHandler.handleDALException(ex);
                throw ex;
            }
        }, executorService);
    }

    public void shutdown() {
        executorService.shutdown();
    }
}
