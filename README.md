# ING Credit Module

## Overview
The ING Credit Module is a software solution designed to manage customer loans. It allows users to:

- Create loans for customers
- List existing loans
- Process loan payments
- List loan installments

This project is built with Java and Spring Boot and uses Maven for dependency management and builds.

## Features

- **Loan Creation**: Easily create loans for customers with necessary details.
- **Loan Listing**: Retrieve list of loans in the system by customerId. Also loans can be filtered with loanAmount, installmentAmount and isPaid parameters. Sorting by those parameters and pagination is also available.
- **Loan Payments**: Process payments for existing loans.
- **Loan Installment Listing**: Retrieve list of loan installments in the system by loan.
- **API Documentation**: Integrated Swagger UI for exploring and testing the API.
- **Authentication**: Secured with basic authentication (username and password).
- **In-Memory Database**: Uses H2 database; tables and dummy data are automatically created on application startup.

---

## Installation

To set up the ING Credit Module on your local machine, follow these steps:

### Prerequisites
- Java Development Kit (JDK) 17 or later
- Maven 3.6 or later

### Steps
1. Clone the repository:
   ```bash
   git clone <repository-url>
   cd CreditModule
   ```

2. Build the project using Maven:
   ```bash
   mvn clean install
   ```

3. Run the application:
   ```bash
   mvn spring-boot:run
   ```

The application should now be running locally on `http://localhost:8080`.

---

## Usage

The ING Credit Module exposes RESTful APIs for interacting with the system. Below are some examples of how to use these APIs with `curl`.

### Authentication
The APIs are secured using basic authentication. Use the `--user` flag in `curl` to pass the username and password. Replace `username` and `password` with `user` and `12345`.

### 1. Create a Loan
```bash
curl -X POST http://localhost:8080/api/v1/loans \
-H "Content-Type: application/json" \
-d '{"customerId":1, "loanAmount":1, "numberOfInstallment":6, "interestRate":0.1}' \
--user user:12345
```

```bash
{"id":20,"insertDate":"2025-01-24 14:10:08","customerId":1,"loanAmount":1.0,"numberOfInstallment":6}

```

### 2. List Loans
```bash
curl -X GET 'http://localhost:8080/api/v1/loans?customerId=1' \
--user user:12345
```

```bash
curl -X GET 'http://localhost:8080/api/v1/loans?customerId=1&isPaid=false' \
--user user:12345
```

```bash
curl -X GET 'http://localhost:8080/api/v1/loans?customerId=1&isPaid=false&pageSize=20&pageNumber=1&sort=+numberOfInstallment' \
--user user:12345
```

```bash
{
  "loans": [
    {
      "id": 19,
      "insertDate": "2025-01-24 13:51:54",
      "updateDate": "2025-01-24 13:51:54",
      "customerId": 1,
      "loanAmount": 400,
      "numberOfInstallment": 6,
      "isPaid": false
    }
  ],
  "paging": {
    "pageNumber": 1,
    "pageSize": 20,
    "totalNumberOfRecords": 1,
    "totalNumberOfPages": 1,
    "hasNextPage": false,
    "hasPreviousPage": false
  }
}
```

### 3. Make a Payment
```bash
curl -X POST http://localhost:8080/api/v1/loans/pay \
-H "Content-Type: application/json" \
-d '{"loanId":20, "paidAmount":10}' \
--user user:12345
```

```bash
{
  "loanId": 20,
  "paidInstallmentCount": 3,
  "totalAmountSpent": 0.54,
  "loanPaidCompletely": false
}
```

---

### 3. List Loan Installments
```bash
curl -X GET 'http://localhost:8080/api/v1/loans/20/installments' \
--user user:12345
```

