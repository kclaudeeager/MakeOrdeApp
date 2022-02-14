package com.demo.makeorders;

public class Product {
    String pName;
    double price;

    @Override
    public String toString() {
        return "Product{" +
                "pName='" + pName + '\'' +
                ", price=" + price +
                ", numReq=" + numReq +
                '}';
    }
    int numReq;
    public Product(String pName, double price){
    }
    public String getpName() {
        return pName;
    }

    public double getPrice() {
        return price;
    }

    public int getNumReq() {
        return numReq;
    }
    public Product(){

    }
    public Product(String pName, double price, int numReq) {
        this.pName = pName;
        this.price = price;
        this.numReq = numReq;
    }

    public void setpName(String pName) {
        this.pName = pName;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public void setNumReq(int numReq) {
        this.numReq = numReq;
    }
}
