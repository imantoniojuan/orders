package com.anthony.orders;

import java.net.URI;
import java.net.URISyntaxException;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import org.json.JSONException;

import com.anthony.orders.dtos.requests.OrderItemPostRequest;
import com.anthony.orders.dtos.requests.OrderItemPutRequest;
import com.anthony.orders.dtos.requests.RegisterRequest;
import com.anthony.orders.dtos.responses.CustomerDeleteByIdResponse;
import com.anthony.orders.dtos.responses.LoginResponse;
import com.anthony.orders.dtos.responses.OrderDeleteByIdResponse;
import com.anthony.orders.dtos.responses.OrderItemDeleteByIdResponse;
import com.anthony.orders.dtos.responses.OrderItemGetByIdResponse;
import com.anthony.orders.dtos.responses.OrderItemGetResponse;
import com.anthony.orders.dtos.responses.OrderItemPostResponse;
import com.anthony.orders.dtos.responses.OrderItemPutResponse;
import com.anthony.orders.dtos.responses.OrderPostResponse;
import com.anthony.orders.dtos.responses.ProductDeleteByIdResponse;
import com.anthony.orders.dtos.responses.ProductPostResponse;
import com.anthony.orders.dtos.responses.RegisterResponse;
import com.anthony.orders.entities.Customer;
import com.anthony.orders.entities.Order;
import com.anthony.orders.entities.OrderItem;
import com.anthony.orders.entities.Product;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@SpringBootTest(classes = OrdersApplication.class, webEnvironment = WebEnvironment.DEFINED_PORT)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class OrderItemsControllerTests {

    @LocalServerPort
    int serverPort = 8081;

    String testEmail = "test3@email.com";
    String testPwd = "pword124!";
    String sessionToken = "";
    Customer createdCustomer;
    Order createdOrder;
    Product createdProduct;
    OrderItem createdOrderItem;

    @BeforeAll
    public void register() throws URISyntaxException {
        RestTemplate restTemplate = new RestTemplate();
        URI uri = new URI("http://localhost:" + serverPort + "/api/auth/register");

        RegisterRequest registerRequest = new RegisterRequest();
        registerRequest.setName("Unit Tester3");
        registerRequest.setEmail(testEmail);
        registerRequest.setPassword(testPwd);
        registerRequest.setAddress("123 St Road");
        registerRequest.setContactNumber("+6599990000");

        HttpEntity<RegisterRequest> request = new HttpEntity<>(registerRequest);
        ResponseEntity<RegisterResponse> response = restTemplate.exchange(uri, HttpMethod.POST, request, RegisterResponse.class);

        Assertions.assertEquals(HttpStatus.CREATED, response.getStatusCode());

        RegisterResponse responseBody = response.getBody();
        Assertions.assertNotNull(responseBody);

        createdCustomer = responseBody.getCustomer();

        Assertions.assertNotNull(createdCustomer);
        Assertions.assertNotNull(createdCustomer.getId());
    }

    @BeforeEach
    public void login() throws URISyntaxException, JSONException {
        RestTemplate restTemplate = new RestTemplate();
         // Login to obtain token
         URI loginUri = new URI("http://localhost:" + serverPort + "/api/auth/login");

         // Set headers for login request
         HttpHeaders loginHeaders = new HttpHeaders();
         loginHeaders.set("Content-Type", "application/json");
 
         // Set login request body
         String loginRequestBody = String.format("{\"email\": \"%s\", \"password\": \"%s\"}", testEmail, testPwd);
 
         HttpEntity<String> loginRequest = new HttpEntity<>(loginRequestBody, loginHeaders);
         ResponseEntity<LoginResponse> response = restTemplate.exchange(loginUri, HttpMethod.POST, loginRequest, LoginResponse.class);
 
         Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());

         LoginResponse loginResponse = response.getBody();
         // Verify login response and extract token
         Assertions.assertNotNull(loginResponse);

         sessionToken = loginResponse.getToken();
         Assertions.assertNotNull(sessionToken);
    }


    @org.junit.jupiter.api.Order(1)
    @Test
    public void testPostOrderItem() throws URISyntaxException {

        // Create an Order
        RestTemplate restTemplate = new RestTemplate();
        URI uri = new URI("http://localhost:" + serverPort + "/api/orders");

        Order order = new Order();
        order.setStatus("New");
        order.setTotalAmount(10.0);

        HttpHeaders authHeaders = new HttpHeaders();
        authHeaders.set("Authorization", "Bearer " + sessionToken);

        HttpEntity<Order> orderRequest = new HttpEntity<>(order,authHeaders);
        ResponseEntity<OrderPostResponse> orderResponse = restTemplate.exchange(uri, HttpMethod.POST, orderRequest, OrderPostResponse.class);

        Assertions.assertEquals(HttpStatus.CREATED, orderResponse.getStatusCode());
        
        OrderPostResponse orderPostResponse = orderResponse.getBody();

        Assertions.assertNotNull(orderPostResponse);

        createdOrder = orderPostResponse.getOrder();
        Assertions.assertNotNull(createdOrder.getId());
        Assertions.assertEquals(order.getStatus(), createdOrder.getStatus());
        Assertions.assertEquals(order.getTotalAmount(), createdOrder.getTotalAmount());


        // Create a product
        uri = new URI("http://localhost:" + serverPort + "/api/products");

        Product product = new Product();
        product.setUpc("012345678905");
        product.setName("Two Door Cabinet (Grey)");
        product.setDescription("Cabinet with doors, grey, 78x95 cm");

        authHeaders = new HttpHeaders();
        authHeaders.set("Authorization", "Bearer " + sessionToken);

        HttpEntity<Product> productRequest = new HttpEntity<>(product,authHeaders);
        ResponseEntity<ProductPostResponse> productResponse = restTemplate.exchange(uri, HttpMethod.POST, productRequest, ProductPostResponse.class);

        Assertions.assertEquals(HttpStatus.CREATED, productResponse.getStatusCode());
        
        ProductPostResponse productPostResponse = productResponse.getBody();

        Assertions.assertNotNull(productPostResponse);

        createdProduct = productPostResponse.getProduct();
        Assertions.assertNotNull(createdProduct.getId());
        Assertions.assertEquals(product.getUpc(), createdProduct.getUpc());
        Assertions.assertEquals(product.getName(), createdProduct.getName());
        Assertions.assertEquals(product.getDescription(), createdProduct.getDescription());

        // Create a Order Item
        uri = new URI("http://localhost:" + serverPort + "/api/order-items");

        OrderItemPostRequest orderItemRequest = new OrderItemPostRequest();
        orderItemRequest.setOrderId(createdOrder.getId());
        orderItemRequest.setProductId(createdProduct.getId());
        orderItemRequest.setQuantity(3);
        orderItemRequest.setUnitPrice(4.0);
        orderItemRequest.setSubTotal(3*4.0);

        authHeaders = new HttpHeaders();
        authHeaders.set("Authorization", "Bearer " + sessionToken);

        HttpEntity<OrderItemPostRequest> request = new HttpEntity<>(orderItemRequest,authHeaders);
        ResponseEntity<OrderItemPostResponse> response = restTemplate.exchange(uri, HttpMethod.POST, request, OrderItemPostResponse.class);

        Assertions.assertEquals(HttpStatus.CREATED, response.getStatusCode());
        
        OrderItemPostResponse orderItemPostResponse = response.getBody();

        Assertions.assertNotNull(orderItemPostResponse);

        createdOrderItem = orderItemPostResponse.getOrderItem();
        Assertions.assertNotNull(createdOrderItem.getId());
        Assertions.assertEquals(createdOrderItem.getQuantity(), orderItemRequest.getQuantity());
        Assertions.assertEquals(createdOrderItem.getUnitPrice(), orderItemRequest.getUnitPrice());
        Assertions.assertEquals(createdOrderItem.getSubTotal(), orderItemRequest.getSubTotal());
    }

    @org.junit.jupiter.api.Order(2)
    @Test
    public void testGetOrderItem() throws URISyntaxException {
        RestTemplate restTemplate = new RestTemplate();
        URI uri = new URI("http://localhost:" + serverPort + "/api/order-items");

        HttpHeaders authHeaders = new HttpHeaders();
        authHeaders.set("Authorization", "Bearer " + sessionToken);

        HttpEntity<HttpHeaders> request = new HttpEntity<>(authHeaders);
        ResponseEntity<OrderItemGetResponse> response = restTemplate.exchange(uri, HttpMethod.GET, request, OrderItemGetResponse.class);

        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
        
        OrderItemGetResponse orderItemGetResponse = response.getBody();

        Assertions.assertNotNull(orderItemGetResponse);
        Assertions.assertNotNull(orderItemGetResponse.getOrderItems());
        Assertions.assertFalse(orderItemGetResponse.getOrderItems().isEmpty());
    }

    @org.junit.jupiter.api.Order(3)
    @Test
    public void testPutOrderItemById() throws URISyntaxException {
        RestTemplate restTemplate = new RestTemplate();
        URI uri = new URI("http://localhost:" + serverPort + "/api/order-items/" + createdOrderItem.getId());

        OrderItemPutRequest orderItemPutRequest = new OrderItemPutRequest();
        orderItemPutRequest.setOrderId(createdOrder.getId());
        orderItemPutRequest.setProductId(createdProduct.getId());
        orderItemPutRequest.setQuantity(2);
        orderItemPutRequest.setUnitPrice(5.0);
        orderItemPutRequest.setSubTotal(2*5.0);

        HttpHeaders authHeaders = new HttpHeaders();
        authHeaders.set("Authorization", "Bearer " + sessionToken);

        HttpEntity<OrderItemPutRequest> request = new HttpEntity<>(orderItemPutRequest,authHeaders);
        ResponseEntity<OrderItemPutResponse> response = restTemplate.exchange(uri, HttpMethod.PUT, request, OrderItemPutResponse.class);

        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
        
        OrderItemPutResponse orderItemPutResponse = response.getBody();

        Assertions.assertNotNull(orderItemPutResponse);

        OrderItem updatedOrderItem = orderItemPutResponse.getOrderItem();
        Assertions.assertEquals(createdOrderItem.getId(), updatedOrderItem.getId());
        Assertions.assertEquals(orderItemPutRequest.getQuantity(), updatedOrderItem.getQuantity());
        Assertions.assertEquals(orderItemPutRequest.getUnitPrice(), updatedOrderItem.getUnitPrice());
        Assertions.assertEquals(orderItemPutRequest.getSubTotal(), updatedOrderItem.getSubTotal());

        createdOrderItem = updatedOrderItem;
    }

    @org.junit.jupiter.api.Order(4)
    @Test
    public void testGetOrderItemById() throws URISyntaxException {
        RestTemplate restTemplate = new RestTemplate();
        URI uri = new URI("http://localhost:" + serverPort + "/api/order-items/" + createdOrderItem.getId());

        HttpHeaders authHeaders = new HttpHeaders();
        authHeaders.set("Authorization", "Bearer " + sessionToken);

        HttpEntity<HttpHeaders> request = new HttpEntity<>(authHeaders);
        ResponseEntity<OrderItemGetByIdResponse> response = restTemplate.exchange(uri, HttpMethod.GET, request, OrderItemGetByIdResponse.class);

        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());

        OrderItemGetByIdResponse responseBody = response.getBody();
        Assertions.assertNotNull(responseBody);

        OrderItem retrievedOrderItem = responseBody.getOrderItem();

        Assertions.assertNotNull(retrievedOrderItem);
        Assertions.assertEquals(createdOrderItem, retrievedOrderItem);
    }

    @AfterAll
    public void cleanup() throws URISyntaxException, JSONException {
        RestTemplate restTemplate = new RestTemplate();

        // Delete OrderItem by ID
        if (createdOrderItem != null) {
            URI orderItemUri = new URI("http://localhost:" + serverPort + "/api/order-items/" + createdOrderItem.getId());
            HttpHeaders orderItemAuthHeaders = new HttpHeaders();
            orderItemAuthHeaders.set("Authorization", "Bearer " + sessionToken);

            HttpEntity<HttpHeaders> orderItemRequest = new HttpEntity<>(orderItemAuthHeaders);
            ResponseEntity<OrderItemDeleteByIdResponse> orderItemResponse = restTemplate.exchange(orderItemUri, HttpMethod.DELETE, orderItemRequest, OrderItemDeleteByIdResponse.class);

            Assertions.assertEquals(HttpStatus.OK, orderItemResponse.getStatusCode());
            OrderItemDeleteByIdResponse orderItemDeleteByIdResponse = orderItemResponse.getBody();
            Assertions.assertNotNull(orderItemDeleteByIdResponse);

            Long deletedOrderItemId = orderItemDeleteByIdResponse.getId();
            Assertions.assertEquals(createdOrderItem.getId(), deletedOrderItemId);
        }

        // Delete Order by ID
        if (createdOrder != null) {
            URI orderUri = new URI("http://localhost:" + serverPort + "/api/orders/" + createdOrder.getId());
            HttpHeaders orderAuthHeaders = new HttpHeaders();
            orderAuthHeaders.set("Authorization", "Bearer " + sessionToken);

            HttpEntity<HttpHeaders> orderRequest = new HttpEntity<>(orderAuthHeaders);
            ResponseEntity<OrderDeleteByIdResponse> orderResponse = restTemplate.exchange(orderUri, HttpMethod.DELETE, orderRequest, OrderDeleteByIdResponse.class);

            Assertions.assertEquals(HttpStatus.OK, orderResponse.getStatusCode());
            OrderDeleteByIdResponse orderDeleteByIdResponse = orderResponse.getBody();
            Assertions.assertNotNull(orderDeleteByIdResponse);

            Long deletedOrderId = orderDeleteByIdResponse.getId();
            Assertions.assertEquals(createdOrder.getId(), deletedOrderId);
        }

        // Delete Product by ID
        if (createdProduct != null) {
            URI productItemUri = new URI("http://localhost:" + serverPort + "/api/products/" + createdProduct.getId());
            HttpHeaders productAuthHeaders = new HttpHeaders();
            productAuthHeaders.set("Authorization", "Bearer " + sessionToken);

            HttpEntity<HttpHeaders> productItemRequest = new HttpEntity<>(productAuthHeaders);
            ResponseEntity<ProductDeleteByIdResponse> productResponse = restTemplate.exchange(productItemUri, HttpMethod.DELETE, productItemRequest, ProductDeleteByIdResponse.class);

            Assertions.assertEquals(HttpStatus.OK, productResponse.getStatusCode());
            ProductDeleteByIdResponse productDeleteByIdResponse = productResponse.getBody();
            Assertions.assertNotNull(productDeleteByIdResponse);

            Long deletedProductId = productDeleteByIdResponse.getId();
            Assertions.assertEquals(createdProduct.getId(), deletedProductId);
        }

        // Delete Customer by ID
        if (createdCustomer != null) {
            URI customerUri = new URI("http://localhost:" + serverPort + "/api/customer/" + createdCustomer.getId());
            HttpHeaders customerAuthHeaders = new HttpHeaders();
            customerAuthHeaders.set("Authorization", "Bearer " + sessionToken);

            HttpEntity<HttpHeaders> customerRequest = new HttpEntity<>(customerAuthHeaders);
            ResponseEntity<CustomerDeleteByIdResponse> customerResponse = restTemplate.exchange(customerUri, HttpMethod.DELETE, customerRequest, CustomerDeleteByIdResponse.class);

            Assertions.assertEquals(HttpStatus.OK, customerResponse.getStatusCode());
            CustomerDeleteByIdResponse customerResponseBody = customerResponse.getBody();
            Assertions.assertNotNull(customerResponseBody);

            Long retrievedCustomerId = customerResponseBody.getId();
            Assertions.assertNotNull(retrievedCustomerId);
            Assertions.assertEquals(retrievedCustomerId, createdCustomer.getId());
        }
    }
}
