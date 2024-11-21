package com.anthony.orders.services;

import java.util.Optional;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.anthony.orders.dtos.requests.LoginRequest;
import com.anthony.orders.dtos.requests.RegisterRequest;
import com.anthony.orders.dtos.responses.RegisterResponse;
import com.anthony.orders.entities.Customer;
import com.anthony.orders.repositories.CustomerRepository;

@Service
public class AuthenticationService {
    private final CustomerRepository customerRepository;
    
    private final PasswordEncoder passwordEncoder;
    
    private final AuthenticationManager authenticationManager;

    public AuthenticationService(
        CustomerRepository customerRepository,
        AuthenticationManager authenticationManager,
        PasswordEncoder passwordEncoder
    ) {
        this.authenticationManager = authenticationManager;
        this.customerRepository = customerRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public RegisterResponse register(RegisterRequest input) {
        Optional<Customer> customerOpt = customerRepository.findByEmail(input.getEmail());
        if(!customerOpt.isPresent() || customerOpt.isEmpty()){
            Customer customer = new Customer();
            customer.setEmail(input.getEmail());
            customer.setPassword(passwordEncoder.encode(input.getPassword()));
            customer.setContactNumber(input.getContactNumber());
            customer.setName(input.getName());
            customer.setAddress(input.getAddress());
            return new RegisterResponse(customerRepository.save(customer));
        }
        else
            return new RegisterResponse(customerOpt.get());
    }

    public Customer authenticate(LoginRequest input) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        input.getEmail(),
                        input.getPassword()
                )
        );

        return customerRepository.findByEmail(input.getEmail()).orElseThrow();
    }
}
