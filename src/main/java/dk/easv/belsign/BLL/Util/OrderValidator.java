package dk.easv.belsign.BLL.Util;

import dk.easv.belsign.BE.Orders;
import dk.easv.belsign.DAL.ICrudRepo;
import dk.easv.belsign.DAL.OrdersDAO;

import java.io.IOException;

public class OrderValidator {

    private ICrudRepo ordersDAO;


    public OrderValidator() throws IOException {
        ordersDAO = new OrdersDAO();
    }


    public boolean validateOrder(int orderId) throws Exception {
        

        // Check if the order exists in the database
        Orders order = (Orders) ordersDAO.read(orderId).join();
        if(order != null) {
            OrderSession.setEnteredOrder(order);
            return true;
        } else return false;

    }







}
