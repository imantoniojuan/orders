package com.anthony.orders.dtos.requests;

public class LoginRequest {
    private String email;
    private String password;

    public String getEmail() {
        return email;
    }

    public LoginRequest setEmail(String email) {
        this.email = email;
        return this;
    }

    public String getPassword() {
        return password;
    }

    public LoginRequest setPassword(String password) {
        this.password = password;
        return this;
    }

    @Override
    public String toString() {
        return "LoginDto [email=" + email + ", password=" + password + "]";
    }
}