package com.anthony.orders;

import java.net.URI;
import java.net.URISyntaxException;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
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

import com.anthony.orders.dtos.requests.RegisterRequest;
import com.anthony.orders.dtos.responses.CustomerDeleteByIdResponse;
import com.anthony.orders.dtos.responses.LoginResponse;
import com.anthony.orders.dtos.responses.OrderDeleteByIdResponse;
import com.anthony.orders.dtos.responses.OrderGetByIdResponse;
import com.anthony.orders.dtos.responses.OrderGetResponse;
import com.anthony.orders.dtos.responses.OrderPostResponse;
import com.anthony.orders.dtos.responses.OrderPutResponse;
import com.anthony.orders.dtos.responses.RegisterResponse;
import com.anthony.orders.entities.Customer;
import com.anthony.orders.entities.Order;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@SpringBootTest(classes = OrdersApplication.class, webEnvironment = WebEnvironment.DEFINED_PORT)
public class OrderControllerTests {

    @LocalServerPort
    int serverPort = 8081;

    String testEmail = "test2@email.com";
    String testPwd = "pword124!";
    String sessionToken = "";
    Customer createdCustomer;
    Order createdOrder;

    @BeforeAll
    public void register() throws URISyntaxException {
        RestTemplate restTemplate = new RestTemplate();
        URI uri = new URI("http://localhost:" + serverPort + "/api/auth/register");

        RegisterRequest registerRequest = new RegisterRequest();
        registerRequest.setName("Unit Tester2");
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

    @Test
    public void testPostOrder() throws URISyntaxException {
        RestTemplate restTemplate = new RestTemplate();
        URI uri = new URI("http://localhost:" + serverPort + "/api/orders");

        Order order = new Order();
        order.setStatus("New");
        order.setTotalAmount(10.0);

        HttpHeaders authHeaders = new HttpHeaders();
        authHeaders.set("Authorization", "Bearer " + sessionToken);

        HttpEntity<Order> request = new HttpEntity<>(order,authHeaders);
        ResponseEntity<OrderPostResponse> response = restTemplate.exchange(uri, HttpMethod.POST, request, OrderPostResponse.class);

        Assertions.assertEquals(HttpStatus.CREATED, response.getStatusCode());
        
        OrderPostResponse orderPostResponse = response.getBody();

        Assertions.assertNotNull(orderPostResponse);

        createdOrder = orderPostResponse.getOrder();
        Assertions.assertNotNull(createdOrder.getId());
        Assertions.assertEquals(order.getStatus(), createdOrder.getStatus());
        Assertions.assertEquals(order.getTotalAmount(), createdOrder.getTotalAmount());
    }

    @Test
    public void testGetOrder() throws URISyntaxException {
        RestTemplate restTemplate = new RestTemplate();
        URI uri = new URI("http://localhost:" + serverPort + "/api/orders");

        HttpHeaders authHeaders = new HttpHeaders();
        authHeaders.set("Authorization", "Bearer " + sessionToken);

        HttpEntity<HttpHeaders> request = new HttpEntity<>(authHeaders);
        ResponseEntity<OrderGetResponse> response = restTemplate.exchange(uri, HttpMethod.GET, request, OrderGetResponse.class);

        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
        
        OrderGetResponse orderGetResponse = response.getBody();

        Assertions.assertNotNull(orderGetResponse);
        Assertions.assertNotNull(orderGetResponse.getOrders());
        Assertions.assertFalse(orderGetResponse.getOrders().isEmpty());
    }

    @Test
    public void testPutOrderById() throws URISyntaxException {
        RestTemplate restTemplate = new RestTemplate();
        URI uri = new URI("http://localhost:" + serverPort + "/api/orders/" + createdOrder.getId());

        Order order = new Order();
        order.setStatus("Processing");
        order.setTotalAmount(11.0);

        HttpHeaders authHeaders = new HttpHeaders();
        authHeaders.set("Authorization", "Bearer " + sessionToken);

        HttpEntity<Order> request = new HttpEntity<>(order,authHeaders);
        ResponseEntity<OrderPutResponse> response = restTemplate.exchange(uri, HttpMethod.PUT, request, OrderPutResponse.class);

        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
        
        OrderPutResponse orderPutResponse = response.getBody();

        Assertions.assertNotNull(orderPutResponse);

        Order updatedOrder = orderPutResponse.getOrder();
        Assertions.assertEquals(createdOrder.getId(),updatedOrder.getId());
        Assertions.assertEquals(order.getStatus(), updatedOrder.getStatus());
        Assertions.assertEquals(order.getTotalAmount(), updatedOrder.getTotalAmount());

        createdOrder = updatedOrder;
    }

    @Test
    public void testGetOrderById() throws URISyntaxException {
        RestTemplate restTemplate = new RestTemplate();
        URI uri = new URI("http://localhost:" + serverPort + "/api/orders/" + createdOrder.getId());

        HttpHeaders authHeaders = new HttpHeaders();
        authHeaders.set("Authorization", "Bearer " + sessionToken);

        HttpEntity<HttpHeaders> request = new HttpEntity<>(authHeaders);
        ResponseEntity<OrderGetByIdResponse> response = restTemplate.exchange(uri, HttpMethod.GET, request, OrderGetByIdResponse.class);

        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());

        OrderGetByIdResponse responseBody = response.getBody();
        Assertions.assertNotNull(responseBody);

        Order retrievedOrder = responseBody.getOrder();

        Assertions.assertNotNull(retrievedOrder);
        Assertions.assertEquals(createdOrder, retrievedOrder);
    }

    @AfterAll
    public void cleanup() throws URISyntaxException, JSONException {
        RestTemplate restTemplate = new RestTemplate();

        // Delete Order by ID
        if (createdOrder != null) {
            URI orderUri = new URI("http://localhost:" + serverPort + "/api/orders/" + createdOrder.getId());
            HttpHeaders orderAuthHeaders = new HttpHeaders();
            orderAuthHeaders.set("Authorization", "Bearer " + sessionToken);

            HttpEntity<HttpHeaders> request = new HttpEntity<>(orderAuthHeaders);
            ResponseEntity<OrderDeleteByIdResponse> response = restTemplate.exchange(orderUri, HttpMethod.DELETE, request, OrderDeleteByIdResponse.class);

            Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
            OrderDeleteByIdResponse orderDeleteByIdResponse = response.getBody();
            Assertions.assertNotNull(orderDeleteByIdResponse);

            Long deletedOrderId = orderDeleteByIdResponse.getId();
            Assertions.assertEquals(createdOrder.getId(), deletedOrderId);
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
