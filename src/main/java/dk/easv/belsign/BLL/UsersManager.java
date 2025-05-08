package dk.easv.belsign.BLL;

import dk.easv.belsign.BE.Users;
import dk.easv.belsign.DAL.ICrudRepo;
import dk.easv.belsign.DAL.UsersDAO;

import java.io.IOException;
import java.util.List;


public class UsersManager {

    private final ICrudRepo usersDataAccess;

    public UsersManager() throws IOException {
        usersDataAccess = new UsersDAO();
    }

    public List<Users> getAllUsers() throws Exception {
        return (List<Users>) usersDataAccess.readAll().join();
    }

    public void updateUser(Users user) throws Exception {
        usersDataAccess.update(user);
    }

    public Users getUserById(int userId) throws Exception {
        return (Users) usersDataAccess.read(userId).join();
    }

}
