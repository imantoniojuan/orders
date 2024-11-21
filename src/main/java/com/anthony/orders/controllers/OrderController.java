package com.anthony.orders.controllers;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.anthony.orders.dtos.requests.OrderPostRequest;
import com.anthony.orders.dtos.requests.OrderPutRequest;
import com.anthony.orders.dtos.responses.OrderDeleteByIdResponse;
import com.anthony.orders.dtos.responses.OrderGetByIdResponse;
import com.anthony.orders.dtos.responses.OrderGetResponse;
import com.anthony.orders.dtos.responses.OrderPostResponse;
import com.anthony.orders.dtos.responses.OrderPutResponse;
import com.anthony.orders.dtos.responses.Pagination;
import com.anthony.orders.entities.Order;
import com.anthony.orders.services.CustomerService;
import com.anthony.orders.services.OrderService;

@RequestMapping("/api/orders")
@RestController
public class OrderController extends BaseController{

    @Autowired
    private OrderService orderService;

    @Autowired
    private CustomerService customerService;

    @PostMapping("")
    public ResponseEntity<OrderPostResponse> add(HttpServletRequest request, @RequestBody OrderPostRequest orderPostRequest) {
        OrderPostResponse response = new OrderPostResponse();
        prepare(response);
    
        Long customerId = (Long) request.getAttribute("customerId");
        orderPostRequest.setCustomer(customerService.findById(customerId));
        response.setOrder(orderService.add(orderPostRequest));

        conclude(response);
		return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PutMapping("")
    public ResponseEntity<OrderPutResponse> modify(HttpServletRequest request, @RequestBody OrderPutRequest orderPutRequest) {
        OrderPutResponse response = new OrderPutResponse();
        prepare(response);
    
        Long customerId = (Long) request.getAttribute("customerId");
        orderPutRequest.setCustomer(customerService.findById(customerId));
        Order order = orderService.modify(orderPutRequest);

        if(order.getId() == null){
            response.setErrorMessage("Not allowed to modify order");
            return new ResponseEntity<>(response, HttpStatus.FORBIDDEN);
        }
        else{
            response.setOrder(order);
        }
        
        conclude(response);
		return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("")
    public ResponseEntity<OrderGetResponse> getAll(HttpServletRequest request, @RequestParam(required=false) Integer offset, @RequestParam(required=false) Integer limit) {
        OrderGetResponse response = new OrderGetResponse();
        prepare(response);
    
        Long customerId = (Long) request.getAttribute("customerId");

        List<Order> orderList = orderService.findByCustomerId(customerId);
        if(orderList != null){
            if(offset!=null && limit!=null){
                Pagination pagination = new Pagination();
                List<Order> temp = new ArrayList<Order>();

                pagination.setTotalItems(orderList.size());
                pagination.setTotalPages((orderList.size() + limit -1) / limit);
                pagination.setLimit(limit);
                pagination.setOffset(offset);

                for (int i = limit*offset; i < (limit*(offset+1)) && i < orderList.size(); i++) {
                    temp.add(orderList.get(i));
                }
                pagination.setNumOfItems(temp.size());
                response.setPagination(pagination);
                orderList = temp;
            }
            response.setOrders(orderList);
        }
        else{
            response.setOrders(new ArrayList<Order>());
        }
        

        conclude(response);
		return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrderGetByIdResponse> getById(HttpServletRequest request, @PathVariable Long id) {
        OrderGetByIdResponse response = new OrderGetByIdResponse();
        prepare(response);

        Long customerId = (Long) request.getAttribute("customerId");
        response.setOrder(orderService.findByIdAndCustomerId(id,customerId));

        conclude(response);
		return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<OrderDeleteByIdResponse> deleteById(HttpServletRequest request, @PathVariable Long id) {
        OrderDeleteByIdResponse response = new OrderDeleteByIdResponse();
        prepare(response);

        Long customerId = (Long) request.getAttribute("customerId");
        Long orderId = orderService.deleteByIdAndCustomerId(id,customerId);
        if(orderId!=null){
            response.setId(orderId);
        }
        else{
            response.setErrorMessage("Error deleting");
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }

        conclude(response);
		return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
