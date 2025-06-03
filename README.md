# Baraka Order Matching Engine
A simplified in-memory order matching engine for buy/sell orders, implemented in Java using Spring Boot. Supports placing market orders and retrieving them, with real-time matching logic and in-memory persistence.

---
## Running Locally 
### IDE: 
- Just click RUN on OrderMatchingApplication.java and service should be up
### Gradle
- Use command : 
```
  ./gradlew bootRun
```

The service will be up at : ```http://localhost:8080```

## POSTMAN COLLECTION
Is available under ```/resources``` folder.

## Testing
To run all tests (unit + integration):
```
./gradlew test
```