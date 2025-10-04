package com.yuziak.unitalktest;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@RestController
public class BetController {


    private final Map<Car, Integer> bets = new ConcurrentHashMap<>();

    @PostMapping("/bet")
    public String placeBet(@RequestParam String auto, @RequestParam int amount) {
        if (!Car.isValid(auto)) {
            return "Invalid auto";
        }
        Car car = Car.fromString(auto);
        bets.put(car, bets.getOrDefault(car, 0) + amount);
        return "Placed $" + amount + " on " + car.name();
    }


    @GetMapping("/stats")
    public String getStats(@RequestParam(required = false) String auto) {
        if (auto != null) {
            if (!Car.isValid(auto)) {
                return "Invalid auto";
            }
            Car car = Car.fromString(auto);
            int total = bets.getOrDefault(car, 0);
            return car.name() + ": $" + total;
        } else {
            StringBuilder sb = new StringBuilder();
            for (Map.Entry<Car, Integer> entry : bets.entrySet()) {
                sb.append(entry.getKey().name()).append(": $").append(entry.getValue()).append("\n");
            }
            return sb.toString();
        }
    }
}