```bash
curl -X GET 'http://localhost:8080/api/v1/loans/20/installments' \
--user user:12345
```{
  "loanId": 20,
  "loanInstallments": [
    {
      "id": 91,
      "insertDate": "2025-01-24 14:10:08",
      "updateDate": "2025-01-24 14:13:32",
      "amount": 0.18,
      "paidAmount": 0.18,
      "dueDate": "2025-02-01",
      "paymentDate": "2025-01-24 14:13:32",
      "isPaid": true
    },
    {
      "id": 92,
      "insertDate": "2025-01-24 14:10:08",
      "updateDate": "2025-01-24 14:13:32",
      "amount": 0.18,
      "paidAmount": 0.18,
      "dueDate": "2025-03-01",
      "paymentDate": "2025-01-24 14:13:32",
      "isPaid": true
    },
    {
      "id": 93,
      "insertDate": "2025-01-24 14:10:08",
      "updateDate": "2025-01-24 14:13:32",
      "amount": 0.18,
      "paidAmount": 0.18,
      "dueDate": "2025-04-01",
      "paymentDate": "2025-01-24 14:13:32",
      "isPaid": true
    },
    {
      "id": 94,
      "insertDate": "2025-01-24 14:10:08",
      "updateDate": "2025-01-24 14:10:08",
      "amount": 0.18,
      "paidAmount": 0,
      "dueDate": "2025-05-01",
      "paymentDate": null,
      "isPaid": false
    },
    {
      "id": 95,
      "insertDate": "2025-01-24 14:10:08",
      "updateDate": "2025-01-24 14:10:08",
      "amount": 0.18,
      "paidAmount": 0,
      "dueDate": "2025-06-01",
      "paymentDate": null,
      "isPaid": false
    },
    {
      "id": 96,
      "insertDate": "2025-01-24 14:10:08",
      "updateDate": "2025-01-24 14:10:08",
      "amount": 0.18,
      "paidAmount": 0,
      "dueDate": "2025-07-01",
      "paymentDate": null,
      "isPaid": false
    }
  ],
  "paging": {
    "pageNumber": 1,
    "pageSize": 20,
    "totalNumberOfRecords": 6,
    "totalNumberOfPages": 1,
    "hasNextPage": false,
    "hasPreviousPage": false
  }
}

---

---

## API Endpoints

### Loan Management
- `GET /api/v1/loans/` - List loans of a customer
- `GET /api/v1/loans/{loanId}/installments` - List loan installments of a loan
- `POST /api/v1/loans` - Create a new loan for a customer
- `POST /api/v1/loans/pay` - Pay a loan

---

## API Documentation

The ING Credit Module includes Swagger UI for exploring and testing the API. Once the application is running, navigate to:

[http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)

Here, you can view detailed API documentation and interact with the endpoints directly.

---

## Database Schema

The ING Credit Module uses an H2 in-memory database. Upon application startup, the following tables are created along with some dummy data:

### `customer` Table
```sql
CREATE TABLE IF NOT EXISTS customer
(
    id                BIGINT         NOT NULL AUTO_INCREMENT,
    idate             TIMESTAMP      NOT NULL,
    udate             TIMESTAMP DEFAULT NULL,
    name              VARCHAR(50)    NOT NULL,
    surname           VARCHAR(50)    NOT NULL,
    credit_limit      DECIMAL(15, 2) NOT NULL,
    used_credit_limit DECIMAL(15, 2) NOT NULL,
    PRIMARY KEY (id)
);
```

### `loan` Table
```sql
CREATE TABLE IF NOT EXISTS loan
(
    id                    BIGINT         NOT NULL AUTO_INCREMENT,
    idate                 TIMESTAMP      NOT NULL,
    udate                 TIMESTAMP DEFAULT NULL,
    customer_id           BIGINT         NOT NULL,
    loan_amount           DECIMAL(15, 2) NOT NULL,
    number_of_installment SMALLINT       NOT NULL,
    is_paid               BOOLEAN        NOT NULL,
    interest_rate         DECIMAL(15, 2) NOT NULL,
    PRIMARY KEY (id),
    FOREIGN KEY (customer_id) REFERENCES customer (id)
);
```

### `loan_installment` Table
```sql
CREATE TABLE IF NOT EXISTS loan_installment
(
    id           BIGINT         NOT NULL AUTO_INCREMENT,
    idate        TIMESTAMP      NOT NULL,
    udate        TIMESTAMP DEFAULT NULL,
    loan_id      BIGINT         NOT NULL,
    amount       DECIMAL(15, 2) NOT NULL,
    paid_amount  DECIMAL(15, 2) NOT NULL,
    due_date     DATE           NOT NULL,
    payment_date TIMESTAMP DEFAULT NULL,
    is_paid      BOOLEAN        NOT NULL,
    PRIMARY KEY (id),
    FOREIGN KEY (loan_id) REFERENCES loan (id)
);
```

---

## Project Structure

```plaintext
src/main/java
  |-- com.inghub.credit
       |-- config       # DB and Security Configuration
       |-- constant     # Constants
       |-- controller   # API controllers
       |-- domain       # Entity classes
       |-- exception    # Expcetion management
       |-- repository   # Data access
       |-- request      # Request and DTO classes
       |-- response     # Response and DTO classes
       |-- service      # Business logic
```

---
