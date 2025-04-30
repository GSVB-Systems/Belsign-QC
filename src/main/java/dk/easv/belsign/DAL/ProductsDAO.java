package dk.easv.belsign.DAL;

import dk.easv.belsign.BE.Products;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ProductsDAO implements IProductsDataAccess {

    private DBConnector dbConnector;

    public ProductsDAO() throws IOException {
        this.dbConnector = DBConnector.getInstance();
    }

    @Override
    public List<Products> getAllProducts() throws SQLException {

        ArrayList<Products> products = new ArrayList<>();
        String sql = "SELECT * FROM products";

        try(Connection conn = dbConnector.getConnection();
            PreparedStatement statement = conn.prepareStatement(sql)){
            ResultSet rs = statement.executeQuery(sql);

            while (rs.next()) {
                int productId = rs.getInt("productId");
                int photoId = rs.getInt("photoId");
                int orderId = rs.getInt("orderId");
                String productName = rs.getString("productName");
                int quantity = rs.getInt("quantity");

                Products product = new Products(productId, photoId, orderId, productName, quantity);
                products.add(product);
            }
            return products;
        } catch (SQLException e) {
            throw new SQLException("Error fetching products from database", e);
        }
    }

    @Override
    public void updateProduct(Products product) throws SQLException {

        String sql = "UPDATE products SET quantity = ?, photoId = ?, productName = ? WHERE productId = ?";

        try(Connection conn = dbConnector.getConnection()){
            conn.setAutoCommit(false);

            try(PreparedStatement statement = conn.prepareStatement(sql)){
                statement.setInt(1, product.getQuantity());
                statement.setInt(2, product.getPhotoId());
                statement.setString(3, product.getProductName());
                statement.setInt(4, product.getProductId());

                statement.executeUpdate();
            } catch (SQLException e) {
                conn.rollback();
                throw new SQLException("Error updating product in database", e);
            }
            conn.commit();
        }
    }
}
