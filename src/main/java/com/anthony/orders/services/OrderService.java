package com.anthony.orders.services;

import org.springframework.stereotype.Service;

import com.anthony.orders.dtos.requests.OrderPostRequest;
import com.anthony.orders.dtos.requests.OrderPutRequest;
import com.anthony.orders.entities.Order;
import com.anthony.orders.repositories.OrderRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class OrderService {

    private final OrderRepository orderRepository;

    public OrderService(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    public Order add(OrderPostRequest request){
        Order order = new Order();

        order.setCustomer(request.getCustomer());
        order.setStatus(request.getStatus());
        order.setTotalAmount(request.getTotalAmount());
        order.setOrderedAt(LocalDateTime.now());
        order = orderRepository.save(order);

        return order;
    }
  
    public Order modify(OrderPutRequest request){
        Order order = new Order();
        Optional<Order> orderOpt = orderRepository.findById(request.getId());

        if(orderOpt.isPresent() && orderOpt.get().getCustomer().getId().equals(request.getCustomer().getId())){
            order = orderOpt.get();
            order.setStatus(request.getStatus() != null ? request.getStatus() : order.getStatus());
            order.setTotalAmount(request.getTotalAmount() != null ? request.getTotalAmount() : order.getTotalAmount());
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

    public Long deleteByIdAndCustomerId(Long id, Long customerId){
        // TODO: delete all order-items first
        orderRepository.deleteByIdAndCustomerId(id, customerId);
        return id;
    }
}