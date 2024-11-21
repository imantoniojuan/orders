package com.anthony.orders.dtos.requests;

import com.anthony.orders.entities.Customer;

public class OrderPostRequest {
    private Customer customer;
    private String status;
    private Double totalAmount;

    public OrderPostRequest(){

    }
    
    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Double getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(Double totalAmount) {
        this.totalAmount = totalAmount;
    }
}
