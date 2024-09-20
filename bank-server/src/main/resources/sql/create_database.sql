CREATE DATABASE IF NOT EXISTS bank_db;

USE bank_db;

CREATE TABLE customer (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    first_name VARCHAR(50) NOT NULL,
    last_name VARCHAR(50) NOT NULL,
    email VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE bank_account (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    account_number VARCHAR(10) NOT NULL UNIQUE,
    customer_id BIGINT NOT NULL,
    balance DECIMAL(15, 2) NOT NULL DEFAULT 0.00,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (customer_id) REFERENCES customer(id) ON DELETE CASCADE 
);

CREATE TABLE transfer (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    sender_account_number VARCHAR(10) NOT NULL,
    receiver_account_number VARCHAR(10) NOT NULL,
    amount DECIMAL(15, 2) NOT NULL,
    transfer_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);