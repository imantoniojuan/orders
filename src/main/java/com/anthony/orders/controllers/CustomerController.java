package com.anthony.orders.controllers;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.anthony.orders.dtos.requests.CustomerPutRequest;
import com.anthony.orders.dtos.responses.CustomerDeleteByIdResponse;
import com.anthony.orders.dtos.responses.CustomerGetByIdResponse;
import com.anthony.orders.dtos.responses.CustomerPutResponse;
import com.anthony.orders.entities.Customer;
import com.anthony.orders.services.CustomerService;

@RequestMapping("/api/customer")
@RestController
public class CustomerController extends BaseController{

    @Autowired
    private CustomerService customerService;

    @PutMapping("/{id}")
    public ResponseEntity<CustomerPutResponse> modify(HttpServletRequest request, @PathVariable Long id, @RequestBody CustomerPutRequest customerRequest) {
        CustomerPutResponse response = new CustomerPutResponse();
        prepare(response);
    
        Long customerId = (Long) request.getAttribute("customerId");

        if(customerId!=id){
            response.setErrorMessage("Not allowed to modify customer.");
            return new ResponseEntity<>(response, HttpStatus.FORBIDDEN);
        }
        customerRequest.setId(customerId);

        response.setCustomer(customerService.modify(customerRequest));

        conclude(response);
		return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CustomerGetByIdResponse> getById(HttpServletRequest request, @PathVariable Long id) {
        CustomerGetByIdResponse response = new CustomerGetByIdResponse();
        prepare(response);

        Long customerId = (Long) request.getAttribute("customerId");
        if(customerId!=id){
            response.setErrorMessage("Not allowed to query ID");
            return new ResponseEntity<>(response, HttpStatus.FORBIDDEN);
        }

        Customer customer = customerService.findById(id);
        if(customer!=null){
            response.setCustomer(customer);
        }
        else{
            response.setErrorMessage("ID not found");
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }

        conclude(response);
		return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<CustomerDeleteByIdResponse> deleteById(HttpServletRequest request, @PathVariable Long id) {
        CustomerDeleteByIdResponse response = new CustomerDeleteByIdResponse();
        prepare(response);

        Long customerId = (Long) request.getAttribute("customerId");
        if(customerId!=id){
            response.setErrorMessage("Not allowed to delete ID");
            return new ResponseEntity<>(response, HttpStatus.FORBIDDEN);
        }

        customerId = customerService.deleteById(id);
        if(customerId!=null){
            response.setId(customerId);
        }
        else{
            response.setErrorMessage("Error deleting");
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }

        conclude(response);
		return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
