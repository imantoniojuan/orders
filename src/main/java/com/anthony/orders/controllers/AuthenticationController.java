package com.anthony.orders.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.anthony.orders.dtos.requests.LoginRequest;
import com.anthony.orders.dtos.requests.RegisterRequest;
import com.anthony.orders.dtos.responses.LoginResponse;
import com.anthony.orders.dtos.responses.RegisterResponse;
import com.anthony.orders.entities.Customer;
import com.anthony.orders.services.AuthenticationService;
import com.anthony.orders.services.JwtService;

@RequestMapping("/api/auth")
@RestController
public class AuthenticationController extends BaseController{
    private final JwtService jwtService;
    
    private final AuthenticationService authenticationService;

    public AuthenticationController(JwtService jwtService, AuthenticationService authenticationService) {
        this.jwtService = jwtService;
        this.authenticationService = authenticationService;
    }

    @PostMapping("/register")
    public ResponseEntity<RegisterResponse> register(@RequestBody RegisterRequest registerUserDto) {
        RegisterResponse response = new RegisterResponse();
        prepare(response);

        response = authenticationService.register(registerUserDto);

        conclude(response);
		return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> authenticate(@RequestBody LoginRequest loginUserDto) {

        LoginResponse response = new LoginResponse();
        prepare(response);

        Customer authenticatedCustomer = authenticationService.authenticate(loginUserDto);
        String jwtToken = jwtService.generateToken(authenticatedCustomer,authenticatedCustomer);
        response = new LoginResponse().setToken(jwtToken).setExpiresIn(jwtService.getExpirationTime());

        conclude(response);
		return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
