package com.github.leloxo.bankserver.exception;

import com.github.leloxo.bankserver.exception.bankaccount.BankAccountNotFoundException;
import com.github.leloxo.bankserver.exception.bankaccount.BankAccountRepositoryException;
import com.github.leloxo.bankserver.exception.bankaccount.InsufficientBalanceException;
import com.github.leloxo.bankserver.exception.bankaccount.InvalidBankAccountDataException;
import com.github.leloxo.bankserver.exception.customer.CustomerNotFoundException;
import com.github.leloxo.bankserver.exception.customer.CustomerRepositoryException;
import com.github.leloxo.bankserver.exception.customer.InvalidCustomerDataException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {
    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    /**
     * Handles InvalidCustomerDataException.
     * @param ex The exception.
     * @return A ResponseEntity with an error message and HTTP status BAD_REQUEST.
     */
    @ExceptionHandler(InvalidCustomerDataException.class)
    public ResponseEntity<String> handleInvalidCustomerDataException(InvalidCustomerDataException ex) {
        logger.error("Invalid customer data error: " + ex.getMessage(), ex);
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ex.getMessage());
    }

    /**
     * Handles CustomerNotFoundException.
     * @param ex The exception.
     * @return A ResponseEntity with an error message and HTTP status NOT_FOUND.
     */
    @ExceptionHandler(CustomerNotFoundException.class)
    public ResponseEntity<String> handleCustomerNotFoundException(CustomerNotFoundException ex) {
        logger.error("Customer not found error: " + ex.getMessage(), ex);
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(ex.getMessage());
    }

    /**
     * Handles CustomerRepositoryException.
     * @param ex The exception.
     * @return A ResponseEntity with an error message and HTTP status INTERNAL_SERVER_ERROR.
     */
    @ExceptionHandler(CustomerRepositoryException.class)
    public ResponseEntity<String> handleCustomerRepositoryException(CustomerRepositoryException ex) {
        logger.error("Customer repository error: " + ex.getMessage(), ex);
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("An error occurred while processing your request.");
    }

    /**
     * Handles BankAccountRepositoryException.
     * @param ex The exception.
     * @return A ResponseEntity with an error message and HTTP status INTERNAL_SERVER_ERROR.
     */
    @ExceptionHandler(BankAccountRepositoryException.class)
    public ResponseEntity<String> handleBankAccountRepositoryException(BankAccountRepositoryException ex) {
        logger.error("Bank Account repository error: " + ex.getMessage(), ex);
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("An error occurred while processing your request.");
    }

    /**
     * Handles BankAccountNotFoundException.
     * @param ex The exception.
     * @return A ResponseEntity with an error message and HTTP status NOT_FOUND.
     */
    @ExceptionHandler(BankAccountNotFoundException.class)
    public ResponseEntity<String> handleBankAccountNotFoundException(BankAccountNotFoundException ex) {
        logger.error("Bank Account not found error: " + ex.getMessage(), ex);
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(ex.getMessage());
    }

    /**
     * Handles InsufficientBalanceException.
     * @param ex The exception.
     * @return A ResponseEntity with an error message and HTTP status BAD_REQUEST.
     */
    @ExceptionHandler(InsufficientBalanceException.class)
    public ResponseEntity<String> handleInsufficientBalanceException(InsufficientBalanceException ex) {
        logger.error("Insufficient balance error: " + ex.getMessage(), ex);
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ex.getMessage());
    }

    /**
     * Handles InvalidBankAccountDataException.
     * @param ex The exception.
     * @return A ResponseEntity with an error message and HTTP status BAD_REQUEST.
     */
    @ExceptionHandler(InvalidBankAccountDataException.class)
    public ResponseEntity<String> handleInvalidBankAccountDataException(InvalidBankAccountDataException ex) {
        logger.error("Invalid bank account data error: " + ex.getMessage(), ex);
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ex.getMessage());
    }

    /**
     * Handles all other exceptions.
     * @param ex The exception.
     * @return A ResponseEntity with a generic error message and HTTP status INTERNAL_SERVER_ERROR.
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleGenericException(Exception ex) {
        logger.error("An unexpected error occurred: " + ex.getMessage(), ex);
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("An unexpected error occurred.");
    }

}
