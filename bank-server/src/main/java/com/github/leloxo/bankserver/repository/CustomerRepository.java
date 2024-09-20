package com.github.leloxo.bankserver.repository;

import com.github.leloxo.bankserver.exception.customer.CustomerRepositoryException;
import com.github.leloxo.bankserver.model.customer.Customer;
import com.github.leloxo.bankserver.model.customer.CustomerDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
public class CustomerRepository {
    private static final Logger logger = LoggerFactory.getLogger(CustomerRepository.class);

    /**
     * Retrieves a list of all customers from the database.
     *
     * @return A list of all customers.
     * @throws CustomerRepositoryException If a database error occurs.
     */
    public List<CustomerDto> getCustomers() {
        String query = "SELECT id, first_name, last_name, email, created_at FROM customer";
        List<CustomerDto> customers = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                CustomerDto customer = new CustomerDto(
                        rs.getLong("id"),
                        rs.getString("first_name"),
                        rs.getString("last_name"),
                        rs.getString("email"),
                        rs.getTimestamp("created_at").toLocalDateTime()
                );
                customers.add(customer);
            }
            logger.info("Successfully retrieved {} customers from the database.", customers.size());
        } catch (SQLException e) {
            logger.error("Database error while fetching all customers: {}", e.getMessage(), e);
            throw new CustomerRepositoryException("Failed to fetch all customers", e);
        }
        return customers;
    }

    /**
     * Saves a new customer to the database.
     *
     * @param customer The customer to be saved.
     * @return The saved customer with a generated ID.
     * @throws CustomerRepositoryException If a database error occurs.
     */
    public Customer saveCustomer(Customer customer) {
        String query = "INSERT INTO customer (first_name, last_name, email, password, created_at) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(query, PreparedStatement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, customer.getFirstName());
            stmt.setString(2, customer.getLastName());
            stmt.setString(3, customer.getEmail());
            stmt.setString(4, customer.getPassword());
            stmt.setTimestamp(5, Timestamp.valueOf(customer.getCreatedAt()));

            int rowsAffected = stmt.executeUpdate();

            if (rowsAffected > 0) {
                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        customer.setId(generatedKeys.getLong(1));
                        logger.info("Successfully saved customer with email: {}", customer.getEmail());
                    } else {
                        throw new SQLException("No rows affected, failed to save customer.");
                    }
                }
            }
            return customer;
        } catch (SQLException e) {
            logger.error("Database error while saving customer with email {}: {}", customer.getEmail(), e.getMessage(), e);
            throw new CustomerRepositoryException("Failed to save customer with email: " + customer.getEmail(), e);
        }
    }

    /**
     * Finds a customer by their email.
     *
     * @param email The email of the customer to be found.
     * @return An Optional containing the customer if found, or empty if not found.
     * @throws CustomerRepositoryException If a database error occurs.
     */
    public Optional<Customer> getCustomerByEmail(String email) {
        String query = "SELECT * FROM customer WHERE email = ?";
        try (Connection conn = DatabaseConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, email);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Customer customer = new Customer(
                            rs.getLong("id"),
                            rs.getString("first_name"),
                            rs.getString("last_name"),
                            rs.getString("email"),
                            rs.getString("password"),
                            rs.getTimestamp("created_at").toLocalDateTime()
                    );
                    logger.info("Customer with email {} found.", email);
                    return Optional.of(customer);
                } else {
                    logger.warn("No customer with email {} found.", email);
                    return Optional.empty();
                }
            }
        } catch (SQLException e) {
            logger.error("Database error while finding customer with email {}: {}", email, e.getMessage(), e);
            throw new CustomerRepositoryException("Failed to find customer with email: " + email, e);
        }
    }

    /**
     * Finds a customer by their id.
     *
     * @param id The id of the customer to be found.
     * @return An Optional containing the customer if found, or empty if not found.
     * @throws CustomerRepositoryException If a database error occurs.
     */
    public static Optional<Customer> getCustomerById(Long id) {
        String query = "SELECT * FROM customer WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setLong(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Customer customer = new Customer(
                            rs.getLong("id"),
                            rs.getString("first_name"),
                            rs.getString("last_name"),
                            rs.getString("email"),
                            rs.getString("password"),
                            rs.getTimestamp("created_at").toLocalDateTime()
                    );
                    logger.info("Customer with id {} found.", id);
                    return Optional.of(customer);
                } else {
                    logger.warn("No customer with id {} found.", id);
                    return Optional.empty();
                }
            }
        } catch (SQLException e) {
            logger.error("Database error while searching for customer with id {}: {}", id, e.getMessage(), e);
            throw new CustomerRepositoryException("Failed to find customer with id: " + id, e);
        }
    }

    /**
     * Updates an existing customer in the database.
     *
     * @param updatedCustomer The customer object containing updated details.
     * @throws CustomerRepositoryException If a database error occurs.
     */
    public void updateCustomer(String email, CustomerDto updatedCustomer) {
        String query = "UPDATE customer SET first_name = ?, last_name = ?, email = ? WHERE email = ?";
        try (Connection conn = DatabaseConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, updatedCustomer.getFirstName());
            stmt.setString(2, updatedCustomer.getLastName());
            stmt.setString(3, updatedCustomer.getEmail());
            stmt.setString(4, email);

            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected > 0) {
                logger.info("Successfully updated customer with email: {}", email);
            } else {
                throw new SQLException("No rows affected, failed to update customer.");
            }
        } catch (SQLException e) {
            logger.error("Database error while updating customer with ID {}: {}", updatedCustomer.getId(), e.getMessage(), e);
            throw new CustomerRepositoryException("Failed to update customer with ID: " + updatedCustomer.getId(), e);
        }
    }

    /**
     * Deletes a customer by their email.
     *
     * @param email The email of the customer to be deleted.
     * @throws CustomerRepositoryException If a database error occurs.
     */
    public void deleteCustomer(String email) {
        String query = "DELETE FROM customer WHERE email = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, email);
            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected > 0) {
                logger.info("Successfully deleted customer with email {}", email);
            } else {
                logger.warn("No customer with email {} found for deletion.", email);
            }
        } catch (SQLException e) {
            logger.error("Database error while deleting customer with email {}: {}", email, e.getMessage(), e);
            throw new CustomerRepositoryException("Failed to delete customer with email: " + email, e);
        }
    }
}
