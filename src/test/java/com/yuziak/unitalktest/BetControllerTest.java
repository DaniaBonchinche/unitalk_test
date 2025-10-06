package com.yuziak.unitalktest;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.web.servlet.MockMvc;

import java.lang.reflect.Field;
import java.util.Map;
import java.util.stream.IntStream;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(BetController.class)
public class BetControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private BetController betController;

    @AfterEach
    void clearBets() throws Exception {
        Field betsField = BetController.class.getDeclaredField("bets");
        betsField.setAccessible(true);
        Map<Car, Integer> bets = (Map<Car, Integer>) betsField.get(betController);
        bets.clear();
    }

    private void performPostBet(String auto, String amount, String expectedResponse) throws Exception {
        mockMvc.perform(post("/bet")
                        .param("auto", auto)
                        .param("amount", amount))
                .andExpect(status().isOk())
                .andExpect(content().string(expectedResponse));
    }

    private void performGetStats(String auto, String expectedResponsePart) throws Exception {
        mockMvc.perform(get("/stats")
                        .param("auto", auto))
                .andExpect(status().isOk())
                .andExpect(content().string(expectedResponsePart));
    }

    @Test
    void testPlaceBet_validCar() throws Exception {
        performPostBet("BMW", "100.50", "Placed $100.50 on BMW");
    }

    @Test
    void testPlaceBet_invalidCar() throws Exception {
        performPostBet("TESLA", "100", "Invalid auto");
    }

    @Test
    void testRejectNegativeBet() throws Exception {
        performPostBet("AUDI", "-50", "Bet amount must be positive");
    }

    @Test
    void testRejectZeroBet() throws Exception {
        performPostBet("HONDA", "0", "Bet amount must be positive");
    }

    @Test
    void testRejectBetAboveLimit() throws Exception {
        performPostBet("FERRARI", "1000000001", "Bet exceeds the limit of $1000000");
    }

    @Test
    void testRejectTotalExceedsLimit() throws Exception {
        for (int i = 0; i < 1000; i++) {
            performPostBet("BMW", "1000000", "Placed $1000000 on BMW");
        }
        performPostBet("BMW", "1", "Total bets for BMW exceed the limit of $1000000000");
    }

    @Test
    void testGetStatsForSpecificCar() throws Exception {
        performPostBet("AUDI", "50.25", "Placed $50.25 on AUDI");
        performGetStats("AUDI", "AUDI: $50.25");
    }

    @Test
    void testGetStatsAllCars() throws Exception {
        performPostBet("BMW", "200.10", "Placed $200.10 on BMW");
        performPostBet("FERRARI", "300.90", "Placed $300.90 on FERRARI");
        mockMvc.perform(get("/stats"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("BMW: $200.10")))
                .andExpect(content().string(containsString("FERRARI: $300.90")));
    }

    @Test
    void threadSafeTest() throws Exception {
        IntStream.range(0, 1000).parallel().forEach(i -> {
            try {
                performPostBet("BMW", "1", "Placed $1 on BMW");
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
        performGetStats("BMW", "BMW: $1000");
    }
}
