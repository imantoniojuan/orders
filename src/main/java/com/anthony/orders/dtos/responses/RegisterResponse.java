package com.anthony.orders.dtos.responses;

import java.time.LocalDateTime;

import com.anthony.orders.entities.Customer;

public class RegisterResponse extends ResponseStatus{
    private String name;
    private String email;
    private String address;
    private String password;
    private String contactNumber;
    private LocalDateTime updatedAt;

    public RegisterResponse(){
        
    }

    public RegisterResponse(Customer customer){
        this.name = customer.getName();
        this.email = customer.getEmail();
        this.address = customer.getAddress();
        this.contactNumber = customer.getContactNumber();
        this.updatedAt = customer.getUpdatedAt();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getContactNumber() {
        return contactNumber;
    }

    public void setContactNumber(String contactNumber) {
        this.contactNumber = contactNumber;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
