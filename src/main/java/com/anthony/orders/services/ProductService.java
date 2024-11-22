package com.anthony.orders.services;

import org.springframework.stereotype.Service;

import com.anthony.orders.dtos.requests.ProductPostRequest;
import com.anthony.orders.dtos.requests.ProductPutRequest;
import com.anthony.orders.entities.Product;
import com.anthony.orders.repositories.ProductRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.transaction.Transactional;

@Service
public class ProductService {

    private final ProductRepository productRepository;

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public Product add(ProductPostRequest request){
        Product product = new Product();

        Optional<Product> productOpt = productRepository.findByUpc(request.getUpc());
        if(productOpt.isPresent()){
            if(productOpt.get().getName().equals(request.getName()) && productOpt.get().getDescription().equals(request.getDescription()))
                product = productOpt.get();
        }
        else{
            product.setUpc(request.getUpc());
            product.setName(request.getName());
            product.setDescription(request.getDescription());
            product = productRepository.save(product);
        }
        

        return product;
    }
  
    public Product modify(ProductPutRequest request){
        Product product = new Product();

        Optional<Product> productOpt = productRepository.findById(request.getId());
        if(productOpt.isPresent()){
            product = productOpt.get();            
            product.setUpc(request.getUpc() != null ? request.getUpc() : product.getUpc());
            product.setName(request.getName() != null ? request.getName() : product.getName());
            product.setDescription(request.getDescription() != null ? request.getDescription() : product.getDescription());
            product = productRepository.save(product);
        }

        return product;
    }

    public List<Product> findAll(){
        List<Product> orderList = new ArrayList<>();

        Iterable<Product> orderListIterable = productRepository.findAll();
        orderListIterable.forEach(orderList::add);

        return orderList;
    }

    public Product findById(Long id){
        Product product = new Product();

        Optional<Product> productOpt = productRepository.findById(id);
        if(productOpt.isPresent()){
            product = productOpt.get();
        }

        return product;
    }

    @Transactional
    public Long deleteByIdAndCustomerId(Long id, Long customerId){

        productRepository.deleteById(id);

        return id;
    }
}