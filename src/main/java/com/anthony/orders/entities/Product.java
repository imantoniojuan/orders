package com.anthony.orders.entities;

import javax.persistence.*;

@Entity
@Table(name = "product")
public class Product extends BaseEntity {
    @Column(unique=true)
    private String upc;
    private String name;
    private String description;

    public Product(){

    }

    public String getUpc() {
        return upc;
    }

    public void setUpc(String upc) {
        this.upc = upc;
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
}
