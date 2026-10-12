package com.example.rewards.model;

import java.util.List;

/** Monthly reward breakdown and total for one customer. */
public record CustomerReward(String customerId, List<MonthlyReward> monthlyRewards, long totalPoints) {
}
