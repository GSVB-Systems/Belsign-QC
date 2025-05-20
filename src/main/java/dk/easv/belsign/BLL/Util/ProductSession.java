package dk.easv.belsign.BLL.Util;

import dk.easv.belsign.BE.Products;

public class ProductSession {

    private static ProductSession instance;
    private Products product;

    public static ProductSession getInstance() {
        if (instance == null) {
            instance = new ProductSession();
        }
        return instance;
    }

    public static void setEnteredProduct(Products product) {
        ProductSession productSession = getInstance();
        productSession.product = product;
    }

    public static Products getEnteredProduct() {
        ProductSession productSession = getInstance();
        return productSession.product;
    }
}
