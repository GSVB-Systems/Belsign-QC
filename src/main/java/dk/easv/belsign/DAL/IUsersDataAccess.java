package dk.easv.belsign.DAL;

import dk.easv.belsign.BE.Users;

import java.sql.SQLException;
import java.util.List;

public interface IUsersDataAccess {

    List<Users> getAllUsers() throws SQLException;

}
