package com.anthony.orders.dtos.responses;

import com.anthony.orders.entities.Customer;

public class CustomerGetByIdResponse extends ResponseStatus{
    private Customer customer;

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }
}
