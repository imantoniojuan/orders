package com.anthony.orders.controllers;

import java.util.ArrayList;
import java.util.List;

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

import com.anthony.orders.dtos.requests.ProductPostRequest;
import com.anthony.orders.dtos.requests.ProductPutRequest;
import com.anthony.orders.dtos.responses.Pagination;
import com.anthony.orders.dtos.responses.ProductDeleteByIdResponse;
import com.anthony.orders.dtos.responses.ProductGetByIdResponse;
import com.anthony.orders.dtos.responses.ProductGetResponse;
import com.anthony.orders.dtos.responses.ProductPostResponse;
import com.anthony.orders.dtos.responses.ProductPutResponse;
import com.anthony.orders.entities.Product;
import com.anthony.orders.services.ProductService;

@RequestMapping("/api/products")
@RestController
public class ProductController extends BaseController{

    @Autowired
    private ProductService productService;

    @PostMapping("")
    public ResponseEntity<ProductPostResponse> add(@RequestBody ProductPostRequest productPostRequest) {
        ProductPostResponse response = new ProductPostResponse();
        prepare(response);
    
        response.setProduct(productService.add(productPostRequest));

        conclude(response);
		return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PutMapping("")
    public ResponseEntity<ProductPutResponse> modify(@RequestBody ProductPutRequest productPutRequest) {
        ProductPutResponse response = new ProductPutResponse();
        prepare(response);
    
        Product product = productService.modify(productPutRequest);

        if(product.getId() == null){
            response.setErrorMessage("Product ID not found");
            return new ResponseEntity<>(response, HttpStatus.FORBIDDEN);
        }
        else{
            response.setProduct(product);
        }
        
        conclude(response);
		return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("")
    public ResponseEntity<ProductGetResponse> getAll(@RequestParam(required=false) Integer offset, @RequestParam(required=false) Integer limit) {
        ProductGetResponse response = new ProductGetResponse();
        prepare(response);

        List<Product> productList = productService.findAll();
        if(productList != null){
            if(offset!=null && limit!=null){
                Pagination pagination = new Pagination();
                List<Product> temp = new ArrayList<Product>();

                pagination.setTotalItems(productList.size());
                pagination.setTotalPages((productList.size() + limit -1) / limit);
                pagination.setLimit(limit);
                pagination.setOffset(offset);

                for (int i = limit*offset; i < (limit*(offset+1)) && i < productList.size(); i++) {
                    temp.add(productList.get(i));
                }
                pagination.setNumOfItems(temp.size());
                response.setPagination(pagination);
                productList = temp;
            }
            response.setProducts(productList);
        }
        else{
            response.setProducts(new ArrayList<Product>());
        }
        
        conclude(response);
		return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductGetByIdResponse> getById(@PathVariable Long id) {
        ProductGetByIdResponse response = new ProductGetByIdResponse();
        prepare(response);

        response.setProduct(productService.findById(id));

        conclude(response);
		return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ProductDeleteByIdResponse> deleteById(@PathVariable Long id) {
        ProductDeleteByIdResponse response = new ProductDeleteByIdResponse();
        prepare(response);

        Long productId = productService.deleteById(id);
        if(productId!=null){
            response.setId(productId);
        }
        else{
            response.setErrorMessage("Error deleting");
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }

        conclude(response);
		return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
