package dk.easv.belsign.BLL.Util;

import at.favre.lib.crypto.bcrypt.BCrypt;
import dk.easv.belsign.BE.Users;
import dk.easv.belsign.DAL.UsersDAO;

import java.io.IOException;

public class LoginValidator {

    private UsersDAO usersDAO ;

    public LoginValidator() throws IOException {
        usersDAO = new UsersDAO();

    }


    // In LoginValidator.java
    public boolean validateLogin(String email, String password) {
        // Basic validation checks
        if (email == null || email.isEmpty() || password == null || password.isEmpty()) {
            return false;
        }

        try {
            Users user = usersDAO.getUserByEmail(email).join();

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
        } catch (Exception e) {
            // Handle exceptions
            return false;
        }
    }
}
