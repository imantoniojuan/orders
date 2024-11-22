package com.anthony.orders.dtos.responses;

import java.util.List;

import com.anthony.orders.entities.OrderItem;

public class OrderItemGetResponse extends ResponseStatus{
    private List<OrderItem> orderItems;
    private Pagination pagination;

    public List<OrderItem> getOrderItems() {
        return orderItems;
    }
    public void setOrderItems(List<OrderItem> orderItems) {
        this.orderItems = orderItems;
    }
    public Pagination getPagination() {
        return pagination;
    }
    public void setPagination(Pagination pagination) {
        this.pagination = pagination;
    }
}
