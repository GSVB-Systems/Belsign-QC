package dk.easv.belsign.DAL;

import dk.easv.belsign.BE.Users;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface ICrudRepo<T> {
    CompletableFuture<Void> create(T entity) throws Exception;
    CompletableFuture<T> read(int id) throws Exception;
    CompletableFuture<List<T>> readAll() throws Exception;
    CompletableFuture<Void> update(T entity) throws Exception;
    CompletableFuture<Void> delete(int id) throws Exception;
}