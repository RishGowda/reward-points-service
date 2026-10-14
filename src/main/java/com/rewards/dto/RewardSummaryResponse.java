package com.rewards.dto;

import java.util.List;

public record RewardSummaryResponse(Long customerId, String customerName, List<MonthlyRewardSummary> monthlyRewards,
		long totalPoints) {
}
