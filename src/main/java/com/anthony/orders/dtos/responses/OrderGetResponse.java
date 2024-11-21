package com.anthony.orders.dtos.responses;

import java.util.List;

import com.anthony.orders.entities.Order;

public class OrderGetResponse extends ResponseStatus{
    private List<Order> orders;
    private Pagination pagination;
    
    public Pagination getPagination() {
        return pagination;
    }

    public void setPagination(Pagination pagination) {
        this.pagination = pagination;
    }

    public List<Order> getOrders() {
        return orders;
    }

    public void setOrders(List<Order> orders) {
        this.orders = orders;
    }
}
