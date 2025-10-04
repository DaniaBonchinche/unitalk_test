package com.yuziak.unitalktest;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.web.servlet.MockMvc;

import java.lang.reflect.Field;
import java.util.Map;

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

    private void performPostBet(String auto, int amount, String expectedResponse) throws Exception {
        mockMvc.perform(post("/bet")
                        .param("auto", auto)
                        .param("amount", String.valueOf(amount)))
                .andExpect(status().isOk())
                .andExpect(content().string(expectedResponse));
    }

    private void performGetStats(String auto, String expectedResponsePart) throws Exception {
        mockMvc.perform(get("/stats")
                        .param("auto", auto))
                .andExpect(status().isOk())
                .andExpect(content().string(expectedResponsePart));
    }

    private void performGetAllStatsCheck(String... expectedParts) throws Exception {
        var request = mockMvc.perform(get("/stats"))
                .andExpect(status().isOk());

        for (String part : expectedParts) {
            request.andExpect(content().string(containsString(part)));
        }
    }

    @Test
    void testPlaceBet_validCar() throws Exception {
        performPostBet("BMW", 100, "Placed $100 on BMW");
    }

    @Test
    void testPlaceBet_invalidCar() throws Exception {
        performPostBet("TESLA", 100, "Invalid auto");
    }

    @Test
    void testGetStatsForSpecificCar() throws Exception {
        performPostBet("AUDI", 50, "Placed $50 on AUDI");
        performGetStats("AUDI", "AUDI: $50");
    }

    @Test
    void testGetStatsAllCars() throws Exception {
        performPostBet("BMW", 200, "Placed $200 on BMW");
        performPostBet("FERRARI", 300, "Placed $300 on FERRARI");
        performGetAllStatsCheck("BMW: $200", "FERRARI: $300");
    }
}
