CREATE DATABASE expense_tracker;

USE expense_tracker;

CREATE TABLE expenses (
    id INT AUTO_INCREMENT PRIMARY KEY,
    user_id VARCHAR(255),
    amount DOUBLE,
    date DATE,
    category VARCHAR(255),
    description VARCHAR(255),
    FOREIGN KEY (user_id) REFERENCES users(username)
);

CREATE TABLE users (
    username VARCHAR(255) PRIMARY KEY,
    password VARCHAR(255),
    budget DOUBLE
);
select * from users

