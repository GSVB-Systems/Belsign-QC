package dk.easv.belsign.BLL.Util;

import at.favre.lib.crypto.bcrypt.BCrypt;
import dk.easv.belsign.BE.Users;
import dk.easv.belsign.DAL.ICrudRepo;
import dk.easv.belsign.DAL.UsersDAO;
import dk.easv.belsign.DAL.DALExceptions;

import java.io.IOException;

public class LoginValidator {

    private ICrudRepo usersDAO;

    public void setUsersDAO(ICrudRepo usersDAO) {
        this.usersDAO = usersDAO;
    }

    public LoginValidator() throws IOException {
        usersDAO = new UsersDAO();
    }

    public boolean validateLogin(int userId, String password) {
        // Basic validation checks
        if (userId == 0 || password == null || password.isBlank()) {
            return false;
        }

        try {
            Users user = (Users) usersDAO.read(userId).join();

            // User not found
            if (user == null) {
                return false;
            }

            // Verify password
            boolean verified = BCrypt.verifyer().verify(
                    password.toCharArray(),
                    user.getHashedPassword()
            ).verified;

            // Set session if verified
            if (verified) {
                UserSession.setLoggedInUser(user);
            }

            return verified;

        } catch (DALExceptions e) {
            ExceptionHandler.handleDALException(e);
            return false;

        } catch (Exception e) {
            ExceptionHandler.handleUnexpectedExeption(e);
            return false;
        }
    }
}
