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
import com.anthony.orders.dtos.responses.ProductDeleteByIdResponse;
import com.anthony.orders.dtos.responses.ProductGetByIdResponse;
import com.anthony.orders.dtos.responses.ProductGetResponse;
import com.anthony.orders.dtos.responses.ProductPostResponse;
import com.anthony.orders.dtos.responses.ProductPutResponse;
import com.anthony.orders.dtos.responses.RegisterResponse;
import com.anthony.orders.entities.Customer;
import com.anthony.orders.entities.Product;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@SpringBootTest(classes = OrdersApplication.class, webEnvironment = WebEnvironment.DEFINED_PORT)
public class ProductControllerTests {

    @LocalServerPort
    int serverPort = 8081;

    String testEmail = "test4@email.com";
    String testPwd = "pword124!";
    String sessionToken = "";
    Customer createdCustomer;
    Product createdProduct;

    @BeforeAll
    public void register() throws URISyntaxException {
        RestTemplate restTemplate = new RestTemplate();
        URI uri = new URI("http://localhost:" + serverPort + "/api/auth/register");

        RegisterRequest registerRequest = new RegisterRequest();
        registerRequest.setName("Unit Tester4");
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
    public void testPostProduct() throws URISyntaxException {
        RestTemplate restTemplate = new RestTemplate();
        URI uri = new URI("http://localhost:" + serverPort + "/api/products");

        Product product = new Product();
        product.setUpc("012345678905");
        product.setName("Two Door Cabinet (Grey)");
        product.setDescription("Cabinet with doors, grey, 78x95 cm");

        HttpHeaders authHeaders = new HttpHeaders();
        authHeaders.set("Authorization", "Bearer " + sessionToken);

        HttpEntity<Product> request = new HttpEntity<>(product,authHeaders);
        ResponseEntity<ProductPostResponse> response = restTemplate.exchange(uri, HttpMethod.POST, request, ProductPostResponse.class);

        Assertions.assertEquals(HttpStatus.CREATED, response.getStatusCode());
        
        ProductPostResponse productPostResponse = response.getBody();

        Assertions.assertNotNull(productPostResponse);

        createdProduct = productPostResponse.getProduct();
        Assertions.assertNotNull(createdProduct.getId());
        Assertions.assertEquals(product.getUpc(), createdProduct.getUpc());
        Assertions.assertEquals(product.getName(), createdProduct.getName());
        Assertions.assertEquals(product.getDescription(), createdProduct.getDescription());
    }

    @Test
    public void testGetProduct() throws URISyntaxException {
        RestTemplate restTemplate = new RestTemplate();
        URI uri = new URI("http://localhost:" + serverPort + "/api/products");

        HttpHeaders authHeaders = new HttpHeaders();
        authHeaders.set("Authorization", "Bearer " + sessionToken);

        HttpEntity<HttpHeaders> request = new HttpEntity<>(authHeaders);
        ResponseEntity<ProductGetResponse> response = restTemplate.exchange(uri, HttpMethod.GET, request, ProductGetResponse.class);

        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
        
        ProductGetResponse productGetResponse = response.getBody();

        Assertions.assertNotNull(productGetResponse);
        Assertions.assertNotNull(productGetResponse.getProducts());
        Assertions.assertFalse(productGetResponse.getProducts().isEmpty());
    }

    @Test
    public void testPutProductById() throws URISyntaxException {
        RestTemplate restTemplate = new RestTemplate();
        URI uri = new URI("http://localhost:" + serverPort + "/api/products/" + createdProduct.getId());

        Product product = new Product();
        product.setUpc("012345678905");
        product.setName("Two Door Cabinet (White)");
        product.setDescription("Cabinet with doors, white, 78x95 cm");

        HttpHeaders authHeaders = new HttpHeaders();
        authHeaders.set("Authorization", "Bearer " + sessionToken);

        HttpEntity<Product> request = new HttpEntity<>(product,authHeaders);
        ResponseEntity<ProductPutResponse> response = restTemplate.exchange(uri, HttpMethod.PUT, request, ProductPutResponse.class);

        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
        
        ProductPutResponse productPutResponse = response.getBody();

        Assertions.assertNotNull(productPutResponse);

        Product updatedProduct = productPutResponse.getProduct();
        Assertions.assertEquals(createdProduct.getId(),updatedProduct.getId());
        Assertions.assertEquals(product.getUpc(), updatedProduct.getUpc());
        Assertions.assertEquals(product.getName(), updatedProduct.getName());
        Assertions.assertEquals(product.getDescription(), updatedProduct.getDescription());

        createdProduct = updatedProduct;
    }

    @Test
    public void testGetProductById() throws URISyntaxException {
        RestTemplate restTemplate = new RestTemplate();
        URI uri = new URI("http://localhost:" + serverPort + "/api/products/" + createdProduct.getId());

        HttpHeaders authHeaders = new HttpHeaders();
        authHeaders.set("Authorization", "Bearer " + sessionToken);

        HttpEntity<HttpHeaders> request = new HttpEntity<>(authHeaders);
        ResponseEntity<ProductGetByIdResponse> response = restTemplate.exchange(uri, HttpMethod.GET, request, ProductGetByIdResponse.class);

        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());

        ProductGetByIdResponse responseBody = response.getBody();
        Assertions.assertNotNull(responseBody);

        Product retrievedProduct = responseBody.getProduct();

        Assertions.assertNotNull(retrievedProduct);
        Assertions.assertEquals(createdProduct, retrievedProduct);
    }

    @AfterAll
    public void cleanup() throws URISyntaxException, JSONException {
        RestTemplate restTemplate = new RestTemplate();

        // Delete Product by ID
        if (createdProduct != null) {
            URI orderUri = new URI("http://localhost:" + serverPort + "/api/products/" + createdProduct.getId());
            HttpHeaders authHeaders = new HttpHeaders();
            authHeaders.set("Authorization", "Bearer " + sessionToken);

            HttpEntity<HttpHeaders> request = new HttpEntity<>(authHeaders);
            ResponseEntity<ProductDeleteByIdResponse> response = restTemplate.exchange(orderUri, HttpMethod.DELETE, request, ProductDeleteByIdResponse.class);

            Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
            ProductDeleteByIdResponse productDeleteByIdResponse = response.getBody();
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
