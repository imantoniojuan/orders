package com.anthony.orders.dtos.responses;

import com.anthony.orders.entities.Customer;

public class RegisterResponse extends ResponseStatus{
    private Customer customer;

    public RegisterResponse(){
        
    }

    public RegisterResponse(Customer customer){
        this.customer = customer;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }
}
