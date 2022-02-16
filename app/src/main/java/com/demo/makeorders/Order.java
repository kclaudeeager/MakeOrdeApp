package com.demo.makeorders;

public class Order {
    int orderId;
    Product product;
    String retailer;
    String supplier;

    @Override
    public String toString() {
        return "Order{" +
                "orderId=" + orderId +
                ", product=" + product +
                ", retailer='" + retailer + '\'' +
                ", supplier='" + supplier + '\'' +
                '}';
    }

    public Order(int orderId, Product product, String retailer, String supplier) {
        this.orderId = orderId;
        this.product = product;
        this.retailer = retailer;
        this.supplier = supplier;
    }

    public void setRetailer(String retailer) {
        this.retailer = retailer;
    }

    public void setSupplier(String supplier) {
        this.supplier = supplier;
    }

    public String getRetailer() {
        return retailer;
    }

    public String getSupplier() {
        return supplier;
    }



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
