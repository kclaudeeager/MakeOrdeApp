package com.demo.makeorders;

public class Order {
    int orderId;
    Product product;
public Order(){

}
    public void setOrderId(int orderId) {
        this.orderId = orderId;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public Order(int orderId) {
        this.orderId = orderId;
    }

    public int getOrderId() {
        return orderId;
    }

    public Product getProduct() {
        return product;
    }

    public Order(int orderId, Product product) {
        this.orderId = orderId;
        this.product = product;
    }

}
