package dk.easv.belsign.BLL;

import dk.easv.belsign.BE.Users;
import dk.easv.belsign.DAL.IUsersDataAccess;
import dk.easv.belsign.DAL.UsersDAO;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

public class UsersManager {

    private final IUsersDataAccess usersDataAccess;

    public UsersManager() throws IOException {
        usersDataAccess = new UsersDAO();
    }

    public List<Users> getAllUsers() throws SQLException {
        return usersDataAccess.getAllUsers();
    }

    public void updateUser(Users user) throws SQLException {
        usersDataAccess.updateUser(user);
    }

}
