package com.github.leloxo.bankserver.service;

import com.github.leloxo.bankserver.exception.customer.CustomerNotFoundException;
import com.github.leloxo.bankserver.exception.customer.CustomerRepositoryException;
import com.github.leloxo.bankserver.exception.customer.InvalidCustomerDataException;
import com.github.leloxo.bankserver.model.bankaccount.BankAccountDto;
import com.github.leloxo.bankserver.model.customer.Customer;
import com.github.leloxo.bankserver.model.customer.CustomerDto;
import com.github.leloxo.bankserver.model.validation.ValidationType;
import com.github.leloxo.bankserver.model.validation.ValidationManager;
import com.github.leloxo.bankserver.repository.CustomerRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CustomerService {
    private static final Logger logger = LoggerFactory.getLogger(CustomerService.class);

    private final CustomerRepository customerRepository;
    private final ValidationManager validationManager;

    public CustomerService(CustomerRepository customerRepository, ValidationManager validationManager) {
        this.customerRepository = customerRepository;
        this.validationManager = validationManager;
    }

    /**
     * Retrieves all customers from the repository.
     *
     * @return A list of all customers.
     * @throws CustomerRepositoryException If an error occurs while retrieving customers.
     */
    public List<CustomerDto> getCustomers() {
        try {
            logger.info("Fetching all customers.");
            return customerRepository.getCustomers();
        } catch (CustomerRepositoryException e) {
            logger.error("Error while fetching customers: {}", e.getMessage(), e);
            throw e;
        }
    }

    /**
     * Creates a new customer and saves it to the database.
     *
     * @param firstName The first name of the customer.
     * @param lastName  The last name of the customer.
     * @param email     The email of the customer.
     * @return The newly created customer.
     * @throws InvalidCustomerDataException If the email is already in use.
     * @throws CustomerRepositoryException If an error occurs while saving the customer.
     */
    public Customer createCustomer(String firstName, String lastName, String email, String password) {
        validationManager.validate(ValidationType.NAME, firstName);
        validationManager.validate(ValidationType.NAME, lastName);
        validationManager.validate(ValidationType.EMAIL, email);
        validationManager.validate(ValidationType.PASSWORD, password);

        if (existsByEmail(email)) {
            logger.warn("Failed to create customer: Email {} is already in use.", email);
            throw new InvalidCustomerDataException("Email is already in use.");
        }

        Customer customer = new Customer(firstName, lastName, email, password);
        try {
            logger.info("Creating new customer with email: {}", email);
            return customerRepository.saveCustomer(customer);
        } catch (CustomerRepositoryException e) {
            logger.error("Error while creating customer with email {}: {}", email, e.getMessage(), e);
            throw e;
        }
    }

    /**
     * Authenticates a customer by validating the email and comparing the provided raw password with the stored password.
     *
     * @param email The email address of the customer attempting to authenticate.
     * @param rawPassword The raw password provided by the customer for authentication.
     * @return {@code true} if the provided password matches the stored password, {@code false} otherwise.
     * @throws CustomerNotFoundException If no customer with the provided email is found.
     * @throws InvalidCustomerDataException If the email validation fails.
     */
    public boolean authenticateCustomer(String email, String rawPassword) throws CustomerNotFoundException {
        validationManager.validate(ValidationType.EMAIL, email);
        Customer customer = getCustomerByEmail(email);
        return customer.checkPassword(rawPassword);
    }

    /**
     * Finds a customer by their email address.
     *
     * @param email The email of the customer to find.
     * @return The customer with the specified email.
     * @throws CustomerNotFoundException If no customer with the email is found.
     * @throws CustomerRepositoryException  If an error occurs during the search.
     */
    public Customer getCustomerByEmail(String email) throws CustomerNotFoundException {
        try {
            validationManager.validate(ValidationType.EMAIL, email);
            logger.info("Searching for customer with email: {}", email);
            return customerRepository.getCustomerByEmail(email)
                    .orElseThrow(() -> new CustomerNotFoundException("No customer with email " + email + " was found."));
        } catch (CustomerRepositoryException e) {
            logger.error("Error while searching for customer with email {}: {}", email, e.getMessage(), e);
            throw e;
        }
    }

    /**
     * Checks if a customer exists for a given email address.
     *
     * @param email The email to check.
     * @return true if the customer exists, false otherwise.
     */
    public boolean existsByEmail(String email) {
        Optional<Customer> optionalCustomer = customerRepository.getCustomerByEmail(email);
        return optionalCustomer.isPresent();
    }

    /**
     * Updates an existing customer.
     *
     * @param updatedCustomer The customer object with updated information.
     * @return The updated customer.
     * @throws CustomerNotFoundException If the customer does not exist.
     * @throws CustomerRepositoryException  If an error occurs during the update.
     */
    public Customer updateCustomer(String email, CustomerDto updatedCustomer) throws CustomerNotFoundException {
        try {
            validationManager.validate(ValidationType.EMAIL, email);
            logger.info("Updating customer with email: {}", email);
            if (!existsByEmail(email)) {
                logger.warn("Customer with email {} does not exist.", email);
                throw new CustomerNotFoundException("Customer does not exist.");
            }
            customerRepository.updateCustomer(email, updatedCustomer);
            return customerRepository.getCustomerByEmail(updatedCustomer.getEmail())
                    .orElseThrow(() -> new CustomerNotFoundException("Customer not found after update."));
        } catch (CustomerRepositoryException e) {
            logger.error("Error while updating customer with email {}: {}", email, e.getMessage(), e);
            throw e;
        }
    }

    /**
     * Deletes a customer by their email.
     *
     * @param email The email of the customer to delete.
     * @throws CustomerNotFoundException If the customer does not exist.
     * @throws CustomerRepositoryException  If an error occurs during the deletion.
     */
    public void deleteCustomer(String email) throws CustomerNotFoundException {
        try {
            validationManager.validate(ValidationType.EMAIL, email);
            logger.info("Attempting to delete customer with email: {}", email);
            if (!existsByEmail(email)) {
                logger.warn("Customer with email {} does not exist.", email);
                throw new CustomerNotFoundException("Customer does not exist.");
            }
            customerRepository.deleteCustomer(email);
        } catch (CustomerRepositoryException e) {
            logger.error("Error while deleting customer with email {}: {}", email, e.getMessage(), e);
            throw e;
        }
    }
}
