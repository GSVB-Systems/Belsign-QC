package dk.easv.belsign.BLL.Util;

import dk.easv.belsign.BE.Orders;
import dk.easv.belsign.BE.Users;

public class OrderSession {

    private static OrderSession instance;
    private Orders order;

    public static OrderSession getInstance() {
        if (instance == null) {
            instance = new OrderSession();
        }
        return instance;
    }


    public static void setEnteredOrder(Orders order) {
        OrderSession orderSession = getInstance();
        orderSession.order = order;
    }

    public static Orders getEnteredOrder() {
        OrderSession orderSession = getInstance();
        return orderSession.order;
    }
}
