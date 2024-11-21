package com.anthony.orders.dtos.requests;

public class ProductPostRequest {
    private String upc;
    private String name;
    private String description;

    public ProductPostRequest(){

    }

    
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }


    public String getUpc() {
        return upc;
    }


    public void setUpc(String upc) {
        this.upc = upc;
    }
}
