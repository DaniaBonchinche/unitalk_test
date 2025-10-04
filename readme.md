#  UniTalkTest Betting API

A small Spring Boot REST project that demonstrates a simple betting system for cars.  
It supports placing bets, viewing statistics, and includes validation for bet limits and input correctness.

---

### API Overview

### `POST /bet`

Place a bet on a specific car.

**Parameters:**
| Name | Type | Required | Description |  
|------|------|-----------|-------------|  
| `auto` | `String` | ✅ | Car name (`FERRARI`, `BMW`, `AUDI`, `HONDA`) |  
| `amount` | `BigDecimal` | ✅ | Bet amount (> 0 and ≤ 1,000,000,000) |  

**Validation Rules**  
❌ Negative or zero bets are not allowed  
❌ A single bet cannot exceed 1,000,000,000  
❌ The total bets for a single car cannot exceed 1,000,000,000  
✅ Uses BigDecimal for precise calculations  

## Testing 
The project includes unit tests for controller:  
BetControllerTest — endpoint behavior tested via MockMvc  
Run tests:
`mvn test`

## Run Locally 
Build the project:
`mvn clean package`  
Start the Spring Boot application:
`mvn spring-boot:run`  
Test the API:  
curl -X POST "http://localhost:8080/bet?auto=BMW&amount=100.50"  
curl "http://localhost:8080/stats"   
