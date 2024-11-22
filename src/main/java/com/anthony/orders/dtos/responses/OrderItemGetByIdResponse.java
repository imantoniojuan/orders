package com.anthony.orders.dtos.responses;

import com.anthony.orders.entities.OrderItem;

public class OrderItemGetByIdResponse extends ResponseStatus{
    private OrderItem orderItem;

    public OrderItem getOrderItem() {
        return orderItem;
    }

    public void setOrderItem(OrderItem orderItem) {
        this.orderItem = orderItem;
    }
}
