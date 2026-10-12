# Reward Points Service

Spring Boot REST API that calculates reward points for each customer by calendar month and customer total.

## Requirements

- JDK 21
- Maven 3.9+ (or use your IDE's Maven support)

## Reward rules

Points are calculated **per transaction**, not on the customer's combined monthly spend:

- No points for the first $50.
- 1 point for each whole dollar above $50 up to and including $100.
- 2 points for each whole dollar above $100.

Examples: `$50 = 0`, `$75 = 25`, `$100 = 50`, `$120 = 50 + (20 × 2) = 90`.

Fractional dollars are ignored when counting eligible dollars. For example, `$120.99` earns 90 points.

## Run

From the project root:

```bash
mvn clean test
mvn spring-boot:run
```

The API listens on `http://localhost:8080`.

## Endpoint

`POST /api/rewards/calculate`

Content-Type: `application/json`

Example request:

```json
{
  "transactions": [
    {"customerId": "C100", "transactionDate": "2026-01-05", "amount": 120.00},
    {"customerId": "C100", "transactionDate": "2026-01-20", "amount": 75.00},
    {"customerId": "C100", "transactionDate": "2026-02-02", "amount": 50.00},
    {"customerId": "C200", "transactionDate": "2026-01-10", "amount": 200.00},
    {"customerId": "C200", "transactionDate": "2026-03-14", "amount": 45.00}
  ]
}
```

Try it with curl:

```bash
curl -X POST http://localhost:8080/api/rewards/calculate \
  -H "Content-Type: application/json" \
  -d '{"transactions":[{"customerId":"C100","transactionDate":"2026-01-05","amount":120.00},{"customerId":"C100","transactionDate":"2026-01-20","amount":75.00},{"customerId":"C100","transactionDate":"2026-02-02","amount":50.00},{"customerId":"C200","transactionDate":"2026-01-10","amount":200.00},{"customerId":"C200","transactionDate":"2026-03-14","amount":45.00}]}'
```

## Expected result for the curl request

```json
{
  "customers": [
    {
      "customerId": "C100",
      "monthlyRewards": [
        {"month": "2026-01", "points": 115},
        {"month": "2026-02", "points": 0}
      ],
      "totalPoints": 115
    },
    {
      "customerId": "C200",
      "monthlyRewards": [
        {"month": "2026-01", "points": 250},
        {"month": "2026-03", "points": 0}
      ],
      "totalPoints": 250
    }
  ]
}
```

Months are derived from transaction dates; no month names or fixed three-month window are hard-coded. Months without any transactions are omitted. Each submitted transaction is validated, and malformed requests return HTTP 400.

## Project structure

```text
reward-points-service/
├── pom.xml
├── README.md
└── src
    ├── main
    │   ├── java/com/example/rewards
    │   │   ├── RewardPointsApplication.java
    │   │   ├── controller/RewardController.java
    │   │   ├── exception/ApiError.java
    │   │   ├── exception/GlobalExceptionHandler.java
    │   │   ├── model/
    │   │   └── service/RewardCalculationService.java
    │   └── resources/application.properties
    └── test/java/com/example/rewards
        ├── controller/RewardControllerIntegrationTest.java
        └── service/RewardCalculationServiceTest.java
```

## Submit to GitHub

Do not submit a ZIP. Extract the project locally, create a repository, and commit the source files:

```bash
git init
git add pom.xml README.md src
git commit -m "Implement customer reward points REST API"
git branch -M main
git remote add origin https://github.com/YOUR-USERNAME/YOUR-REPOSITORY.git
git push -u origin main
```

Do not commit `target/`, IDE files, credentials, tokens, or passwords.
=======
# reward-points-service
>>>>>>> b2233307bd45566d3dc47f24b3c1efac9b1c15d7
