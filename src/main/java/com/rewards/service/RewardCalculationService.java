package com.rewards.service;

import org.springframework.stereotype.Service;

import com.rewards.model.CustomerReward;
import com.rewards.model.MonthlyReward;
import com.rewards.model.RewardCalculationRequest;
import com.rewards.model.RewardCalculationResponse;
import com.rewards.model.TransactionRequest;

import java.math.BigDecimal;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/** Applies the retailer's per-transaction reward rules and aggregates points. */
@Service
public class RewardCalculationService {

    private static final BigDecimal FIFTY = new BigDecimal("50");
    private static final BigDecimal ONE_HUNDRED = new BigDecimal("100");

    public RewardCalculationResponse calculate(RewardCalculationRequest request) {
        // TreeMaps give stable, ascending customer and month ordering.
        Map<String, Map<YearMonth, Long>> pointsByCustomer = new TreeMap<>();

        for (TransactionRequest transaction : request.transactions()) {
        	if(transaction == null) {
        		throw new IllegalArgumentException("transactions must not contain null items");
        	}
            long points = calculatePoints(transaction.amount());
            YearMonth month = YearMonth.from(transaction.transactionDate());

            pointsByCustomer
                    .computeIfAbsent(transaction.customerId().trim(), ignored -> new TreeMap<>())
                    .merge(month, points, Long::sum);
        }

        List<CustomerReward> customerRewards = new ArrayList<>();
        pointsByCustomer.forEach((customerId, monthlyPoints) -> {
            List<MonthlyReward> monthlyRewards = monthlyPoints.entrySet().stream()
                    .map(entry -> new MonthlyReward(entry.getKey(), entry.getValue()))
                    .toList();

            long total = monthlyRewards.stream().mapToLong(MonthlyReward::points).sum();
            customerRewards.add(new CustomerReward(customerId, monthlyRewards, total));
        });

        return new RewardCalculationResponse(List.copyOf(customerRewards));
    }

    /**
     * Points are calculated independently for each transaction:
     * 1 point for each whole dollar in the $50-$100 band, and
     * 2 points for each whole dollar above $100.
     */
    public long calculatePoints(BigDecimal amount) {
        if (amount == null || amount.signum() < 0) {
            throw new IllegalArgumentException("amount must be zero or greater");
        }

        long points = 0;
        if (amount.compareTo(FIFTY) > 0) {
            BigDecimal eligibleForOnePoint = amount.min(ONE_HUNDRED).subtract(FIFTY);
            points += eligibleForOnePoint.longValue(); // only complete dollars count
        }
        if (amount.compareTo(ONE_HUNDRED) > 0) {
            points += amount.subtract(ONE_HUNDRED).longValue() * 2;
        }
        return points;
    }
}
