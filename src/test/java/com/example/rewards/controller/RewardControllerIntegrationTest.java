package com.example.rewards.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class RewardControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void calculatesRewardsThroughRestEndpoint() throws Exception {
        String request = """
                {
                  "transactions": [
                    {"customerId":"C100","transactionDate":"2026-01-05","amount":120.00},
                    {"customerId":"C100","transactionDate":"2026-01-20","amount":75.00},
                    {"customerId":"C100","transactionDate":"2026-02-02","amount":50.00},
                    {"customerId":"C200","transactionDate":"2026-01-10","amount":200.00}
                  ]
                }
                """;

        mockMvc.perform(post("/api/rewards/calculate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.customers[0].customerId").value("C100"))
                .andExpect(jsonPath("$.customers[0].monthlyRewards[0].points").value(115))
                .andExpect(jsonPath("$.customers[0].totalPoints").value(115))
                .andExpect(jsonPath("$.customers[1].customerId").value("C200"))
                .andExpect(jsonPath("$.customers[1].totalPoints").value(250));
    }

    @Test
    void returnsBadRequestForNegativeAmount() throws Exception {
        String request = """
                {"transactions":[
                  {"customerId":"C100","transactionDate":"2026-01-05","amount":-10}
                ]}
                """;

        mockMvc.perform(post("/api/rewards/calculate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Validation failed"));
    }

    @Test
    void returnsBadRequestForEmptyTransactions() throws Exception {
        mockMvc.perform(post("/api/rewards/calculate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                        	{
                        	 "transactions":[]
                        	}
                        	"""))
                .andExpect(status().isBadRequest());
    }
}
