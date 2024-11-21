package com.anthony.orders.repositories;

import java.util.Optional;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.anthony.orders.entities.Product;

@Repository
public interface ProductRepository extends CrudRepository<Product, Long> {
    Optional<Product> findByUpc(String upc);
}