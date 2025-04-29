package dk.easv.belsign.DAL;

import dk.easv.belsign.BE.Orders;

import java.sql.SQLException;
import java.util.List;

public interface IOrdersDataAccess {

    List<Orders> getAllOrders() throws SQLException;
}
