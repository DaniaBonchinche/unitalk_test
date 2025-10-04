package com.yuziak.unitalktest;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@RestController
public class BetController {

    private static final BigDecimal MAX_SINGLE_BET = new BigDecimal("1000000"); 
    private static final BigDecimal MAX_TOTAL_PER_CAR = new BigDecimal("1000000000");

    private final Map<Car, BigDecimal> bets = new ConcurrentHashMap<>();

    @PostMapping("/bet")
    public String placeBet(@RequestParam String auto, @RequestParam BigDecimal amount) {
        if (!Car.isValid(auto)) {
            return "Invalid auto";
        }

        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            return "Bet amount must be positive";
        }

        if (amount.compareTo(MAX_SINGLE_BET) > 0) {
            return "Bet exceeds the limit of $" + MAX_SINGLE_BET;
        }

        Car car = Car.fromString(auto);
        BigDecimal current = bets.getOrDefault(car, BigDecimal.ZERO);
        BigDecimal newTotal = current.add(amount);

        if (newTotal.compareTo(MAX_TOTAL_PER_CAR) > 0) {
            return "Total bets for " + car.name() + " exceed the limit of $" + MAX_TOTAL_PER_CAR;
        }

        bets.put(car, newTotal);
        return "Placed $" + amount + " on " + car.name();
    }

    @GetMapping("/stats")
    public String getStats(@RequestParam(required = false) String auto) {
        if (auto != null) {
            if (!Car.isValid(auto)) {
                return "Invalid auto";
            }
            Car car = Car.fromString(auto);
            BigDecimal total = bets.getOrDefault(car, BigDecimal.ZERO);
            return car.name() + ": $" + total;
        } else {
            StringBuilder sb = new StringBuilder();
            for (Map.Entry<Car, BigDecimal> entry : bets.entrySet()) {
                sb.append(entry.getKey().name())
                        .append(": $")
                        .append(entry.getValue())
                        .append("\n");
            }
            return sb.toString();
        }
    }
}
