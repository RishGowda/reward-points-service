# Reward Points Service

Spring Boot REST API for calculating customer reward points, monthly breakdowns, and total points.

## Technology
- Java 17
- Spring Boot 3.5.6
- Maven
- Spring Web and Validation
- Spring Data JPA
- H2 in-memory database
- JUnit 5 and Spring Boot Test

## Reward rules
Points are calculated per transaction:
- First $50: 0 points.
- Each whole dollar above $50 through $100: 1 point.
- Each whole dollar above $100: 2 points, plus the 50 points available in the lower band.

| Amount | Points |
|---:|---:|
| $45 | 0 |
| $50 | 0 |
| $60 | 10 |
| $75 | 25 |
| $100 | 50 |
| $120 | 90 |
| $200 | 250 |

Fractional eligible dollars are ignored (for example, $120.99 earns 90 points).

## API endpoints

### Calculate rewards from request transactions
`POST /api/rewards/calculate`

Example JSON:
```json
{
  "transactions": [
    {"customerId":"C100","transactionDate":"2026-06-15","amount":120},
    {"customerId":"C100","transactionDate":"2026-06-28","amount":75},
    {"customerId":"C100","transactionDate":"2026-07-10","amount":200},
    {"customerId":"C100","transactionDate":"2026-07-20","amount":45},
    {"customerId":"C100","transactionDate":"2026-08-05","amount":130},
    {"customerId":"C100","transactionDate":"2026-08-15","amount":60}
  ]
}
```

This example returns monthly points of 115, 250, and 120, totaling 485.

### Get rewards for all database customers
`GET /api/rewards`

### Get rewards for one customer
`GET /api/rewards/{customerId}`

Example response for `GET /api/rewards/1`:
```json
{
  "customerId": 1,
  "customerName": "Harish",
  "monthlyRewards": [
    {"year": 2026, "month": 6, "points": 115},
    {"year": 2026, "month": 7, "points": 250},
    {"year": 2026, "month": 8, "points": 120}
  ],
  "totalPoints": 485
}
```

## Project structure
- `controller/RewardController.java`: POST calculation endpoint.
- `controller/RewardQueryController.java`: GET endpoints.
- `service/RewardCalculationService.java`: request-based calculation.
- `service/RewardQueryService.java`: database-backed customer summaries.
- `entity/`: JPA customer and transaction entities.
- `repository/`: Spring Data repositories.
- `dto/`: response DTOs matching the reference response.
- `resources/schema.sql`: database tables.
- `resources/data.sql`: sample customer and transaction data.
- `resources/application.properties`: H2 and server configuration.

## Run
Requirements: JDK 17 and Maven 3.9+.

From the directory containing `pom.xml`:
```bash
mvn clean test
mvn spring-boot:run
```

The service runs at `http://localhost:8080`.

In Eclipse, use **Maven → Update Project**, then **Run As → Maven test**, then run `RewardPointsApplication.java`.

## Test with Postman
- `POST http://localhost:8080/api/rewards/calculate`
- `GET http://localhost:8080/api/rewards`
- `GET http://localhost:8080/api/rewards/1`

For POST requests, use `Content-Type: application/json` and Body → raw → JSON.

## H2 database
When enabled, the H2 console is at `http://localhost:8080/h2-console`.
- JDBC URL: `jdbc:h2:mem:rewardsdb`
- Username: `sa`
- Password: blank

The in-memory database is recreated when the application restarts.

## GitHub
Commit source files, `pom.xml`, `.gitignore`, and `README.md`. Do not commit `target/`, `bin/`, passwords, credentials, or tokens.
