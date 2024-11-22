package com.anthony.orders.services;

import org.springframework.stereotype.Service;

import com.anthony.orders.dtos.requests.OrderPostRequest;
import com.anthony.orders.dtos.requests.OrderPutRequest;
import com.anthony.orders.entities.Customer;
import com.anthony.orders.entities.Order;
import com.anthony.orders.repositories.OrderRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import javax.transaction.Transactional;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final CustomerService customerService;

    public OrderService(OrderRepository orderRepository, CustomerService customerService){
        this.orderRepository = orderRepository;
        this.customerService = customerService;
    }

    public Order add(OrderPostRequest request){
        Order order = new Order();

        Customer customer = customerService.findById(request.getCustomerId());
        if(customer!=null){
            order.setCustomer(customer);
            order.setStatus(request.getStatus());
            order.setTotalAmount(request.getTotalAmount());
            order.setOrderedAt(LocalDateTime.now());
            order = orderRepository.save(order);
        }

        return order;
    }
  
    public Order modify(OrderPutRequest request){
        Order order = new Order();

        Optional<Order> orderOpt = orderRepository.findById(request.getId());
        if(orderOpt.isPresent() && orderOpt.get().getCustomer().getId().equals(request.getCustomerId())){
            order = orderOpt.get();
            order.setStatus(request.getStatus() != null ? request.getStatus() : order.getStatus());
            order.setTotalAmount(request.getTotalAmount() != null ? request.getTotalAmount() : order.getTotalAmount());
            order = orderRepository.save(order);
        }

        return order;
    }

    public Order modify(Order orderInput){
        Order order = new Order();

        Optional<Order> orderOpt = orderRepository.findById(orderInput.getId());
        if(orderOpt.isPresent()){
            order = orderOpt.get();
            order.setStatus(orderInput.getStatus() != null ? orderInput.getStatus() : order.getStatus());
            order.setTotalAmount(orderInput.getTotalAmount() != null ? orderInput.getTotalAmount() : order.getTotalAmount());
            order = orderRepository.save(order);
        }

        return order;
    }

    public List<Order> findByCustomerId(Long customerId){
        List<Order> orderList = orderRepository.findByCustomerId(customerId);
        return orderList;
    }

    public Order findByIdAndCustomerId(Long id, Long customerId){
        Order order = new Order();

        Optional<Order> orderOpt = orderRepository.findByIdAndCustomerId(id, customerId);
        if(orderOpt.isPresent()){
            order = orderOpt.get();
        }

        return order;
    }

    @Transactional
    public Long deleteByIdAndCustomerId(Long id, Long customerId){

        orderRepository.deleteByIdAndCustomerId(id, customerId);
        
        return id;
    }
}