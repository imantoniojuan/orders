package com.anthony.orders.repositories;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.anthony.orders.entities.Order;

import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends CrudRepository<Order, Long> {
    Optional<Order> findByStatus(String status);
    List<Order> findByCustomerId(Long customerId);
    Optional<Order> findByIdAndCustomerId(Long id, Long customerId);
    void deleteByIdAndCustomerId(Long id, Long customerId);
}