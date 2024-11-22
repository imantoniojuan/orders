package com.anthony.orders.controllers;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.anthony.orders.dtos.requests.OrderItemPostRequest;
import com.anthony.orders.dtos.requests.OrderItemPutRequest;
import com.anthony.orders.dtos.responses.OrderItemDeleteByIdResponse;
import com.anthony.orders.dtos.responses.OrderItemGetByIdResponse;
import com.anthony.orders.dtos.responses.OrderItemGetResponse;
import com.anthony.orders.dtos.responses.OrderItemPostResponse;
import com.anthony.orders.dtos.responses.OrderItemPutResponse;
import com.anthony.orders.dtos.responses.Pagination;
import com.anthony.orders.entities.OrderItem;
import com.anthony.orders.services.OrderItemService;

@RequestMapping("/api/order-items")
@RestController
public class OrderItemController extends BaseController{

    @Autowired
    private OrderItemService orderItemService;

    @PostMapping("")
    public ResponseEntity<OrderItemPostResponse> add(HttpServletRequest request, @RequestBody OrderItemPostRequest orderItemPostRequest) {
        OrderItemPostResponse response = new OrderItemPostResponse();
        prepare(response);
    
        Long customerId = (Long) request.getAttribute("customerId");
        System.out.println(customerId);
        orderItemPostRequest.setCustomerId(customerId);
        OrderItem orderItem = orderItemService.add(orderItemPostRequest);
        if(orderItem.getId() == null){
            response.setErrorMessage("Not allowed to add order item to order");
            return new ResponseEntity<>(response, HttpStatus.FORBIDDEN);
        }
        else{
            response.setOrderItem(orderItem);
        }
        
        conclude(response);
		return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("")
    public ResponseEntity<OrderItemGetResponse> getAll(HttpServletRequest request, @RequestParam(required=false) Integer offset, @RequestParam(required=false) Integer limit) {
        OrderItemGetResponse response = new OrderItemGetResponse();
        prepare(response);
    
        Long customerId = (Long) request.getAttribute("customerId");

        List<OrderItem> orderItemList = orderItemService.findByCustomerId(customerId);
        if(orderItemList != null){
            if(offset!=null && limit!=null){
                Pagination pagination = new Pagination();
                List<OrderItem> temp = new ArrayList<OrderItem>();

                pagination.setTotalItems(orderItemList.size());
                pagination.setTotalPages((orderItemList.size() + limit -1) / limit);
                pagination.setLimit(limit);
                pagination.setOffset(offset);

                for (int i = limit*offset; i < (limit*(offset+1)) && i < orderItemList.size(); i++) {
                    temp.add(orderItemList.get(i));
                }
                pagination.setNumOfItems(temp.size());
                response.setPagination(pagination);
                orderItemList = temp;
            }
            response.setOrderItems(orderItemList);
        }
        else{
            response.setOrderItems(new ArrayList<OrderItem>());
        }
        

        conclude(response);
		return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PutMapping("/{id}")
    public ResponseEntity<OrderItemPutResponse> modify(HttpServletRequest request, @PathVariable Long id, @RequestBody OrderItemPutRequest orderItemPutRequest) {
        OrderItemPutResponse response = new OrderItemPutResponse();
        prepare(response);
    
        Long customerId = (Long) request.getAttribute("customerId");
        orderItemPutRequest.setCustomerId(customerId);
        orderItemPutRequest.setId(id);
        OrderItem orderItem = orderItemService.modify(orderItemPutRequest);
        if(orderItem.getId() == null){
            response.setErrorMessage("Not allowed to modify order item");
            return new ResponseEntity<>(response, HttpStatus.FORBIDDEN);
        }
        else{
            response.setOrderItem(orderItem);
        }
        
        conclude(response);
		return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrderItemGetByIdResponse> getById(HttpServletRequest request, @PathVariable Long id) {
        OrderItemGetByIdResponse response = new OrderItemGetByIdResponse();
        prepare(response);

        Long customerId = (Long) request.getAttribute("customerId");
        response.setOrderItem(orderItemService.findByIdAndCustomerId(id,customerId));

        conclude(response);
		return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<OrderItemDeleteByIdResponse> deleteById(HttpServletRequest request, @PathVariable Long id) {
        OrderItemDeleteByIdResponse response = new OrderItemDeleteByIdResponse();
        prepare(response);

        Long customerId = (Long) request.getAttribute("customerId");
        Long orderItemId = orderItemService.deleteByIdAndCustomerId(id,customerId, true);
        if(orderItemId!=null){
            response.setId(orderItemId);
        }
        else{
            response.setErrorMessage("Error deleting");
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }

        conclude(response);
		return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
