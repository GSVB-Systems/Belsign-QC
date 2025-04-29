package dk.easv.belsign.DAL;

import dk.easv.belsign.BE.Products;

import java.sql.SQLException;
import java.util.List;

public interface IProductsDataAccess {


    // Method to get all products
    List<Products> getAllProducts() throws SQLException;


}
