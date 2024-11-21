package com.anthony.orders.dtos.responses;

import com.anthony.orders.entities.Product;

public class ProductPostResponse extends ResponseStatus{
    private Product product;

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }
}
