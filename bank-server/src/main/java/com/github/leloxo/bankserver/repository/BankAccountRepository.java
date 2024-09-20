package com.github.leloxo.bankserver.repository;

import com.github.leloxo.bankserver.exception.bankaccount.BankAccountRepositoryException;
import com.github.leloxo.bankserver.exception.customer.CustomerNotFoundException;
import com.github.leloxo.bankserver.exception.customer.CustomerRepositoryException;
import com.github.leloxo.bankserver.model.bankaccount.BankAccount;
import com.github.leloxo.bankserver.model.bankaccount.BankAccountDto;
import com.github.leloxo.bankserver.model.customer.Customer;
import com.github.leloxo.bankserver.model.util.DataConverter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
public class BankAccountRepository {
    private static final Logger logger = LoggerFactory.getLogger(BankAccountRepository.class);

    /**
     * Retrieves a list of all customers from the database.
     *
     * @return A list of all {@link BankAccountDto} objects.
     * @throws CustomerNotFoundException If the customer of the bank account is not found.
     * @throws CustomerRepositoryException If a database error occurs.
     */
    public List<BankAccountDto> getBankAccounts() throws CustomerNotFoundException {
        String query = "SELECT * FROM bank_account";
        List<BankAccountDto> bankAccounts = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                bankAccounts.add(getBankAccountDtoOfCustomerWithId(rs));
            }
            logger.info("Successfully retrieved {} bank accounts from the database.", bankAccounts.size());
        } catch (SQLException e) {
            logger.error("Database error while fetching all bank accounts: {}", e.getMessage(), e);
            throw new BankAccountRepositoryException("Failed to fetch all bank accounts", e);
        }
        return bankAccounts;
    }

    /**
     * Retrieves bank accounts by the customer ID from the database.
     *
     * @param id The ID of the customer.
     * @return A list of {@link BankAccountDto} objects associated with the customer ID.
     * @throws CustomerNotFoundException If the customer with the given ID is not found.
     * @throws CustomerRepositoryException If a database error occurs.
     */
    public List<BankAccountDto> getBankAccountsByCustomerId(Long id) throws CustomerNotFoundException {
        String query = "SELECT * FROM bank_account WHERE customer_id = ?";
        List<BankAccountDto> bankAccounts = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setLong(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    bankAccounts.add(getBankAccountDtoOfCustomerWithId(rs));
                }
                logger.info("Successfully retrieved {} bank accounts from the database.", bankAccounts.size());
            }
        } catch (SQLException e) {
            logger.error("Database error while searching for bank account with customer_id {}: {}", id, e.getMessage(), e);
            throw new CustomerRepositoryException("Failed to find bank account with customer_id: " + id, e);
        }
        return bankAccounts;
    }

    /**
     * Retrieves a bank account by the account number from the database.
     *
     * @param accountNumber The account number of the bank account.
     * @return An {@link Optional} containing the {@link BankAccountDto} if found.
     * @throws CustomerNotFoundException If the customer associated with the account is not found.
     * @throws CustomerRepositoryException If a database error occurs.
     */
    public Optional<BankAccountDto> getBankAccountByAccountNumber(String accountNumber) throws CustomerNotFoundException {
        String query = "SELECT * FROM bank_account WHERE account_number = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, accountNumber);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    BankAccountDto bankAccountDto = getBankAccountDtoOfCustomerWithId(rs);
                    logger.info("Bank account with account number {} found.", accountNumber);
                    return Optional.of(bankAccountDto);
                } else {
                    logger.warn("No account with account number {} found.", accountNumber);
                    return Optional.empty();
                }
            }
        } catch (SQLException e) {
            logger.error("Database error while searching for bank account with account_number {}: {}", accountNumber, e.getMessage(), e);
            throw new CustomerRepositoryException("Failed to find bank account with account_number: " + accountNumber, e);
        }
    }

    /**
     * Converts a ResultSet into a {@link BankAccountDto} object, retrieving the customer details.
     *
     * @param rs The {@link ResultSet} containing the bank account data.
     * @return A {@link BankAccountDto} object.
     * @throws CustomerNotFoundException If the customer associated with the bank account is not found.
     * @throws SQLException If there is a SQL error while retrieving the customer details.
     */
    private BankAccountDto getBankAccountDtoOfCustomerWithId(ResultSet rs) throws CustomerNotFoundException, SQLException {
        Long customerId = rs.getLong("customer_id");
        Optional<Customer> optionalCustomer = CustomerRepository.getCustomerById(customerId);
        Customer customer = optionalCustomer
                .orElseThrow(() -> new CustomerNotFoundException("No customer with id " + customerId + " was found"));

        return new BankAccountDto(
                rs.getLong("id"),
                rs.getString("account_number"),
                DataConverter.toCustomerDto(customer),
                rs.getBigDecimal("balance"),
                rs.getTimestamp("created_at").toLocalDateTime()
        );
    }

    /**
     * Saves a new bank account to the database.
     *
     * @param bankAccount The bank account to be saved.
     * @return The saved bank account with a generated ID.
     * @throws BankAccountRepositoryException If a database error occurs.
     */
    public BankAccount saveBankAccount(BankAccount bankAccount) {
        String query = "INSERT INTO bank_account (account_number, customer_id, balance, created_at) VALUES (?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query, PreparedStatement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, bankAccount.getAccountNumber());
            stmt.setLong(2, bankAccount.getCustomer().getId());
            stmt.setBigDecimal(3, bankAccount.getBalance());
            stmt.setTimestamp(4, Timestamp.valueOf(bankAccount.getCreatedAt()));

            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected > 0) {
                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        bankAccount.setId(generatedKeys.getLong(1));
                        logger.info("Successfully saved bank account with number: {}", bankAccount.getAccountNumber());
                    } else {
                        throw new SQLException("No rows affected, failed to create bank account.");
                    }
                }
            }
            return bankAccount;
        } catch (SQLException e) {
            logger.error("Database error while saving bank account with number {}: {}", bankAccount.getAccountNumber(), e.getMessage(), e);
            throw new BankAccountRepositoryException("Failed to save bank account with account number: " + bankAccount.getAccountNumber(), e);
        }
    }

    /**
     * Transfers money between two bank accounts.
     *
     * @param senderAccountNumber   The account number of the sender.
     * @param receiverAccountNumber The account number of the receiver.
     * @param amount                The amount to be transferred.
     * @throws CustomerRepositoryException If a database error occurs during the transaction.
     */
    public void transferMoney(String senderAccountNumber, String receiverAccountNumber, BigDecimal amount) {
        String removeMoneyQuery = "UPDATE bank_account SET balance = balance - ? WHERE account_number = ?";
        String addMoneyQuery = "UPDATE bank_account SET balance = balance + ? WHERE account_number = ?";
        String insertTransferQuery = "INSERT INTO transfer (sender_account_number, receiver_account_number, amount) VALUES (?, ?, ?)";

        Connection conn = null;
        try {
            conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false);

            // Remove money from sender
            try (PreparedStatement removeStmt = conn.prepareStatement(removeMoneyQuery)) {
                removeStmt.setBigDecimal(1, amount);
                removeStmt.setString(2, senderAccountNumber);
                int senderRowsAffected = removeStmt.executeUpdate();
                if (senderRowsAffected == 0) {
                    throw new SQLException("No rows affected, failed to remove money from sender's account.");
                }
            }

            // Add money to receiver
            try (PreparedStatement addStmt = conn.prepareStatement(addMoneyQuery)) {
                addStmt.setBigDecimal(1, amount);
                addStmt.setString(2, receiverAccountNumber);
                int receiverRowsAffected = addStmt.executeUpdate();
                if (receiverRowsAffected == 0) {
                    throw new SQLException("No rows affected, failed to add money to receiver's account.");
                }
            }

            // Insert transfer into transfer table
            try (PreparedStatement insertTransferStmt = conn.prepareStatement(insertTransferQuery)) {
                insertTransferStmt.setString(1, senderAccountNumber);
                insertTransferStmt.setString(2, receiverAccountNumber);
                insertTransferStmt.setBigDecimal(3, amount);
                int transferRowsAffected = insertTransferStmt.executeUpdate();
                if (transferRowsAffected == 0) {
                    throw new SQLException("No rows affected, failed to insert transfer record.");
                }
            }

            conn.commit();
            logger.info("Successfully transferred {}€ from {} to {}", amount, senderAccountNumber, receiverAccountNumber);
        } catch (SQLException e) {
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException rollbackEx) {
                    logger.error("Error during transaction rollback: {}", rollbackEx.getMessage());
                }
            }
            logger.error("Error during money transfer from {} to {}: {}", senderAccountNumber, receiverAccountNumber, e.getMessage());
            throw new CustomerRepositoryException("Failed to transfer money.", e);
        } finally {
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException closeEx) {
                    logger.error("Error closing connection: {}", closeEx.getMessage());
                }
            }
        }
    }

}
