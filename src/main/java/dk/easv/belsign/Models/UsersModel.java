package dk.easv.belsign.Models;

import dk.easv.belsign.BE.Users;
import dk.easv.belsign.BLL.UsersManager;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.io.IOException;
import java.sql.SQLException;

public class UsersModel {

    private final ObservableList<Users> observableUsers;
    private final UsersManager usersManager;

    public UsersModel() throws Exception {
        this.usersManager = new UsersManager();
        observableUsers   = FXCollections.observableArrayList();
        observableUsers.addAll(usersManager.getAllUsers());
    }


    public ObservableList<Users> getObservableUsers() {
        return observableUsers;
    }

    public void updateUser(Users user) throws Exception {
        usersManager.updateUser(user);
    }

    public Users getUserByd(int userId) throws Exception {
        return usersManager.getUserById(userId);
    }

}
