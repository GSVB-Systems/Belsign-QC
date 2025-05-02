package dk.easv.belsign.BLL.Util;

import dk.easv.belsign.BE.Users;

public class UserSession {

    private static UserSession instance;
    private Users user;


    private UserSession() {
        // Private constructor to prevent instantiation
    }

    public static UserSession getInstance() {
        if (instance == null) {
            instance = new UserSession();
        }
        return instance;
    }


    public static void setLoggedInUser(Users user) {
        UserSession userSession = getInstance();
        userSession.user = user;
    }

    public static Users getLoggedInUser() {
        UserSession userSession = getInstance();
        return userSession.user;
    }
}
