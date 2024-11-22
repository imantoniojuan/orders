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
import com.anthony.orders.dtos.responses.CustomerGetByIdResponse;
import com.anthony.orders.dtos.responses.CustomerPutResponse;
import com.anthony.orders.dtos.responses.LoginResponse;
import com.anthony.orders.dtos.responses.RegisterResponse;
import com.anthony.orders.entities.Customer;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@SpringBootTest(classes = OrdersApplication.class, webEnvironment = WebEnvironment.DEFINED_PORT)
public class CustomerControllerTests {

    @LocalServerPort
    int serverPort = 8081;

    String testEmail = "test1@email.com";
    String testPwd = "pword124!";
    String sessionToken = "";
    Customer createdCustomer;

    @BeforeAll
    public void register() throws URISyntaxException {
        RestTemplate restTemplate = new RestTemplate();
        URI uri = new URI("http://localhost:" + serverPort + "/api/auth/register");

        RegisterRequest registerRequest = new RegisterRequest();
        registerRequest.setName("Unit Tester1");
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
    public void testPutCustomerById() throws URISyntaxException {
        RestTemplate restTemplate = new RestTemplate();
        URI uri = new URI("http://localhost:" + serverPort + "/api/customer/" + createdCustomer.getId());

        Customer customer = new Customer();
        customer.setName("Unit Tester2");
        customer.setEmail(testEmail);
        customer.setAddress("123 St Roads");
        customer.setContactNumber("+65999900002");

        HttpHeaders authHeaders = new HttpHeaders();
        authHeaders.set("Authorization", "Bearer " + sessionToken);

        HttpEntity<Customer> request = new HttpEntity<>(customer,authHeaders);
        ResponseEntity<CustomerPutResponse> response = restTemplate.exchange(uri, HttpMethod.PUT, request, CustomerPutResponse.class);

        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
        
        CustomerPutResponse customerPutResponse = response.getBody();

        Assertions.assertNotNull(customerPutResponse);

        Customer updatedCustomer = customerPutResponse.getCustomer();
        Assertions.assertEquals(createdCustomer.getId(),updatedCustomer.getId());
        Assertions.assertEquals(customer.getName(), updatedCustomer.getName());
        Assertions.assertEquals(customer.getAddress(), updatedCustomer.getAddress());
        Assertions.assertEquals(customer.getContactNumber(), updatedCustomer.getContactNumber());

        createdCustomer = updatedCustomer;
    }

    @Test
    public void testGetCustomerById() throws URISyntaxException {
        RestTemplate restTemplate = new RestTemplate();
        URI uri = new URI("http://localhost:" + serverPort + "/api/customer/" + createdCustomer.getId());

        HttpHeaders authHeaders = new HttpHeaders();
        authHeaders.set("Authorization", "Bearer " + sessionToken);

        HttpEntity<HttpHeaders> request = new HttpEntity<>(authHeaders);
        ResponseEntity<CustomerGetByIdResponse> response = restTemplate.exchange(uri, HttpMethod.GET, request, CustomerGetByIdResponse.class);

        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());

        CustomerGetByIdResponse responseBody = response.getBody();
        Assertions.assertNotNull(responseBody);

        Customer retrievedCustomer = responseBody.getCustomer();

        Assertions.assertNotNull(retrievedCustomer);
        Assertions.assertEquals(createdCustomer, retrievedCustomer);

    }

    @AfterAll
    public void cleanup() throws URISyntaxException, JSONException {
        RestTemplate restTemplate = new RestTemplate();
        URI uri = new URI("http://localhost:" + serverPort + "/api/customer/" + createdCustomer.getId());

        HttpHeaders authHeaders = new HttpHeaders();
        authHeaders.set("Authorization", "Bearer " + sessionToken);

        HttpEntity<HttpHeaders> request = new HttpEntity<>(authHeaders);
        ResponseEntity<CustomerDeleteByIdResponse> response = restTemplate.exchange(uri, HttpMethod.DELETE, request, CustomerDeleteByIdResponse.class);

        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());

        CustomerDeleteByIdResponse responseBody = response.getBody();
        Assertions.assertNotNull(responseBody);

        Long retrievedCustomerId = responseBody.getId();

        Assertions.assertNotNull(retrievedCustomerId);
        Assertions.assertEquals(retrievedCustomerId, createdCustomer.getId());
    }
}
