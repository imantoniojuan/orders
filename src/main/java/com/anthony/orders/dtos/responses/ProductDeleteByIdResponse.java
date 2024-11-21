package com.anthony.orders.dtos.responses;

public class ProductDeleteByIdResponse extends ResponseStatus{
    private Long id;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
