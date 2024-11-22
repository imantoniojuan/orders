package com.anthony.orders.services;

import org.springframework.stereotype.Service;

import com.anthony.orders.dtos.requests.OrderItemPostRequest;
import com.anthony.orders.dtos.requests.OrderItemPutRequest;
import com.anthony.orders.entities.Order;
import com.anthony.orders.entities.OrderItem;
import com.anthony.orders.entities.Product;
import com.anthony.orders.repositories.OrderItemRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.transaction.Transactional;

@Service
public class OrderItemService {

    private final OrderItemRepository orderItemRepository;
    private final OrderService orderService;
    private final ProductService productService;

    public OrderItemService(OrderItemRepository orderItemRepository, OrderService orderService, ProductService productService) {
        this.orderItemRepository = orderItemRepository;
        this.orderService = orderService;
        this.productService = productService;
    }

    public OrderItem add(OrderItemPostRequest request){
        OrderItem orderItem = new OrderItem();

        Order order = orderService.findByIdAndCustomerId(request.getOrderId(), request.getCustomerId());
        Product product = productService.findById(request.getProductId());
        if(order!=null && order.getId()!=null && product!=null && product.getId()!=null){
            orderItem.setOrder(order);
            orderItem.setProduct(product);
            orderItem.setQuantity(request.getQuantity());
            orderItem.setSubTotal(request.getUnitPrice()*request.getQuantity());
            orderItem.setUnitPrice(request.getUnitPrice());
            orderItem = orderItemRepository.save(orderItem);

            updateOrderTotalAmount(order.getId(), request.getCustomerId());
        }

        return orderItem;
    }
  
    public OrderItem modify(OrderItemPutRequest request){
        OrderItem orderItem = new OrderItem();

        Optional<OrderItem> orderItemOpt = orderItemRepository.findById(request.getId());
        if(orderItemOpt.isPresent() && orderItemOpt.get().getOrder().getCustomer().getId().equals(request.getCustomerId())){
            orderItem = orderItemOpt.get();
            orderItem.setQuantity(request.getQuantity() != null ? request.getQuantity() : orderItem.getQuantity());
            orderItem.setSubTotal(request.getUnitPrice()*request.getQuantity());
            orderItem.setUnitPrice(request.getUnitPrice() != null ? request.getUnitPrice() : orderItem.getUnitPrice());
            orderItem = orderItemRepository.save(orderItem);

            updateOrderTotalAmount(orderItemOpt.get().getOrder().getId(), request.getCustomerId());
        }

        return orderItem;
    }

    public List<OrderItem> findByCustomerId(Long customerId){
        List<OrderItem> orderItemList = new ArrayList<OrderItem>();

        List<Order> orderList = orderService.findByCustomerId(customerId);
        for(Order order:orderList){
            orderItemList.addAll(orderItemRepository.findByOrderId(order.getId()));
        }

        return orderItemList;
    }

    public OrderItem findByIdAndCustomerId(Long id, Long customerId){
        OrderItem orderItem = new OrderItem();

        Optional<OrderItem> orderItemOpt = orderItemRepository.findById(id);
        if(orderItemOpt.isPresent()){
            if(orderItemOpt.get().getOrder().getCustomer().getId().equals(customerId)){
                orderItem = orderItemOpt.get();
            }
        }

        return orderItem;
    }

    @Transactional
    public Long deleteByIdAndCustomerId(Long id, Long customerId, Boolean updateOrder){
   
        Optional<OrderItem> orderItemOpt = orderItemRepository.findById(id);
        if(orderItemOpt.isPresent()){
            if(orderItemOpt.get().getOrder().getCustomer().getId().equals(customerId)){
                Long orderId = orderItemOpt.get().getOrder().getId();
                orderItemRepository.delete(orderItemOpt.get());
                if(updateOrder)
                    updateOrderTotalAmount(orderId, customerId);
            }
        }

        return id;
    }

    @Transactional
    public Long deleteByProductIdAndCustomerId(Long productId, Long customerId){
   
        Optional<OrderItem> orderItemOpt = orderItemRepository.findByProductId(productId);
        if(orderItemOpt.isPresent()){
            if(orderItemOpt.get().getOrder().getId().equals(customerId)){
                Long orderId = orderItemOpt.get().getOrder().getId();
                orderItemRepository.delete(orderItemOpt.get());
                updateOrderTotalAmount(orderId, customerId);
            }
        }

        return productId;
    }

    private void updateOrderTotalAmount(Long orderId, Long customerId){
        Order order = orderService.findByIdAndCustomerId(orderId, customerId);
        List<OrderItem> orderItems = orderItemRepository.findByOrderId(orderId);
        Double totalAmount = 0.0;
        for(OrderItem oi:orderItems){
            totalAmount += oi.getSubTotal();
        }
        order.setTotalAmount(totalAmount);
        orderService.modify(order);
    }
}