package com.anthony.orders.dtos.responses;

import com.anthony.orders.entities.Order;

public class OrderPutResponse extends ResponseStatus{
    private Order order;

    public Order getOrder() {
        return order;
    }

    public void setOrder(Order order) {
        this.order = order;
    }
}
