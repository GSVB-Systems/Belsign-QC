package dk.easv.belsign.DAL;

import dk.easv.belsign.BE.Users;

import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UsersDAO implements IUsersDataAccess {

    private DBConnector dbConnector = new DBConnector();

    public UsersDAO() throws IOException {
        this.dbConnector = new DBConnector();
    }

    @Override
    public List<Users> getAllUsers() throws SQLException {

        ArrayList<Users> users = new ArrayList<>();
        String sql = "SELECT * FROM users";

        try(Connection conn = dbConnector.getConnection();
            PreparedStatement statement = conn.prepareStatement(sql)){
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
            throw new SQLException("Error fetching users from database", e);
        }
    }

    @Override
    public void updateUser(Users user) throws SQLException {
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
            } catch (SQLException e) {
                conn.rollback();
                throw new SQLException("Error updating user in database", e);
            }
            conn.commit();
        }
    }
}
