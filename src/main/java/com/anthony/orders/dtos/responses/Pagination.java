package com.anthony.orders.dtos.responses;

import java.util.Objects;

public class Pagination {

    private Integer offset = null;
    private Integer limit = null;
    private Integer numOfItems = null;
    private Integer totalPages = null;
    private Integer totalItems = null;

	public Pagination(){}

	public Pagination offset(Integer offset) {
		this.offset = offset;
		return this;
	}

	public Integer getOffset() {
		return offset;
	}

	public void setOffset(Integer offset) {
		this.offset = offset;
    }
    
    public Pagination limit(Integer limit) {
		this.limit = limit;
		return this;
	}

	public Integer getLimit() {
		return limit;
	}

	public void setLimit(Integer limit) {
		this.limit = limit;
    }
    public Pagination numOfItems(Integer numOfItems) {
		this.numOfItems = numOfItems;
		return this;
	}

	public Integer getNumOfItems() {
		return numOfItems;
	}

	public void setNumOfItems(Integer numOfItems) {
		this.numOfItems = numOfItems;
    }
    public Pagination totalPages(Integer totalPages) {
		this.totalPages = totalPages;
		return this;
	}

	public Integer getTotalPages() {
		return totalPages;
	}

	public void setTotalPages(Integer totalPages) {
		this.totalPages = totalPages;
    }
    public Pagination totalItems(Integer totalItems) {
		this.totalItems = totalItems;
		return this;
	}

	public Integer getTotalItems() {
		return totalItems;
	}

	public void setTotalItems(Integer totalItems) {
		this.totalItems = totalItems;
    }

	@Override
	public boolean equals(java.lang.Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}
		Pagination p = (Pagination) o;
        return Objects.equals(this.offset, p.offset) &&
                Objects.equals(this.limit, p.limit) &&
                Objects.equals(this.numOfItems, p.numOfItems)&&
                Objects.equals(this.totalPages, p.totalPages)&&
                Objects.equals(this.totalItems, p.totalItems);
	}

	@Override
	public int hashCode() {
		return Objects.hash(offset,limit,numOfItems,totalPages,totalItems);
	}
}
