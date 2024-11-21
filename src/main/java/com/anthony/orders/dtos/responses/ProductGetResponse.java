package com.anthony.orders.dtos.responses;

import java.util.List;

import com.anthony.orders.entities.Product;

public class ProductGetResponse extends ResponseStatus{
    private List<Product> products;
    private Pagination pagination;
    
    public List<Product> getProducts() {
        return products;
    }
    public void setProducts(List<Product> products) {
        this.products = products;
    }
    public Pagination getPagination() {
        return pagination;
    }
    public void setPagination(Pagination pagination) {
        this.pagination = pagination;
    }
   
}
