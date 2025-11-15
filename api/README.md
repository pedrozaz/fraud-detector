# Fraud Detection API

A secure, high-performance API for real-time financial fraud detection, built with a Java/Spring Boot backend and a Python/TensorFlow machine learning service.

## Features

**Secure Authentication:** JWT-based registration (/register) and login (/login).

**Protected Endpoints:** All detection endpoints are secured via Spring Security.

**AI-Powered Detection:** A POST /api/v1/detect endpoint that uses a Python ML service to score transactions.

**Database Persistence:** All transactions and their fraud scores are saved to a PostgreSQL database.

**API Documentation:** Fully documented and testable via Swagger UI (/swagger-ui.html).

## Tech Stack

**Core API:** Java 21, Spring Boot 3, Spring Security (JWT), Spring Data JPA, PostgreSQL

**ML Service:** Python 3, Flask, TensorFlow (Keras), Scikit-learn

### Running the Project

This system requires two services to be run.

1. Python ML Service (Port 5000)
   Navigate to the ml-model directory.

Create and activate a virtual environment:

```
python -m venv venv
source venv/bin/activate
```
Install dependencies:
```
pip install -r requirements.txt
```
_(If first time) Train the model:_
```
python train_model.py
```
Run the API server:
```
python app.py
```
2. Java Core API (Port 8080)


Set the required environment variables:

- **DB_URL** (e.g., jdbc:postgresql://localhost:5432/fraud_db)

- **DB_USER**

- **DB_PASS**

- **APPLICATION_SECRET_KEY**

Navigate to the Java API root directory.

Run the application:

```
./mvnw spring-boot:run
```

## API Usage

The easiest way to interact with the API is via the Swagger UI.

Swagger URL: http://localhost:8080/swagger-ui.html

### Standard Flow

Use POST /api/v1/auth/register to create an account.

Use POST /api/v1/auth/login to retrieve your JWT token.

Click the "Authorize" button on the Swagger UI and enter your token (e.g., Bearer <your-token>).

Use POST /api/v1/detect to submit transactions for analysis.