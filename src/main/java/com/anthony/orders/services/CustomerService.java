package com.anthony.orders.services;

import org.springframework.stereotype.Service;

import com.anthony.orders.dtos.requests.CustomerPutRequest;
import com.anthony.orders.entities.Customer;
import com.anthony.orders.repositories.CustomerRepository;

import java.util.Optional;

@Service
public class CustomerService {

    private final CustomerRepository customerRepository;

    public CustomerService(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }
  
    public Customer modify(CustomerPutRequest customerRequest){
        Customer customer = new Customer();

        Optional<Customer> customerOpt = customerRepository.findById(customerRequest.getId());
        if(customerOpt!=null && customerOpt.isPresent()){
            customer = customerOpt.get();
            customer.setAddress(customerRequest.getAddress() != null ? customerRequest.getAddress() : customer.getAddress());
            customer.setContactNumber(customerRequest.getContactNumber() != null ? customerRequest.getContactNumber() : customer.getContactNumber());
            customer.setEmail(customerRequest.getEmail() != null ? customerRequest.getEmail() : customer.getEmail());
            customer.setName(customerRequest.getName() != null ? customerRequest.getName() : customer.getName());

            customer = customerRepository.save(customer);
        }

        return customer;
    }

    public Customer findById(Long id){
        Customer customer = null;

        Optional<Customer> customerOpt = customerRepository.findById(id);
        if(customerOpt!=null && customerOpt.isPresent()){
            customer = customerOpt.get();
        }

        return customer;
    }

    public Long deleteById(Long id){
        Long customerId = null;

        Optional<Customer> customerOpt = customerRepository.findById(id);
        if(customerOpt!=null && customerOpt.isPresent()){
            Customer customer = customerOpt.get();
            customerRepository.delete(customer);
            customerId = id;
        }

        return customerId;
    }
}