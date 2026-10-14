package com.rewards.service;

import com.rewards.dto.MonthlyRewardSummary;
import com.rewards.dto.RewardSummaryResponse;
import com.rewards.entity.CustomerEntity;
import com.rewards.entity.TransactionEntity;
import com.rewards.repository.CustomerRepository;
import com.rewards.repository.TransactionRepository;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

@Service
public class RewardQueryService {
    private final CustomerRepository customerRepository;
    private final TransactionRepository transactionRepository;

    public RewardQueryService(CustomerRepository customerRepository,
                              TransactionRepository transactionRepository) {
        this.customerRepository = customerRepository;
        this.transactionRepository = transactionRepository;
    }

    public List<RewardSummaryResponse> getAllRewards() {
        return customerRepository.findAll().stream()
                .map(this::buildSummary)
                .toList();
    }

    public RewardSummaryResponse getRewardsByCustomer(Long customerId) {
        CustomerEntity customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Customer not found: " + customerId));
        return buildSummary(customer);
    }

    private RewardSummaryResponse buildSummary(CustomerEntity customer) {
        Map<YearMonth, Long> pointsByMonth = new TreeMap<>();
        List<TransactionEntity> transactions =
                transactionRepository.findByCustomerIdOrderByTransactionDateAsc(customer.getId());

        for (TransactionEntity transaction : transactions) {
            long points = calculatePoints(transaction.getAmount());
            YearMonth month = YearMonth.from(transaction.getTransactionDate());
            pointsByMonth.merge(month, points, Math::addExact);
        }

        List<MonthlyRewardSummary> monthlyRewards = new ArrayList<>();
        long total = 0L;
        for (Map.Entry<YearMonth, Long> entry : pointsByMonth.entrySet()) {
            YearMonth month = entry.getKey();
            long points = entry.getValue();
            monthlyRewards.add(new MonthlyRewardSummary(
                    month.getYear(), month.getMonthValue(), points));
            total = Math.addExact(total, points);
        }

        return new RewardSummaryResponse(
                customer.getId(), customer.getName(), List.copyOf(monthlyRewards), total);
    }

    private long calculatePoints(BigDecimal amount) {
        if (amount == null || amount.signum() < 0) {
            throw new IllegalArgumentException("Transaction amount must be zero or greater");
        }
        BigDecimal fifty = new BigDecimal("50");
        BigDecimal hundred = new BigDecimal("100");
        if (amount.compareTo(fifty) <= 0) {
            return 0L;
        }

        long points = amount.min(hundred).subtract(fifty)
                .setScale(0, RoundingMode.DOWN).longValueExact();

        if (amount.compareTo(hundred) > 0) {
            long aboveHundred = amount.subtract(hundred)
                    .setScale(0, RoundingMode.DOWN).longValueExact();
            points = Math.addExact(points, Math.multiplyExact(aboveHundred, 2L));
        }
        return points;
    }
}
