package dk.easv.belsign.BLL.Util;

import dk.easv.belsign.DAL.UsersDAO;

import java.io.IOException;

public class LoginValidator {

    private UsersDAO usersDAO ;

    public LoginValidator() throws IOException {
        usersDAO = new UsersDAO();

    }


    public boolean validateLogin(String email, String password) {
        // Simple validation logic
        if (email == null || email.isEmpty()) {
            return false;
        }
        if (password == null || password.isEmpty()) {
            return false;
        }
        // Add more complex validation logic as needed

        return usersDAO.checkUserCredentials(email, password).join();
    }
}
