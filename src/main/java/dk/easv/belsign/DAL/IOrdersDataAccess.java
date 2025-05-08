package dk.easv.belsign.DAL;

import dk.easv.belsign.BE.Orders;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface IOrdersDataAccess {
    CompletableFuture<List<Orders>> getAllOrders();
    CompletableFuture<Void> updateOrder(Orders order);
}