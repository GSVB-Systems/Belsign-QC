package dk.easv.belsign.DAL;

import dk.easv.belsign.BE.Users;

import java.sql.SQLException;
import java.util.List;

import java.util.concurrent.CompletableFuture;

public interface IUsersDataAccess {

    CompletableFuture<List<Users>> getAllUsers() throws SQLException;

    CompletableFuture<Void> updateUser(Users user) throws SQLException;

    CompletableFuture<Users> getUserByEmail(String email) throws SQLException;

}
