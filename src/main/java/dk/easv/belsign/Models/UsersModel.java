package dk.easv.belsign.Models;

import dk.easv.belsign.BE.Users;
import dk.easv.belsign.BLL.UsersManager;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

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

    public List<Users> getAllUsersFromDB() throws Exception {
        return usersManager.getAllUsers();
    }

    public void updateUser(Users user) throws Exception {
        usersManager.updateUser(user);
    }

    public Users getUserById(int userId) throws Exception {
        return usersManager.getUserById(userId);
    }
    public void createUser(Users user) throws Exception {
        usersManager.createUser(user);
    }
    public void deleteUser(int userId) throws Exception {
        usersManager.deleteUser(userId);
    }
}