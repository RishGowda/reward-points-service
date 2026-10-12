package com.example.rewards.model;

import java.util.List;

/** Response containing reward summaries for all customers in the request. */
public record RewardCalculationResponse(List<CustomerReward> customers) {
}
