package dk.easv.belsign.BLL;

import dk.easv.belsign.BE.Users;
import dk.easv.belsign.DAL.ICrudRepo;
import dk.easv.belsign.DAL.UsersDAO;

import at.favre.lib.crypto.bcrypt.BCrypt;

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
            Users existingUser = getUserById(user.getUserId());
            // If no new password is provided, keep the old one
            if (user.getHashedPassword() == null || user.getHashedPassword().isEmpty()) {
                user.setHashedPassword(existingUser.getHashedPassword());
            } else {
                // New password was typed → hash it
                String hashed = hashPassword(user.getHashedPassword());
                user.setHashedPassword(hashed);
            }
            usersDataAccess.update(user);
    }

    public Users getUserById(int userId) throws Exception {
        return (Users) usersDataAccess.read(userId).join();
    }
    public void createUser(Users user) throws Exception {
        // 🔐 Hash the password using favre bcrypt
        String hashed = hashPassword(user.getHashedPassword());
        user.setHashedPassword(hashed);

        usersDataAccess.create(user);
    }

    private String hashPassword(String plainPassword) {
        return BCrypt.withDefaults().hashToString(12, plainPassword.toCharArray());
    }

    public void deleteUser(int userId) throws Exception {
        usersDataAccess.delete(userId).join();
    }
}
