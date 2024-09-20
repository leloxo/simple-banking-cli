package com.github.leloxo.bankserver.controller;

import com.github.leloxo.bankserver.exception.customer.CustomerNotFoundException;
import com.github.leloxo.bankserver.exception.customer.InvalidCustomerDataException;
import com.github.leloxo.bankserver.model.customer.Customer;
import com.github.leloxo.bankserver.model.customer.CustomerDto;
import com.github.leloxo.bankserver.model.customer.LoginRequestPayload;
import com.github.leloxo.bankserver.model.util.DataConverter;
import com.github.leloxo.bankserver.service.CustomerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/customers")
public class CustomerController {
    private static final Logger logger = LoggerFactory.getLogger(CustomerController.class);
    private final CustomerService customerService;

    public CustomerController(CustomerService customerService) {
        this.customerService = customerService;
    }

    @GetMapping("/all")
    public ResponseEntity<List<CustomerDto>> getCustomers() {
        List<CustomerDto> customers = customerService.getCustomers();
        return ResponseEntity.status(HttpStatus.OK).body(customers);
    }

    @GetMapping("get/{email}")
    public ResponseEntity<CustomerDto> getCustomerByEmail(@PathVariable String email) throws CustomerNotFoundException {
        if (email == null || email.isEmpty()) {
            throw new InvalidCustomerDataException("Email cannot be null or empty.");
        }
        Customer customer = customerService.getCustomerByEmail(email);
        return ResponseEntity.ok(DataConverter.toCustomerDto(customer));
    }

    @PostMapping("/register")
    public ResponseEntity<CustomerDto> registerCustomer(@RequestBody Customer customer) { // TODO make own payload class
        if (customer == null) {
            throw new InvalidCustomerDataException("Customer data cannot be null.");
        }
        Customer createdCustomer = customerService.createCustomer(
                customer.getFirstName(),
                customer.getLastName(),
                customer.getEmail(),
                customer.getPassword()
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(DataConverter.toCustomerDto(createdCustomer));
    }

    @PostMapping("/login")
    public ResponseEntity<String> loginAsCustomer(@RequestBody LoginRequestPayload loginRequest) throws CustomerNotFoundException {
        if (loginRequest == null) {
            throw new InvalidCustomerDataException("Login request data cannot be null.");
        }
        boolean isAuthenticated = customerService.authenticateCustomer(loginRequest.getEmail(), loginRequest.getPassword());
        if (isAuthenticated) {
            logger.info("Login for customer with email {} successful.", loginRequest.getEmail());
            return ResponseEntity.ok("Login successful");
        } else {
            logger.info("Login for customer with email {} failed.", loginRequest.getEmail());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid password.");
        }
    }

    @PutMapping("/update/{email}")
    public ResponseEntity<CustomerDto> updateCustomer(@PathVariable String email,
                                                      @RequestBody CustomerDto updatedCustomerDto) throws CustomerNotFoundException {
        if (updatedCustomerDto == null) {
            throw new InvalidCustomerDataException("Customer data cannot be null.");
        }
        Customer updatedCustomer = customerService.updateCustomer(email, updatedCustomerDto);
        return ResponseEntity.status(HttpStatus.OK).body(DataConverter.toCustomerDto(updatedCustomer));
    }

    @DeleteMapping("/delete/{email}")
    public ResponseEntity<String> deleteCustomer(@PathVariable String email) throws CustomerNotFoundException {
        if (email == null || email.isEmpty()) {
            throw new InvalidCustomerDataException("Email cannot be null or empty.");
        }
        customerService.deleteCustomer(email);
        return ResponseEntity.ok("Successfully deleted customer with email: " + email);
    }

}
